package com.wanyi.plugins.socket.commandFunc;

import static com.wanyi.plugins.enums.OperationLogType.OPEN_GATE;

import com.alibaba.fastjson.JSONObject;
import com.wanyi.plugins.enums.OperationLogType;
import com.wanyi.plugins.model.FuncInputData;
import com.wanyi.plugins.order.GateCommandExecutor;
import com.wanyi.plugins.service.OperationLogService;

import java.util.function.Function;

public class GateCommandFunc implements Function<FuncInputData, JSONObject> {
    @Override
    public JSONObject apply(FuncInputData data) {
        return GateCommandExecutor.openGate(data.getContext());
    }
}
