package com.wanyi.plugins.enums;

public enum OperationLogType {
    OPEN_GATE("手动指令开舱"),
    CARGO_TAKE("扫码开舱");

    private String desc;

    OperationLogType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
