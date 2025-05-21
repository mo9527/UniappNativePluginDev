package com.wanyi.plugins.socket.commandFunc;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.wanyi.plugins.model.Device;
import com.wanyi.plugins.model.FuncInputData;
import com.wanyi.plugins.model.Response;
import com.wanyi.plugins.service.DeviceStatusCheckService;

import java.util.List;
import java.util.function.Function;

public class DevicesCheckFunc implements Function<FuncInputData, JSONObject> {
    public static final String TAG = "DevicesCheckFunc";

    @Override
    public JSONObject apply(FuncInputData data) {
        try {
            Context context = data.getContext().getApplicationContext();
            List<Device> deviceList = DeviceStatusCheckService.getInstance().checkDeviceStatus(context);
            Log.i(TAG, "远程命令, 设备检查结果：" + JSONObject.toJSONString(deviceList));
            Thread.sleep(2000);
            return Response.success(deviceList);
        }catch (Exception e){
            Log.e(TAG, "设备检查结果解析异常：" + e.getMessage());
            return Response.fail(e.getMessage());
        }
    }
}
