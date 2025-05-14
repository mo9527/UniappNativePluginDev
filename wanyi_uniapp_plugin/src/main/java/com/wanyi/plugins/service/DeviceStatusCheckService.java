package com.wanyi.plugins.service;

public class DeviceStatusCheckService {
    private static final String TAG = "DeviceStatusCheckService";

    private static DeviceStatusCheckService INSTANCE;

    public static DeviceStatusCheckService getInstance(){
        synchronized (DeviceStatusCheckService.class){
            if (INSTANCE == null){
                INSTANCE = new DeviceStatusCheckService();
            }
        }
        return INSTANCE;
    }
}
