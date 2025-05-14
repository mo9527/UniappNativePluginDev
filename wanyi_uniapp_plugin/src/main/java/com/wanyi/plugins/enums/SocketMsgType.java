package com.wanyi.plugins.enums;

public enum SocketMsgType {
    CARGO_STOCK_VO("货物和取货码剩余数量");
    private String desc;
    SocketMsgType(String desc) {
        this.desc = desc;
    }
}
