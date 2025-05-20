package com.wanyi.plugins.cache;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.wanyi.plugins.R;

public class LocalCache {
    private static final String TAG = "LocalCache";

    private final Context context;

    private SharedPreferences sharedPreferences;

    public LocalCache (Context context) {
        this.context = context.getApplicationContext();
    }

    //创建单例模式
    private static volatile LocalCache instance;

    public static LocalCache getInstance(Context context) {
        if (instance == null) {
            synchronized (LocalCache.class) {
                if (instance == null) {
                    instance = new LocalCache(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public synchronized void set(String key, String value){
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
        Log.i(TAG, "设置缓存：" + key + "=" + value);
    }

    public String get(String key){
        SharedPreferences sharedPref = getSharedPreferences();
        String value = sharedPref.getString(key, "");
        return value;
    }

    public synchronized void setInt(String key, int value){
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
        Log.i(TAG, "设置缓存：" + key + "=" + value);
    }

    public int getInt(String key){
        SharedPreferences sharedPref = getSharedPreferences();
        int value = sharedPref.getInt(key, 0);
        return value;
    }

    private SharedPreferences getSharedPreferences(){
        if (sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);;
        }
        return sharedPreferences;
    }
}
