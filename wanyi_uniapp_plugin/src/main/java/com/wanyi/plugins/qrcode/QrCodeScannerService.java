package com.wanyi.plugins.qrcode;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.superlead.sdk.Scanner;
import com.superlead.sdk.ScannerInstanceHolder;
import com.superlead.sdk.listener.BarcodeReceiver;
import com.superlead.sdk.service.BarcodeService;
import com.superlead.sdk.usb.UsbScannerFinder;

import java.util.List;
import java.util.Optional;

public class QrCodeScannerService {
    
    private static final String TAG = "QrCodeScannerService";

    private static BarcodeReceiver barcodeReceiver;

    private static Intent barcodeIntent;

    private static boolean isRunning = false;

    public static void start(Context context) {

        try {
            UsbScannerFinder canner = new UsbScannerFinder(context);
            List<Scanner> usbScanners = canner.list();
            if (usbScanners.isEmpty()) {
                Log.i(TAG, "UsbScannerFinder: 没有找到USB二维码扫描设备");
                return;
            }

            Optional<Scanner> scannerOpt = usbScanners.stream().filter(scanner -> scanner.getType().equalsIgnoreCase("usb")).findFirst();
            if (scannerOpt.isPresent()){
                Scanner scanner = scannerOpt.get();
                scanner.open();
                Log.i(TAG, "打开二维码扫描端口");
                ScannerInstanceHolder.INSTANCE.setScanner(scanner);
            }else {
                Log.i(TAG, "没有找到USB二维码扫描设备");
                return;
            }
            if (!isRunning){
                barcodeIntent = new Intent(context, BarcodeService.class);
                context.startService(barcodeIntent);
                barcodeReceiver = new BarcodeReceiver(new QrCodeDataListener(context), context);
                isRunning = true;
            }
        }catch (Exception e){
            Log.e(TAG, "startScanner error:" + e.getMessage(), e);
        }
    }

    public static void close(Context context) {
        try {
            context.unregisterReceiver(barcodeReceiver);
            context.stopService(barcodeIntent);
            ScannerInstanceHolder.INSTANCE.getScanner().close();
        }catch (Exception e){
            Log.e(TAG, "closeScanner error:" + e.getMessage(), e);
        }
    }
}
