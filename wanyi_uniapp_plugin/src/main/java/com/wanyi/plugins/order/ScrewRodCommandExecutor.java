package com.wanyi.plugins.order;

import android.content.Context;
import android.util.Log;

import com.lztek.toolkit.Lztek;
import com.lztek.toolkit.SerialPort;
import com.wanyi.plugins.cache.LocalCache;
import com.wanyi.plugins.constants.AppConstants;
import com.wanyi.plugins.constants.CacheConstants;
import com.wanyi.plugins.states.CargoPickupState;
import com.wanyi.plugins.states.ScrewRodState;
import com.wanyi.plugins.enums.ScrewRodOrderEnum;
import com.wanyi.plugins.enums.SerialPortEnum;
import com.wanyi.plugins.serialport.SerialPortManager;
import com.wanyi.plugins.utils.HexUtil;
import com.wanyi.plugins.utils.SerialPortUtils;

/**
 * 丝杆命令执行器
 */
public class ScrewRodCommandExecutor {

    private static final String TAG = "ScrewRodCommandExecutor";

    private static final String portName = SerialPortEnum._2.getPath();

    //超时时间
    private static long TIMEOUT = 15 * 1000;

    /**
     * 丝杆移动到指定楼层
     * @param context
     * @param floor
     * @return
     */
    public synchronized static void moveTo(Context context, int floor){
        if (floor < 1 || floor > AppConstants.MAX_FLOOR){
            Log.e(TAG, "楼层超出范围: " + floor);
            return;
        }
        Log.i(TAG, "丝杆去往第几层： " + floor);
        ScrewRodOrderEnum floorOrder = ScrewRodOrderEnum.getFloorOrder(floor);
        String command = floorOrder.getOrder();
        Log.i(TAG, "写入丝杆移动命令数据：" + command);

        SerialPortManager portManager = SerialPortManager.getInstance();
        portManager.write(portName, command);
        ScrewRodState.setCurrentState(ScrewRodState.STATE_MOVING);
    }

    public synchronized static void backToCurrentFloor(Context context){
        int floor = LocalCache.getInstance(context).getInt(CacheConstants.SCREW_ROD_CURRENT_FLOOR);
        Log.i(TAG, "丝杆出货后复位到当前层 " + floor);
        ScrewRodOrderEnum floorOrder = ScrewRodOrderEnum.getFloorOrder(floor);
        String command = floorOrder.getOrder();
        SerialPortManager portManager = SerialPortManager.getInstance();
        portManager.write(portName, command);
        ScrewRodState.setCurrentState(ScrewRodState.STATE_MOVING);
    }

    /**
     * 检查丝杆电机是否到达指定楼层
     * @param context
     * @param targetFloor
     * @return
     */
    public synchronized static boolean checkMotorArriveFloor(Context context, int targetFloor){
        SerialPort serialPort = null;
        try {
            Lztek lztek = Lztek.create(context);
            serialPort = lztek.openSerialPort(SerialPortEnum._2.getPath(), ScrewRodOrderEnum.BAUD, 8, 0, 1, 0);
            if (serialPort == null){
                return false;
            }

            ScrewRodOrderEnum checkMotorCurrentFloor = ScrewRodOrderEnum.SCREW_ROD_POSITION;
            ScrewRodOrderEnum targetMotorPosition = ScrewRodOrderEnum.getFloorPosition(targetFloor);
            String command = checkMotorCurrentFloor.getOrder();
            Log.i(TAG, "查询电机当前坐标命令：" + command);
            int dataLength = SerialPortUtils.write(serialPort, command);
            if (dataLength <= 0){
                return false;
            }
            long startTime = System.currentTimeMillis();

            do {
                byte[] result = SerialPortUtils.read(serialPort);
                if (result != null){
                    String s = HexUtil.hexString(result);
                    Log.i(TAG, "查询电机当前坐标反馈：" + s);

                    if (s.equalsIgnoreCase(targetMotorPosition.getOrder())){
                        Log.i(TAG, "丝杆电机到达指定目标floor: " + targetFloor);
                        return true;
                    }

                    if (s.contains(targetMotorPosition.getOrder().toLowerCase())){
                        Log.i(TAG, "丝杆电机已定位在指定目标floor: " + targetFloor);
                        return true;
                    }

                }else {
                    Log.i(TAG, "读取《查询电机当前坐标》为空");
                }
                Thread.sleep(1000);
            }while (System.currentTimeMillis() - startTime < TIMEOUT);


        }catch (Exception e){
            Log.e(TAG, "丝杆移动错误", e);
        }finally {
            if(serialPort != null) serialPort.close();
            Log.i(TAG, "关闭查询电机状态端口");
        }

        return false;
    }

    public synchronized static boolean checkArrivedAt(Context context, int floor){
        long startTime = System.currentTimeMillis();
        do {
            LocalCache localCache = LocalCache.getInstance(context);
            int currentFloor = localCache.getInt(CacheConstants.SCREW_ROD_CURRENT_FLOOR);
            if (currentFloor == floor){
                return true;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //ignore
            }
        }while (System.currentTimeMillis() - startTime < TIMEOUT);
        return false;
    }

    /**
     * 货物是否被拿走了？
     * @param context
     * @return
     */
    public synchronized static boolean checkCargoPickup(){
        long startTime = System.currentTimeMillis();
        do {
            int currentState = CargoPickupState.getCurrentState();
            if (currentState == CargoPickupState.EMPTY){
                return true;
            }
        }while (System.currentTimeMillis() - startTime < TIMEOUT);
        return false;
    }

    /**
     * 步进电机当前坐标
     * @param context
     * @return
     */
    public synchronized static boolean getScrewRodCurrentPosition(Context context){
        SerialPortManager manager = SerialPortManager.getInstance();
        manager.write(portName, ScrewRodOrderEnum.SCREW_ROD_POSITION.getOrder());
        return false;
    }
}
