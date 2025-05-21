package com.wanyi.plugins.socket;


import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.wanyi.plugins.socket.commandFunc.DevicesCheckFunc;
import com.wanyi.plugins.socket.commandFunc.GateCommandFunc;
import com.wanyi.plugins.socket.commandFunc.ImageSaverFunc;
import com.wanyi.plugins.socket.commandFunc.PickupExcelReceiverFunc;
import com.wanyi.plugins.enums.SocketCommandEnum;
import com.wanyi.plugins.model.FuncInputData;
import com.wanyi.plugins.model.Response;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class WebsocketServer {
    private static final String TAG = "WebSocketServer";
    private static WebSocketServer server;
    private static final int DEFAULT_PORT = 9527;
    private static boolean isRunning;


    private static final ConcurrentHashMap<String, WebSocket> clientMap = new ConcurrentHashMap<>();

    private static final Map<String, Function<FuncInputData, JSONObject>> commandMap;

    //注册命令函数
    static {
        commandMap = new HashMap<>();
        commandMap.put(SocketCommandEnum.OPEN_GATE.getCommand(), new GateCommandFunc());
        commandMap.put(SocketCommandEnum.IMAGE_SAVE.getCommand(), new ImageSaverFunc());
        commandMap.put(SocketCommandEnum.IMPORT_PICKUP_CODE.getCommand(), new PickupExcelReceiverFunc());
        commandMap.put(SocketCommandEnum.DEVICES_CHECK.getCommand(), new DevicesCheckFunc());
    }

    public synchronized static void startServer(Context context) {
        try {
            if (server == null) {
                server = new WebSocketServer(new InetSocketAddress(DEFAULT_PORT)) {
                    @Override
                    public void onOpen(WebSocket conn, ClientHandshake handshake) {
                        Log.i(TAG, "Client connected: " + conn.getRemoteSocketAddress());
                        clientMap.put(conn.getRemoteSocketAddress().toString(), conn);
                    }

                    @Override
                    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                        Log.i(TAG, "Client disconnected: " + reason);
                        clientMap.remove(conn.getRemoteSocketAddress().toString());
                    }

                    @Override
                    public void onError(WebSocket conn, Exception ex) {
                        Log.e(TAG, "Websocket Server error: " + ex.getMessage());
                    }

                    @Override
                    public void onStart() {
                        Log.i(TAG, "Websocket Server started on port " + DEFAULT_PORT);
                        isRunning = true;
                    }

                    /*****************************************************************************/

                    @Override
                    public void onMessage(WebSocket conn, String message) {
                        try {
                            Log.i(TAG, "Received socket message: " + message);
                            // Parse JSON message
                            org.json.JSONObject json = new org.json.JSONObject(message);

                            if (json.has("type") && "heartbeat".equalsIgnoreCase(json.getString("type"))){
                                conn.send(Response.success().toJSONString());
                                return;
                            }
                            String action = json.getString("command");

                            if (commandMap.containsKey(action)) {
                                Function<FuncInputData, JSONObject> func = commandMap.get(action);
                                assert func != null;

                                FuncInputData data = new FuncInputData(context, message, conn);
                                JSONObject res = func.apply(data);
                                conn.send(res.toJSONString());
                            } else {
                                conn.send(Response.fail("未知的命令").toJSONString());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Message error: " + e.getMessage());
                            conn.send(Response.fail("处理消息失败").toJSONString());
                        }
                    }
                };
                server.setReuseAddr(true);
                server.start();
            }
        } catch (Exception e) {
            Log.e(TAG, "Websocket Start server failed: " + e.getMessage());
        }
    }

    public synchronized static void sendMessage(String message) {
        try {
            for (WebSocket conn : clientMap.values()) {
                if (conn.isOpen()){
                    try {
                        conn.send(message);
                    }catch (Exception e){
                        Log.w(TAG, "sendMessage to "+conn.getRemoteSocketAddress()+" error:" + e.getMessage(), e);
                    }
                }
            }
        }catch (Exception e){
            Log.e(TAG, "发送socket消息失败 error:" + e.getMessage(), e);
        }
    }

    public synchronized static void sendMessage(WebSocket conn, String message){
        try {
            if (conn != null && conn.isOpen()){
                conn.send(message);
            }
        }catch (Exception e){
            Log.e(TAG, "发送socket消息失败 error:" + e.getMessage(), e);
        }
    }

    public synchronized static void sendMessage(String remoteIp, String message){
        sendMessage(clientMap.get(remoteIp), message);
    }

    public static boolean isServerRunning() {
        return server != null && isRunning;
    }

    public static void stopServer() {
        try {
            if (server != null) {
                server.stop(1000);
                server = null;
                Log.i(TAG, "Server stopped");
            }
        } catch (Exception e) {
            Log.e(TAG, "Stop server failed: " + e.getMessage());
        }finally {
            isRunning = false;
        }
    }

}
