package com.wanyi.plugins.devices;

import android.content.Context;
import android.util.Log;

import com.wanyi.plugins.model.Device;

public abstract class AbstractBootDeviceChecker {
    public static final String TAG = "AbstractBootDeviceChecker";

    public static AbstractBootDeviceChecker[] getAllDeviceChecker() {
        try {
            return new AbstractBootDeviceChecker[]{
                    new MicrophoneChecker(),
                    new UsbCameraChecker(),
                    new QrCodeScannerChecker()
            };
        } catch (Exception e) {
            Log.i(TAG, "获取设备检查器失败: ", e);
        }
        return new AbstractBootDeviceChecker[0];
    }

    public abstract Device checkDeviceStatus(Context context);
}
