package com.wanyi.plugins.utils;

import com.lztek.toolkit.SerialPort;

public class SerialPortUtils {

    /**
     * 向串口发送数据
     * return: data length
     */
    public static int write(SerialPort serialPort, String hexStr ) {
        if (hexStr == null || hexStr.isEmpty()) return -1;
        byte[] data = HexUtil.hexBytes(hexStr);
        if (null == serialPort || null == data || data.length == 0) {
            return -1;
        }
        java.io.OutputStream output = null;
        try {
            output = serialPort.getOutputStream();
            output.write(data);
            return data.length;
        } catch (Exception e) {
            android.util.Log.d("#ERROR#", "[COM]Write Failed: " + e.getMessage(), e);
            return -1;
        }finally {
            try {
                if (output != null) output.close();
            }catch (Exception e){
                //ignore
            }
        }
    }

    /**
     * 从串口读取数据
     * @return
     */
    public static byte[] read(SerialPort mSerialPort) {
        if (null == mSerialPort) {
            return null;
        }

        java.io.InputStream input = null;
        try {
            input = mSerialPort.getInputStream();

            byte[] buffer = new byte[2048];
            int len = input.read(buffer);
            // return len > 0? new String(buffer, 0, len) : "";
            return len > 0 ? java.util.Arrays.copyOfRange(buffer, 0, len) : null;
        } catch (Exception e) {
            android.util.Log.d("#ERROR#", "[COM]Read Failed: " + e.getMessage(), e);
            return null;
        }
    }
}
