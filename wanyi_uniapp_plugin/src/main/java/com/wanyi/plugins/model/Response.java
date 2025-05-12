package com.wanyi.plugins.model;

import com.alibaba.fastjson.JSONObject;

public class Response<T> {
    public static final int SUCCESS = 0;
    public static final int FAIL = -1;

    //状态码 -1:失败，0 成功
    private int status;


    //提示信息,错误信息
    private String message;

    //数据载体
    private T data;

    public static JSONObject fail(){
        Response<Object> model = new Response<>();
        model.setStatus(FAIL);
        model.setMessage("操作失败");
        return toJson(model);
    }

    public static JSONObject fail(String msg){
        Response<Object> model = new Response<>();
        model.setStatus(FAIL);
        model.setMessage(msg);
        return toJson(model);
    }

    public static JSONObject success(){
        Response<Object> model = new Response<>();
        model.setStatus(SUCCESS);
        model.setMessage("操作成功");
        return toJson(model);
    }

    public static JSONObject success(Object data){
        Response<Object> model = new Response<>();
        model.setStatus(SUCCESS);
        model.setMessage("操作成功");
        model.setData(data);
        return toJson(model);
    }

    private static JSONObject toJson(Response<Object> model){
        return JSONObject.parseObject(JSONObject.toJSONString(model));
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
