package com.wanyi.plugins.devices;

import android.content.Context;

import com.superlead.sdk.Scanner;
import com.superlead.sdk.ScannerInstanceHolder;
import com.wanyi.plugins.enums.DeviceStatus;
import com.wanyi.plugins.model.Device;

public class QrCodeScannerChecker extends AbstractBootDeviceChecker{

    public static final String TAG = "QrCodeScannerChecker";

    private final String DISPLAY_NAME = "二维码扫描器";

    private static QrCodeScannerChecker INSTANCE;
    public static QrCodeScannerChecker getInstance() {
        if (INSTANCE == null){
            synchronized (QrCodeScannerChecker.class) {
                if (INSTANCE == null) {
                    INSTANCE = new QrCodeScannerChecker();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Device checkDeviceStatus(Context context) {
        Device device = new Device("QR_CODE_SCANNER", "USB", DISPLAY_NAME);
        device.setFaultReason("二维码读取器未连接");
        Scanner scanner = ScannerInstanceHolder.INSTANCE.getScanner();
        if (scanner != null){
            device.setStatus(DeviceStatus.NORMAL);
            device.setFaultReason("");
            return device;
        }
        return device;
    }
}
