package com.wanyi.plugins.enums;

/**
 * 串口枚举
 */
public enum SerialPortEnum {
    /**
     * com0串口 仓门控制端口
     */
    _0("ttyS0", "/dev/ttyS0"),

    /**
     * com2串口 丝杆和货道电机
     */
    _2("ttyS2", "/dev/ttyS2");

    private String name;
    private String path;

    SerialPortEnum(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

}
