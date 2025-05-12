package com.wanyi.plugins.commandFunc;

import com.alibaba.fastjson.JSONObject;
import com.wanyi.plugins.model.FuncInputData;
import com.wanyi.plugins.order.GateCommandExecutor;

import java.util.function.Function;

public class GateCommandFunc implements Function<FuncInputData, JSONObject> {
    @Override
    public JSONObject apply(FuncInputData data) {
        return GateCommandExecutor.openGate(data.getContext());
    }
}
