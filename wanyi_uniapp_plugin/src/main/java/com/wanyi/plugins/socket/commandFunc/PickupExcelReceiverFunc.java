package com.wanyi.plugins.socket.commandFunc;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanyi.plugins.entity.PickupCode;
import com.wanyi.plugins.model.FuncInputData;
import com.wanyi.plugins.model.Response;
import com.wanyi.plugins.order.PickupCodeExecutor;
import com.wanyi.plugins.utils.text.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 导入取货码文件接收
 */
public class PickupExcelReceiverFunc implements Function<FuncInputData, JSONObject> {
    public static final String TAG = "PickupExcelReceiverFunc";

    /**
     * {
     *     "command": "import_pickup_code",
     *     "payload": {
     *     	"type": 1,
     *     	"data": [{"A":"90016","B":"ABC"},{"A":"90017","B":"ABC"},{"A":"90018","B":"ABC"},{"A":"90019","B":"ABC"},{"A":"90020","B":"ABC"},{"A":"90021","B":"ABC"},{"A":"90022","B":"ABC"},{"A":"90023","B":"ABC"},{"A":"90024","B":"ABC"},{"A":"90025","B":"ABC"},{"A":"90026","B":"ABC"},{"A":"90027","B":"ABC"},{"A":"90028","B":"ABC"},{"A":"90029","B":"ABC"},{"A":"90030","B":"ABC"},{"A":"90031","B":"ABC"},{"A":"90032","B":"ABC"},{"A":"90033","B":"ABC"},{"A":"90034","B":"ABC"},{"A":"90035","B":"ABC"},{"A":"90036","B":"ABC"}]
     *     }
     *
     * }
     * @param data the function argument
     * @return
     */
    @Override
    public JSONObject apply(FuncInputData data) {
        String payload = data.getPayload();
        Context context = data.getContext();
        //把payload转成json array
        if (StringUtils.isNotEmpty(payload)){
            try {
                JSONObject payloadJson = JSONObject.parseObject(payload);
                JSONObject payloadData = payloadJson.getJSONObject("payload");

                int type = payloadData.getIntValue("type");
                JSONArray jsonArray = payloadData.getJSONArray("data");
                List<Map<String, Object>> dataMaps = new ArrayList<>();
                for (Object o : jsonArray) {
                    Map<String, Object> dataMap = (Map<String, Object>) o;
                    dataMaps.add(dataMap);
                }
                PickupCodeExecutor.executeInsertPickupCode(context, dataMaps, type);
            }catch (Exception e){
                Log.e(TAG, "导入取货码出错: ", e);
                return Response.fail("导入失败");
            }
        }
        return Response.success();
    }
}
