package com.wanyi.plugins.serialport;

import android.content.Context;
import android.util.Log;

import com.wanyi.plugins.cache.LocalCache;
import com.wanyi.plugins.constants.CacheConstants;
import com.wanyi.plugins.states.CargoPickupState;
import com.wanyi.plugins.states.CargoPusherState;
import com.wanyi.plugins.states.ScrewRodState;
import com.wanyi.plugins.enums.ScrewRodOrderEnum;
import com.wanyi.plugins.utils.HexUtil;


/**
 * com2串口数据监听，用于仓门控制
 */
public class SerialPortCom2DataCallback implements SerialPortManager.SerialDataCallback {
    private static final String TAG = "Com2串口数据监听器";

    private final Context context;

    private LocalCache localCache;

    public SerialPortCom2DataCallback(Context context) {
        this.context = context;
        localCache = LocalCache.getInstance(context.getApplicationContext());
    }

    @Override
    public void onDataReceived(String port, byte[] data) {
        String msg = HexUtil.hexString(data);
        Log.i(TAG, "串口 "+port+" 监听器收到数据: " + msg);
        if (msg.isEmpty()){
            return;
        }
        msg = msg.toUpperCase();

        //丝杆命令反馈
        if(msg.length() > 33){
            int arriveFloor = getArriveFloor(msg);
            if (arriveFloor > -1){
                ScrewRodState.setCurrentState(ScrewRodState.STATE_ARRIVED);
                Log.i(TAG, "丝杆当前层数: " + arriveFloor);
                localCache.setInt(CacheConstants.SCREW_ROD_CURRENT_FLOOR, arriveFloor);
            }
        }else {
            //货道命令返回
            if (msg.contains("0291000294D10D0A")){
                //货道停止
                Log.i(TAG, "货道停止");
                CargoPusherState.setCurrentState(CargoPusherState.STATE_STOP);
            } else if (msg.contains("02A08020000432A80D0A")) {
                //有货
                Log.i(TAG, "货栏中有货");
                CargoPickupState.setCurrentState(CargoPickupState.FULL);
            }else if (msg.contains("02A0801000043DA80D0A")){
                //无货
                Log.i(TAG, "货栏中无货");
                CargoPickupState.setCurrentState(CargoPickupState.EMPTY);
            }
        }
    }


    @Override
    public void onError(String port, String error) {
        Log.e(TAG, "串口 "+port+" 监听器出错: " + error);
    }

    private int getArriveFloor(String msg){
        for (int i = 1; i <= 8; i++) {
            ScrewRodOrderEnum floorPositionHex = ScrewRodOrderEnum.getFloorPosition(i);
            if (msg.contains(floorPositionHex.getOrder().toUpperCase())){
                return i;
            }
        }
        return -1;
    }
}
