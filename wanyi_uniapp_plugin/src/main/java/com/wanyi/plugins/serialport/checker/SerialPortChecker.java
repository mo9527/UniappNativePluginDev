package com.wanyi.plugins.serialport.checker;

import android.content.Context;
import android.util.Log;

import com.wanyi.plugins.enums.DeviceStatus;
import com.wanyi.plugins.model.Device;
import com.wanyi.plugins.serialport.SerialPortManager;

public class SerialPortChecker {
    private static final String TAG = "SerialPortChecker";

    public static void checkDevice(Device device, Context context) {
        SerialPortManager serialManager = SerialPortManager.getInstance();
        try {
            if (!serialManager.isRunning(device.getSerialPort())) {
                serialManager.startListening(context, device.getSerialPort());
            }

            // 发送测试命令
            serialManager.write(device.getSerialPort(), "AT");

            Thread.sleep(100); // 等待 100ms
            device.setStatus(DeviceStatus.NORMAL);
            device.setFaultReason("");
            Log.d(TAG, "设备正常: " + device.getId());
        } catch (Exception e) {
            device.setStatus(DeviceStatus.FAULT);
            device.setFaultReason("串口异常: " + e.getMessage());
            Log.e(TAG, "检查设备失败: " + device.getId(), e);
        }
    }

}
