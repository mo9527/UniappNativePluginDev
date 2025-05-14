package com.wanyi.plugins.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.wanyi.plugins.cache.LocalCache;
import com.wanyi.plugins.constants.CacheConstants;
import com.wanyi.plugins.dao.PickupCodeDao;
import com.wanyi.plugins.database.AppDatabase;
import com.wanyi.plugins.entity.PickupCode;
import com.wanyi.plugins.model.CargoStockVo;
import com.wanyi.plugins.model.Device;
import com.wanyi.plugins.model.Response;
import com.wanyi.plugins.socket.WebsocketServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 货物数量以及取货码数量监听服务
 */
public class CargoStockMonitorService {
    private static final String TAG = "CargoStockMonitorService";

    private PickupCodeDao pickupCodeDao;

    private Handler handler = new Handler(Looper.myLooper());

    private static final long TASK_INTERVAL = 10;

    public CargoStockMonitorService(){}

    public static CargoStockMonitorService getInstance(){
        return new CargoStockMonitorService();
    }


    public void init(Context context) {
        Log.d(TAG, "CargoStockMonitorService created");
        AppDatabase database = AppDatabase.getDatabase(context.getApplicationContext());
        pickupCodeDao = database.pickupCodeDao();
        Runnable task = new Runnable(){

            @Override
            public void run() {
                sendStockMessage(context);
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(task, 3, TASK_INTERVAL, TimeUnit.SECONDS);
    }



    private void sendStockMessage(Context context){
        int codeLeft = pickupCodeDao.countOfUnUsed();
        int codeUsed = pickupCodeDao.countOfUsed();;
        LocalCache localCache = LocalCache.getInstance(context.getApplicationContext());
        int cargoLeft = localCache.getInt(CacheConstants.CARGO_STOCK_LEFT);

        CargoStockVo  vo = new CargoStockVo(codeLeft, cargoLeft, codeUsed);

        WebsocketServer.sendMessage(Response.success(vo).toJSONString());
    }
}
