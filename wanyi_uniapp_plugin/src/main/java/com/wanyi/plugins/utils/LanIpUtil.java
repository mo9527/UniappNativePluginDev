package com.wanyi.plugins.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.lztek.toolkit.AddrInfo;
import com.lztek.toolkit.Lztek;

import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 上位机 IP地址工具类
 */
public class LanIpUtil {

    /**
     * 上位机/本机ip
     * @param context
     * @return
     */
    public static String getUpIp(Context context) {
        try {
            ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network activeNetwork = connectManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectManager.getNetworkCapabilities(activeNetwork);
            boolean isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            if (isWifi){
                //获取wifi的ip地址
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();

                // 将整数形式的 IP 地址转换为点分十进制格式
                return String.format(
                        "%d.%d.%d.%d",
                        (ipAddress & 0xff),
                        (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff),
                        (ipAddress >> 24 & 0xff)
                );
            }
        } catch (Exception e) {
            Log.e("LanIpUtil", "获取Lan Ip出错", e);
        }

        return "";
    }

    /**
     * 获取机器人底盘ip
     * @return
     */
    public static String getRobotIP() {
        try {
            String url = "http://192.168.11.1:1448/api/core/system/v1/network/status";
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                String res = response.body().string();
                JSONObject resJson = JSONObject.parseObject(res);
                JSONObject networkstatus = resJson.getJSONObject("networkstatus");
                String ip = networkstatus.getString("ip");
                Log.i("getRobotIp", ip) ;
                return ip;
            } else {
                Log.e("LanIpUtil", "获取Lan Ip出错: " + response.body().string());
            }

        } catch (Exception e) {
            Log.e("LanIpUtil", "获取Lan Ip出错", e);
        }

        return "";
    }
}
