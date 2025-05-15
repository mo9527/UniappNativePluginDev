package com.wanyi.plugins.cache;

import android.content.Context;
import android.util.Log;

import com.wanyi.plugins.constants.CacheConstants;

public class CargoCacheOperator {
    public static final String TAG = "CargoCache";

    //单例
    private static CargoCacheOperator instance;
    private static LocalCache localCache;

    //创建单例模式
    public static CargoCacheOperator getInstance(Context context) {
        if (instance == null) {
            synchronized (CargoCacheOperator.class) {
                if (instance == null) {
                    instance = new CargoCacheOperator();
                    localCache = LocalCache.getInstance(context);
                }
            }
        }
        return instance;
    }

    public void resetCargoStock(int total, int eachFloorStock) {
        localCache.setInt(CacheConstants.F1_STOCK_LEFT, eachFloorStock);
        localCache.setInt(CacheConstants.F2_STOCK_LEFT, eachFloorStock);
        localCache.setInt(CacheConstants.F3_STOCK_LEFT, eachFloorStock);
        localCache.setInt(CacheConstants.F4_STOCK_LEFT, eachFloorStock);
        localCache.setInt(CacheConstants.F5_STOCK_LEFT, eachFloorStock);
        localCache.setInt(CacheConstants.F6_STOCK_LEFT, eachFloorStock);
        localCache.setInt(CacheConstants.F7_STOCK_LEFT, eachFloorStock);
        localCache.setInt(CacheConstants.CARGO_STOCK_TOTAL, total);
        localCache.setInt(CacheConstants.CARGO_STOCK_LEFT, total);
        localCache.setInt(CacheConstants.X_EACH_FLOOR_STOCK, eachFloorStock);
    }

    //货物减少

    /**
     *
     * @param floor
     * @return true 丝杆下移一格
     */
    public boolean cargoDecrease(int floor) {
        if (floor < 1 || floor > 7) {
            throw new RuntimeException("楼层号超出范围: " + floor);
        }
        try {
            // 减少总库存
            int totalLeft = getSafeInteger(localCache.getInt(CacheConstants.CARGO_STOCK_LEFT), 0);
            if (totalLeft <= 0) {
                throw new RuntimeException("总库存已空");
            }
            localCache.setInt(CacheConstants.CARGO_STOCK_LEFT, totalLeft - 1);

            // 获取当前楼层库存键, 往下递减楼层
            String currentFloorKey = getFloorKey(floor);
            String nextFloorKey = getFloorKey(floor - 1);

            // 获取当前楼层库存
            int currentFloorStock = getSafeInteger(localCache.getInt(currentFloorKey), 0);

            if (currentFloorStock > 0) {
                // 当前楼层有库存，减少当前楼层库存
                localCache.setInt(currentFloorKey, currentFloorStock - 1);
                return false;
            } else if (!nextFloorKey.isEmpty()) {
                // 当前楼层无库存，减少下一层楼库存
                int nextFloorStock = getSafeInteger(localCache.getInt(nextFloorKey), 0);
                if (nextFloorStock > 0) {
                    localCache.setInt(nextFloorKey, nextFloorStock - 1);
                    return true;
                } else {
                    throw new RuntimeException("第 " + floor + " 层及其以下楼层库存已空");
                }
            } else {
                throw new RuntimeException("所有楼层库存已空");
            }
        } catch (Exception e) {
            Log.e(TAG, "货物减少失败", e);
            throw e;
        }
    }

    // 根据楼层获取对应的库存键
    private String getFloorKey(int floor) {
        switch (floor) {
            case 1: return CacheConstants.F1_STOCK_LEFT;
            case 2: return CacheConstants.F2_STOCK_LEFT;
            case 3: return CacheConstants.F3_STOCK_LEFT;
            case 4: return CacheConstants.F4_STOCK_LEFT;
            case 5: return CacheConstants.F5_STOCK_LEFT;
            case 6: return CacheConstants.F6_STOCK_LEFT;
            case 7: return CacheConstants.F7_STOCK_LEFT;
            default: return "";
        }
    }

    // 安全地将字符串转换为整数，若为空或无效则返回默认值
    private int getSafeInteger(int value, int defaultValue) {
        try {
            if (value == 0 || value < 0 ) {
                return defaultValue;
            }
            return value;
        }catch (Exception e){
            return defaultValue;
        }
    }
}
