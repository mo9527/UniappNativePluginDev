package com.wanyi.plugins.order;

import static com.wanyi.plugins.enums.OperationLogType.OPEN_GATE;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.wanyi.plugins.constants.GateConstants;
import com.wanyi.plugins.service.OperationLogService;
import com.wanyi.plugins.states.GateState;
import com.wanyi.plugins.enums.GateOrderEnum;
import com.wanyi.plugins.enums.SerialPortEnum;
import com.wanyi.plugins.model.Response;
import com.wanyi.plugins.serialport.SerialPortManager;

public class GateCommandExecutor {

    private static final String TAG = "GateCommandExecutor";
    private static final String OPEN = GateConstants.DOOR_OPEN;
    private static final String CLOSE = GateConstants.DOOR_CLOSE;
    private static final String PORT_NAME = SerialPortEnum._0.getPath();

    private static final int TIMEOUT = 15 * 1000;

    public static JSONObject openGate(Context context){
        String openGate1Command = GateOrderEnum.OPEN.getOrder();
        SerialPortManager portManager = SerialPortManager.getInstance();
        portManager.write(PORT_NAME, openGate1Command);
        Log.i(TAG, "写入开锁命令数据：" + openGate1Command);
        return Response.success();
    }

    /**
     * 检查门是否关闭
     * @return true 已关闭
     */
    public synchronized static boolean checkGateClosed(){
        long startTime = System.currentTimeMillis();
        do {
            int currentState = GateState.getCurrentState();
            if (currentState == GateState.STATE_CLOSE){
                return true;
            }
            try {
                String queryStatusOrder = GateOrderEnum.STATUS_CLOSE.getOrder();
                SerialPortManager portManager = SerialPortManager.getInstance();
                portManager.write(PORT_NAME, queryStatusOrder);

            } catch (Exception e) {
                // Ignore
            }
        }while (System.currentTimeMillis() - startTime < TIMEOUT);
        return false;
    }

}
