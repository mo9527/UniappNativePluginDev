package com.wanyi.plugins.model;

import android.content.Context;

import java.nio.ByteBuffer;

public class FuncInputData {
    private Context context;

    private String payload;

    public FuncInputData(Context context, String payload) {
        this.context = context;
        this.payload = payload;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

}
