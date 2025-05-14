package com.wanyi.plugins.commandFunc;

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
        JSONObject jsonObject = GateCommandExecutor.openGate(data.getContext());
        OperationLogService.getInstance().addLog(data.getContext(), OPEN_GATE, OPEN_GATE.getDesc());
        return jsonObject;
    }
}
