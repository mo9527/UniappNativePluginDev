package com.wanyi.plugins.serialport;

import android.content.Context;
import android.util.Log;

import com.wanyi.plugins.states.GateState;
import com.wanyi.plugins.utils.HexUtil;


/**
 * com0串口数据监听，用于仓门控制
 */
public class SerialPortCom0DataCallback implements SerialPortManager.SerialDataCallback {
    private static final String TAG = "SerialPortCom0DataCallback";

    private final Context context;

    public SerialPortCom0DataCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onDataReceived(String port, byte[] data) {
        String msg = HexUtil.hexString(data);
        Log.i(TAG, "串口 "+port+" 监听器收到数据: " + msg);
        if (msg.isEmpty()){
            return;
        }
        msg = msg.toUpperCase();
        if (msg.contains("80010100B3")){
            Log.i(TAG, "开门状态: " + msg);
            GateState.setCurrentState(GateState.STATE_OPEN);
        } else if (msg.contains("8001010080")) {
            Log.i(TAG, "关门状态: " + msg);
            GateState.setCurrentState(GateState.STATE_CLOSE);
        }
    }

    @Override
    public void onError(String port, String error) {

    }
}
