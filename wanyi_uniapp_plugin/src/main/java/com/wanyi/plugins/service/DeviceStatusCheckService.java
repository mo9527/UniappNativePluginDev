package com.wanyi.plugins.service;

import android.content.Context;
import android.util.Log;

import com.wanyi.plugins.devices.AbstractBootDeviceChecker;
import com.wanyi.plugins.enums.SerialPortEnum;
import com.wanyi.plugins.model.Device;
import com.wanyi.plugins.serialport.checker.SerialPortChecker;

import java.util.ArrayList;
import java.util.List;

public class DeviceStatusCheckService {
    private static final String TAG = "DeviceStatusCheckService";

    private static DeviceStatusCheckService INSTANCE;

    public static DeviceStatusCheckService getInstance(){
        if (INSTANCE == null){
            synchronized (DeviceStatusCheckService.class){
                INSTANCE = new DeviceStatusCheckService();
            }
        }
        return INSTANCE;
    }

    private static List<Device> comDeviceList = new ArrayList<>();
    static {
        comDeviceList.add(new Device(SerialPortEnum._0.getName(), SerialPortEnum._0.getPath(), "仓门锁控设备"));
        comDeviceList.add(new Device(SerialPortEnum._2.getName(), SerialPortEnum._2.getPath(), "丝杆与货道设备"));
    }

    public List<Device> checkDeviceStatus(Context context){
        List<Device> checkResult = new ArrayList<>();
        try {
            for (Device device : comDeviceList){
                SerialPortChecker.checkDevice(device, context.getApplicationContext());
                checkResult.add(device);
            }

            for (AbstractBootDeviceChecker deviceChecker : AbstractBootDeviceChecker.getAllDeviceChecker()){
                Device device = deviceChecker.checkDeviceStatus(context.getApplicationContext());
                if (device != null){
                    checkResult.add(device);
                }
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage(), e);
        }

        return checkResult;
    }
}
