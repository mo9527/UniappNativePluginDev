package com.wanyi.plugins.enums;

public enum GateOrderEnum {
    OPEN(9600, "8A0101119B", "8A0101008A",  "开锁命令"),
    STATUS_OPEN(9600, "80010133B3", "80010100B3", "查询锁状态是否打开"),

    STATUS_CLOSE(9600, "80010133B3", "8001010080", "查询锁状态是否关闭");

    private int baud;

    private String order;

    private String orderOkRes;

    private String desc;

    GateOrderEnum(int baud, String order, String orderOkRes, String desc) {
        this.baud = baud;
        this.order = order;
        this.orderOkRes = orderOkRes;
        this.desc = desc;
    }

    public int getBaud() {
        return baud;
    }

    public String getOrder() {
        return order;
    }

    public String getOrderOkRes() {
        return orderOkRes;
    }

    public String getDesc() {
        return desc;
    }
}
