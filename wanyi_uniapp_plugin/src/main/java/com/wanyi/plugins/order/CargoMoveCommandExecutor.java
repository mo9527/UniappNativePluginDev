package com.wanyi.plugins.order;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.wanyi.plugins.states.CargoPusherState;
import com.wanyi.plugins.enums.ScrewRodOrderEnum;
import com.wanyi.plugins.enums.SerialPortEnum;
import com.wanyi.plugins.model.Response;
import com.wanyi.plugins.serialport.SerialPortManager;

/**
 * 货杆控制器
 */
public class CargoMoveCommandExecutor {

    private static final String TAG = "CargoPushCommandExecutor";

    private static final String PORT_NAME = SerialPortEnum._2.getPath();

    /**
     * 货道移动
     * @param context
     * @param floor
     * @return
     */
    public synchronized static JSONObject cargoPush(Context context, int floor){
        SerialPortManager portManager = SerialPortManager.getInstance();

        Log.i(TAG, "在第几层推出货物： " + floor);
        //todo 根据floor选择对应的货道
        portManager.write(PORT_NAME, ScrewRodOrderEnum.CARGO_F1_MOVE.getOrder());
        CargoPusherState.setCurrentState(CargoPusherState.STATE_MOVING);
        return Response.success();
    }

    /**
     * 查询货道状态
     * @param context
     * @param floor
     * @return true 停止 false 移动中
     */
//    public synchronized static boolean cargoPusherStatus(Context context, int floor){
//        SerialPort serialPort = null;
//        try {
//            Lztek lztek = Lztek.create(context);
//            serialPort = lztek.openSerialPort(SerialPortEnum._2.getPath(), ScrewRodOrderEnum.BAUD, 8, 0, 1, 0);
//            if (serialPort == null){
//                Log.e(TAG, "打开串口失败");
//                return false;
//            }
//
//            //货道1开始移动
//            ScrewRodOrderEnum moveCargoF1 = ScrewRodOrderEnum.CARGO_F1_MOVE;
//            String command = moveCargoF1.getOrder();
//            Log.i(TAG, "写入2串口货道Hex数据：" + command);
//            int dataLength = SerialPortUtils.write(serialPort, command);
//            if (dataLength <= 0){
//                Log.e(TAG, "货道移动失败");
//                return false;
//            }
//            Log.i(TAG, "读取货道移动反馈");
//            long startTime = System.currentTimeMillis();
//
//
//            do {
//                byte[] result = SerialPortUtils.read(serialPort);
//                if (result != null){
//                    String s = HexUtil.hexString(result);
//                    Log.i(TAG, "读取货道串口Hex数据：" + s);
//                    //"8a0101008a"
//                    if (s.equalsIgnoreCase(moveCargoF1.getOrderOkRes())){
//                        return Response.success();
//                    }
//                }
//            }while (System.currentTimeMillis() - startTime < 5000);
//
//
//        }catch (Exception e){
//            Log.e(TAG, "货道移动错误", e);
//            return Response.fail();
//        }finally {
//            if(serialPort != null) serialPort.close();
//            Log.i(TAG, "关闭2串口");
//        }
//    }
}
