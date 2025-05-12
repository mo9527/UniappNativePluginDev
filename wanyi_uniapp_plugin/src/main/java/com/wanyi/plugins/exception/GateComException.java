package com.wanyi.plugins.exception;

public class GateComException extends RuntimeException{
    private final String errorMsg;

    public GateComException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
