package com.wanyi.plugins.model;

import android.content.Context;

import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;

public class FuncInputData {
    private Context context;

    private WebSocket client;

    private String payload;

    public FuncInputData(Context context, String payload, WebSocket client) {
        this.context = context;
        this.payload = payload;
        this.client = client;
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

    public WebSocket getClient() {
        return client;
    }

    public void setClient(WebSocket client) {
        this.client = client;
    }
}
