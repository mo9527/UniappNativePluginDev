package com.wanyi.plugins.order;

import static com.wanyi.plugins.R.string.env;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.wanyi.plugins.R;
import com.wanyi.plugins.cache.CargoCacheOperator;
import com.wanyi.plugins.cache.LocalCache;
import com.wanyi.plugins.constants.CacheConstants;
import com.wanyi.plugins.dao.PickupCodeDao;
import com.wanyi.plugins.database.AppDatabase;
import com.wanyi.plugins.states.CargoPusherState;
import com.wanyi.plugins.entity.PickupCode;

import java.util.List;

public class CargoMachineCommandExecutor {
    private static final String TAG = "CargoMachineCommandExecutor";

    private PickupCodeDao pickupCodeDao;

    private static volatile CargoMachineCommandExecutor instance;

    public static synchronized CargoMachineCommandExecutor getInstance() {
        if (instance == null){
            synchronized (CargoMachineCommandExecutor.class) {
                if (instance == null) {
                    instance = new CargoMachineCommandExecutor();
                }
            }
        }
        return instance;
    }

    /**
     * 执行取货操作
     * 逻辑：
     * 所有的操作逻辑均在一个阻塞线程中执行，进来新的取货码则不执行任何逻辑
     * 1. 查询数据库，判断取货码是否存在，不存在则返回
     * 2. 判断当前层是否还有货，如果没有货，则丝杆移动到下一层
     * 3. 获取当前层剩余货量，如果还有货，则直接取货，否则，丝杆移动到下一层，再取货
     * 4. 当前货物取货成功后，本层货物数量-1，如果当前层货物数量为0，则丝杆移动到下一层，再取货
     * @param context
     */
    public synchronized void beginPickup(Context context, String code) {
        if (code == null || "".equals(code)){
            Log.e(TAG, "取货码为空");
            return;
        }

        //清除code的换行符\n \t 等
        code = code.replaceAll("\t", "");
        code = code.replaceAll("\n", "");
        code = code.trim();

        String finalCode = code;
        try {
            AppDatabase appDatabase = AppDatabase.getDatabase(context);
            pickupCodeDao = appDatabase.pickupCodeDao();
            List<PickupCode> all = pickupCodeDao.selectAll();
            if (!all.isEmpty()){
                Log.i(TAG, "取货码列表：" + JSON.toJSONString(all));
            }
            PickupCode oneEntity = pickupCodeDao.selectByCode(finalCode);
            if (oneEntity == null) {
                Log.e(TAG, "取货码不存在");
                return;
            }
            if (oneEntity.getStatus() == 1){
                Log.e(TAG, "取货码已使用");
                return;
            }

            Log.i(TAG, "开始执行取货操作，取货码：" + finalCode);

            LocalCache localCache = LocalCache.getInstance(context);
            int floor = localCache.getInt(CacheConstants.SCREW_ROD_CURRENT_FLOOR);

            //是否去往下一层（上层已经空了？）
            CargoCacheOperator cargoCacheOperator = CargoCacheOperator.getInstance(context);
            boolean goingDownNextFloor = cargoCacheOperator.cargoDecrease(floor);

            if (goingDownNextFloor){
                floor = floor - 1;
                boolean yMoveOrderOk = ScrewRodCommandExecutor.moveTo(context, floor);
                Log.i(TAG, "丝杆到达目标楼层：" + floor + " 结果：" + yMoveOrderOk);
                if (!yMoveOrderOk) return;

                boolean arrived = ScrewRodCommandExecutor.checkArrivedAt(context, floor);
                if (arrived){
                    localCache.setInt(CacheConstants.SCREW_ROD_CURRENT_FLOOR, floor);
                    CargoMoveCommandExecutor.cargoPush(context, floor);
                }
            }else {
                Log.i(TAG, "当前层货物数量充足，直接取货: " + floor);
                CargoMoveCommandExecutor.cargoPush(context, floor);
            }



            long startTime = System.currentTimeMillis();
            do {
                //等待货道停止
                if (CargoPusherState.getCurrentState() == CargoPusherState.STATE_STOP){
                    //抬升货物到最上层
                    ScrewRodCommandExecutor.moveTo(context, 8);
                    boolean arrived = ScrewRodCommandExecutor.checkArrivedAt(context, 8);
                    if (arrived){
                        GateCommandExecutor.openGate(context);
                        boolean cargoPickupB = ScrewRodCommandExecutor.checkCargoPickup();
                        if (cargoPickupB){
                            boolean gateClosed = GateCommandExecutor.checkGateClosed();
                            if (gateClosed){
                                //返回当前层(等一等再回)
                                Thread.sleep(3000);
                                ScrewRodCommandExecutor.moveTo(context, floor);
                                break;
                            }
                        }
                    }
                }
                Thread.sleep(1000);
            } while (System.currentTimeMillis() - startTime < 30 * 1000);

            oneEntity.setStatus(1);

            String env = context.getResources().getString(R.string.env);
            if ("prod".equals(env)){
                pickupCodeDao.updateRecord(oneEntity);
            }
            PickupCode newEntity = pickupCodeDao.selectByCode(finalCode);
            Log.i(TAG, "取货码状态：" + JSON.toJSONString(newEntity));
        }catch (Exception e){
            Log.e(TAG, "执行取货操作失败", e);
        }
    }

    /**
     * 丝杆复位，到达最上层
     * @param context
     */
    public synchronized void resetPosition(Context context) {
        ScrewRodCommandExecutor.moveTo(context, 7);
    }
}
