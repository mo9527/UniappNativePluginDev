package com.wanyi.plugins.model;

import android.content.Context;

import org.java_websocket.WebSocket;

public class FuncInputData {
    private Context context;

    private WebSocket client;

    private String data;

    public FuncInputData(Context context, String data, WebSocket client) {
        this.context = context;
        this.data = data;
        this.client = client;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public WebSocket getClient() {
        return client;
    }

    public void setClient(WebSocket client) {
        this.client = client;
    }
}
