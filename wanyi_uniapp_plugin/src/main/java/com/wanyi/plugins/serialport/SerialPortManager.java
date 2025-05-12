package com.wanyi.plugins.serialport;

import android.content.Context;
import android.util.Log;

import com.lztek.toolkit.Lztek;
import com.lztek.toolkit.SerialPort;
import com.wanyi.plugins.enums.SerialPortEnum;
import com.wanyi.plugins.utils.HexUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SerialPortManager {
    private static final String TAG = "SerialPortManager";
    private static final int BAUD_RATE = 9600;

    // 单例实例
    private static volatile SerialPortManager instance;
    private Map<String, SerialPort> serialPorts = new HashMap<>();
    private Map<String, Thread> readThreads = new HashMap<>();
    private Map<String, AtomicBoolean> isRunningFlags = new HashMap<>();
    private Map<String, SerialDataCallback> callbacks = new HashMap<>();
    private Map<String, OutputStream> outputStreams = new HashMap<>();

    public interface SerialDataCallback {
        void onDataReceived(String port, byte[] data);
        void onError(String port, String error);
    }


    private SerialPortManager() {}


    public static SerialPortManager getInstance() {
        if (instance == null) {
            synchronized (SerialPortManager.class) {
                if (instance == null) {
                    instance = new SerialPortManager();
                }
            }
        }
        return instance;
    }


    public void setCallback(String port, SerialDataCallback callback) {
        callbacks.put(port, callback);
    }


    public synchronized void startListening(Context context, String portName) {
        if (isRunningFlags.containsKey(portName) && isRunningFlags.get(portName).get()) {
            Log.w(TAG, "串口 " + portName + " 已在监听中");
            return;
        }

        try {

            Lztek lztek = com.lztek.toolkit.Lztek.create(context);
            SerialPort serialPort = lztek.openSerialPort(portName, BAUD_RATE, 8, 0, 1, 0);
            if (serialPort == null) {
                Log.e(TAG, "无法打开串口 " + portName);
                if (callbacks.containsKey(portName)) {
                    callbacks.get(portName).onError(portName, "无法打开串口 " + portName);
                }
                return;
            }
            serialPorts.put(portName, serialPort);
            outputStreams.put(portName, serialPort.getOutputStream());
            isRunningFlags.put(portName, new AtomicBoolean(true));
            Log.i(TAG, "串口 " + portName + " 已打开，开始监听...");

            Thread readThread = new Thread(() -> {
                SerialDataCallback serialDataCallback = callbacks.get(portName);
                try {
                    InputStream input = serialPort.getInputStream();
                    byte[] buffer = new byte[1024];
                    while (isRunningFlags.get(portName).get() && !Thread.interrupted()) {
                        int len = input.read(buffer);
                        if (len > 0) {
                            byte[] data = java.util.Arrays.copyOfRange(buffer, 0, len);
                            if (callbacks.containsKey(portName)) {
                                if (serialDataCallback != null){
                                    serialDataCallback.onDataReceived(portName, data);
                                }

                            }
                        }
                    }
                } catch (Exception e) {
                    if (isRunningFlags.get(portName).get()) {
                        Log.e(TAG, "串口 " + portName + " 读取失败: " + e.getMessage());
                        if (callbacks.containsKey(portName)) {
                            serialDataCallback.onError(portName, "读取失败: " + e.getMessage());
                        }
                    }
                }
            });
            readThreads.put(portName, readThread);
            readThread.start();
        } catch (Exception e) {
            Log.e(TAG, "串口 " + portName + " 监听失败: " + e.getMessage());
            if (callbacks.containsKey(portName)) {
                callbacks.get(portName).onError(portName, "监听失败: " + e.getMessage());
            }
        }
    }


    public synchronized void write(String portName, String data) {
        if (!serialPorts.containsKey(portName) || !isRunningFlags.get(portName).get()) {
            Log.e(TAG, "串口 " + portName + " 未打开，无法写入");
            if (callbacks.containsKey(portName)) {
                callbacks.get(portName).onError(portName, "串口未打开，无法写入");
            }
            return;
        }

        try {
            byte[] hexBytes = HexUtil.hexBytes(data);
            OutputStream output = outputStreams.get(portName);
            output.write(hexBytes);
            output.flush();
            Log.d(TAG, "串口 " + portName + " 写入数据: " + data);
        } catch (Exception e) {
            Log.e(TAG, "串口 " + portName + " 写入失败: " + e.getMessage());
            if (callbacks.containsKey(portName)) {
                callbacks.get(portName).onError(portName, "写入失败: " + e.getMessage());
            }
        }
    }


    public synchronized void stopListening(String portName) {
        if (isRunningFlags.containsKey(portName)) {
            isRunningFlags.get(portName).set(false);
        }
        if (readThreads.containsKey(portName)) {
            readThreads.get(portName).interrupt();
            readThreads.remove(portName);
        }
        if (serialPorts.containsKey(portName) && serialPorts.get(portName) != null) {
            serialPorts.get(portName).close();
            serialPorts.remove(portName);
            outputStreams.remove(portName);
            Log.i(TAG, "串口 " + portName + " 已关闭");
        }
    }


    public synchronized void stopAllListening() {
        for (String portName : serialPorts.keySet()) {
            stopListening(portName);
        }
        callbacks.clear();
    }


    public boolean isRunning(String portName) {
        return isRunningFlags.containsKey(portName) && isRunningFlags.get(portName).get();
    }

    public void autoReconnect(Context context){
        for (String portName : serialPorts.keySet()) {
            try {
                byte[] hexBytes = HexUtil.hexBytes("AT");
                OutputStream output = outputStreams.get(portName);
                output.write(hexBytes);
                output.flush();
            } catch (Exception e) {
                startListening(context, portName);
            }
        }
    }

    /**
     * 初始化所有需要的端口
     * @param context
     */
    public void init(Context context){
        //初始化0、2串口数据监听器
        String port0 = SerialPortEnum._0.getPath();
        String port2 = SerialPortEnum._2.getPath();

        instance.setCallback(port0, new SerialPortCom0DataCallback(context));
        instance.startListening(context, port0);

        instance.setCallback(port2, new SerialPortCom2DataCallback(context));
        instance.startListening(context, port2);
    }
}