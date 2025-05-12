package com.wanyi.plugins.devices;

import android.app.Service;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.wanyi.plugins.enums.DeviceStatus;
import com.wanyi.plugins.model.Device;

import java.util.ArrayList;
import java.util.List;

public class DeviceChecker extends Service {
    private static final String TAG = "DeviceMonitorService";
    private List<Device> devices = new ArrayList<>();
    private Handler handler = new Handler();
    private static final long CHECK_INTERVAL = 5000; // 每 5 秒检查一次

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化设备列表
        devices.add(new Device("usb_001", "USB"));
        devices.add(new Device("sensor_001", "Sensor"));
        devices.add(new Device("camera_001", "Camera"));
        Log.d(TAG, "Service created");
        startMonitoring();
    }

    private void startMonitoring() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkDevicesStatus();
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        }, CHECK_INTERVAL);
    }

    private void checkDevicesStatus() {
        for (Device device : devices) {
            // 模拟状态检测
            DeviceStatusSimulator.simulateStatus(device);
            Log.d(TAG, "Device: " + device.getId() + ", Status: " + device.getStatus() +
                    ", Reason: " + device.getFaultReason());

            // 如果故障，触发通知（可选）
            if (device.getStatus() == DeviceStatus.FAULT) {
                notifyFault(device);
            }
        }
    }

    private void notifyFault(Device device) {
        // 真实场景：发送通知到 UI 或 UniApp
        Log.w(TAG, "Fault detected on " + device.getId() + ": " + device.getFaultReason());
        // 可通过 Broadcast 或 UniApp 回调通知前端
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        Log.d(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
