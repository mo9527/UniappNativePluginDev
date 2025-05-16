package com.wanyi.plugins.qrcode;

import android.content.Context;
import android.util.Log;

import com.superlead.sdk.Scanner;
import com.superlead.sdk.ScannerInstanceHolder;
import com.superlead.sdk.listener.BarcodeEventListener;
import com.wanyi.plugins.cache.CargoCacheOperator;
import com.wanyi.plugins.cache.LocalCache;
import com.wanyi.plugins.enums.ScannerCommands;
import com.wanyi.plugins.order.CargoMachineCommandExecutor;
import com.wanyi.plugins.order.ScrewRodCommandExecutor;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QrCodeDataListener implements BarcodeEventListener {

    private static final String TAG = "QrCodeDataListener";

    private Context mContext;

    private LocalCache localCache;

    private ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

    public QrCodeDataListener() {
        Log.i(TAG, "QrCodeListener init");
    }

    public QrCodeDataListener(Context context) {
        Log.i(TAG, "初始化二维码扫描监听器");
        this.mContext = context;
        localCache = new LocalCache(context);
    }


    @Override
    public void onDataReceived(byte[] data) {
        Log.i(TAG, "QrCodeListener onReceive");
        if (data != null && data.length > 0) {
            String message = new String(data, Charset.forName("ISO-8859-1"));
            Log.i(TAG, "QrCodeListener onReceive:" + message);

            threadExecutor.execute(() -> {
                //暂停扫码
                try {
                    pauseScanner();
                    CargoMachineCommandExecutor executor = CargoMachineCommandExecutor.getInstance();
                    executor.beginPickup(mContext, message);
                }catch (Exception e){
                    Log.e(TAG, "pauseScanner error:" + e.getMessage(), e);
                }finally {
                    startScanner();
                }
            });

        }
    }

    private void pauseScanner(){
        //暂停扫码
        try {
            Scanner scanner = ScannerInstanceHolder.INSTANCE.getScanner();
            scanner.write(ScannerCommands.CMD_UNTRIGGER_SCAN.getCmd());
            Log.i(TAG, "暂停扫码功能");
            localCache.set("scanner_pause", "true");
        }catch (Exception e){
            Log.e(TAG, "pauseScanner error:" + e.getMessage(), e);
        }
    }

    private void startScanner(){
        //暂停扫码
        try {
            Thread.sleep(1000);
            Scanner scanner = ScannerInstanceHolder.INSTANCE.getScanner();
            scanner.write(ScannerCommands.CMD_TRIGGER_SCAN.getCmd());
            Log.i(TAG, "开启触发");
        }catch (Exception e){
            Log.e(TAG, "startScanner error:" + e.getMessage(), e);
        }
    }
}