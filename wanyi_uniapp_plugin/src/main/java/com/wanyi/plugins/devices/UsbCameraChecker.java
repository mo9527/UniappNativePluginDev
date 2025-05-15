package com.wanyi.plugins.devices;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.wanyi.plugins.enums.DeviceStatus;
import com.wanyi.plugins.model.Device;

import java.util.HashMap;

public class UsbCameraChecker extends AbstractBootDeviceChecker{
    public static final String TAG = "UsbCameraChecker";

    private final String DISPLAY_NAME = "USB摄像头";

    private static UsbCameraChecker INSTANCE;
    public static UsbCameraChecker getInstance() {
        if (INSTANCE == null){
            synchronized (UsbCameraChecker.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UsbCameraChecker();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Device checkDeviceStatus(Context context) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        Device result = new Device("UsbCamera", "USB", DISPLAY_NAME);
        for (UsbDevice device : deviceList.values()) {
            for (int i = 0; i < device.getInterfaceCount(); i++) {
                UsbInterface intf = device.getInterface(i);
                if (intf.getInterfaceClass() == UsbConstants.USB_CLASS_VIDEO) {
                    // 找到了UVC类设备（USB 摄像头）
                    result.setStatus(DeviceStatus.NORMAL);
                    result.setFaultReason("");
                    return result;
                }
            }
        }
        result.setFaultReason("未找到USB摄像头");
        result.setStatus(DeviceStatus.FAULT);
        return result;
    }
}
