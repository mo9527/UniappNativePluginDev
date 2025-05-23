package com.wanyi.plugins;

import static com.wanyi.plugins.enums.OperationLogType.OPEN_GATE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lztek.toolkit.Lztek;
import com.lztek.toolkit.SerialPort;
import com.wanyi.plugins.cache.CargoCacheOperator;
import com.wanyi.plugins.cache.LocalCache;
import com.wanyi.plugins.constants.CacheConstants;
import com.wanyi.plugins.constants.GateConstants;
import com.wanyi.plugins.entity.DeviceOperationLog;
import com.wanyi.plugins.enums.GateOrderEnum;
import com.wanyi.plugins.enums.SerialPortEnum;
import com.wanyi.plugins.model.CargoStockVo;
import com.wanyi.plugins.model.Device;
import com.wanyi.plugins.model.Response;
import com.wanyi.plugins.order.CargoMachineCommandExecutor;
import com.wanyi.plugins.order.CargoMoveCommandExecutor;
import com.wanyi.plugins.order.PickupCodeExecutor;
import com.wanyi.plugins.order.ScrewRodCommandExecutor;
import com.wanyi.plugins.permissions.PermissionNeed;
import com.wanyi.plugins.qrcode.QrCodeScannerService;
import com.wanyi.plugins.serialport.SerialPortManager;
import com.wanyi.plugins.service.CargoStockMonitorService;
import com.wanyi.plugins.service.DeviceStatusCheckService;
import com.wanyi.plugins.service.OperationLogService;
import com.wanyi.plugins.socket.WebsocketServer;
import com.wanyi.plugins.order.GateCommandExecutor;
import com.wanyi.plugins.utils.ExcelHelper;
import com.wanyi.plugins.utils.HexUtil;
import com.wanyi.plugins.utils.LanIpUtil;
import com.wanyi.plugins.utils.SerialPortUtils;
import com.wanyi.plugins.utils.text.StringUtils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class WanyiPlugins extends UniModule {
    private final static String TAG = "WanyiPlugins";

    private final static String OPEN = GateConstants.DOOR_OPEN;
    private final static String CLOSE = GateConstants.DOOR_CLOSE;

    /**
     * 初始化一些东西
     * @param options
     * @param callback
     */
    @UniJSMethod(uiThread = false)
    public void init(JSONObject options, final UniJSCallback callback){
        Context context = mUniSDKInstance.getContext();
        WebsocketServer.startServer(context);
        QrCodeScannerService.start(context);
        CargoStockMonitorService.getInstance().init(context);


//        LocalCache localCache = LocalCache.getInstance(context);
//        localCache.setInt(CacheConstants.X_EACH_FLOOR_STOCK, 5);
//
//        Log.i(TAG, "查询缓存：" + localCache.get("cargoStockTotal"));

        //初始化库存
//        CargoCacheOperator.getInstance(context).resetCargoStock(40, 5);

        SerialPortManager.getInstance().init(context);
//        CargoMachineCommandExecutor.getInstance().resetPosition(context);

        ExcelHelper.initProperties();
    }


    @Override
    public void onActivityDestroy() {
        Log.i(TAG, "销毁 onActivityDestroy..........");
        WebsocketServer.stopServer();
        QrCodeScannerService.close(mUniSDKInstance.getContext());
        SerialPortManager.getInstance().stopAllListening();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(PermissionNeed.REQUEST_CODE == requestCode) {
            for(int i= 0; i< permissions.length; i++) {
                String preName = permissions[i];
                int granted = grantResults[i];
                if(Manifest.permission.ACCESS_FINE_LOCATION.equals(preName) && granted == PackageManager.PERMISSION_GRANTED) {
                    //获取权限结果
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @UniJSMethod(uiThread = false)
    public void openGate(JSONObject options, final UniJSCallback callback) {
        JSONObject result = GateCommandExecutor.openGate(mUniSDKInstance.getContext());
        OperationLogService.getInstance().addLog(mUniSDKInstance.getContext().getApplicationContext(), OPEN_GATE, OPEN_GATE.getDesc());
        callback.invoke(result);
    }

    @UniJSMethod(uiThread = false)
    public void gateStatus(JSONObject options, final UniJSCallback callback){
        SerialPort serialPort = null;
        Context context = mUniSDKInstance.getContext();
        try {
            Lztek lztek = Lztek.create(context);
            serialPort = lztek.openSerialPort(SerialPortEnum._0.getPath(), GateOrderEnum.STATUS_OPEN.getBaud(), 8, 0, 1, 0);
            if (serialPort == null){
                callback.invoke(Response.fail("打开端口失败"));
                return;
            }

            int dataLength = SerialPortUtils.write(serialPort, GateOrderEnum.STATUS_OPEN.getOrder());
            long startTime = System.currentTimeMillis();
            Log.i(TAG, "查询锁状态, 接收反馈信号");
            do {
                byte[] result = SerialPortUtils.read(serialPort);
                if (result != null){
                    String s = HexUtil.hexString(result);
                    Log.i(TAG, "读取串口Hex数据：" + s);
                    if (s.equalsIgnoreCase(GateOrderEnum.STATUS_OPEN.getOrderOkRes())){
                        callback.invoke(Response.success(OPEN));
                        return;
                    }else if (s.equalsIgnoreCase(GateOrderEnum.STATUS_CLOSE.getOrderOkRes())){
                        callback.invoke(Response.success(CLOSE));
                        return;
                    }
                }
            }while (System.currentTimeMillis() - startTime < 5000);

        }catch (Exception e){
            Log.e(TAG, "获取串口数据错误", e);
            callback.invoke(Response.fail());
            return;
        }finally {
            serialPort.close();
            Log.i(TAG, "关闭0串口");
        }

        callback.invoke(Response.fail());
    }

    @UniJSMethod(uiThread = false)
    public void screwRodMoveTo(JSONObject options, final UniJSCallback callback){
        int floor = options.getIntValue("floor");
        ScrewRodCommandExecutor.moveTo(mUniSDKInstance.getContext(), floor);
        callback.invoke( Response.success());
    }

    @UniJSMethod(uiThread = false)
    public void cargoMove(JSONObject options, final UniJSCallback callback){
        int floor = options.getIntValue("floor");
        JSONObject jsonObject = CargoMoveCommandExecutor.cargoPush(mUniSDKInstance.getContext(), floor);
        callback.invoke(jsonObject);
    }

    @UniJSMethod(uiThread = false)
    public void restCargoStock(JSONObject options, final UniJSCallback callback){
        int itemTotal = options.getIntValue("itemTotal");
        if (itemTotal <= 1){
            callback.invoke(Response.fail("每层货物数量必须大于1"));
            return;
        }
        int total = itemTotal * 7;
        CargoCacheOperator cargoCacheOperator = CargoCacheOperator.getInstance(mUniSDKInstance.getContext());
        cargoCacheOperator.resetCargoStock(total, itemTotal);
        CargoMachineCommandExecutor.getInstance().resetPosition(mUniSDKInstance.getContext());
        callback.invoke(Response.success());
    }

    @UniJSMethod(uiThread = false)
    public void getRobotIp(JSONObject options, final UniJSCallback callback){
        String ip = LanIpUtil.getUpIp(mUniSDKInstance.getContext());
        String robotIp = LanIpUtil.getRobotIP();
        Map<String, String> map = new HashMap<>();
        map.put("upIp", ip);
        map.put("downIp", robotIp);
        Log.i(TAG, "获取到ip：" + JSONObject.toJSONString(map));
        callback.invoke(Response.success(map));
    }

    @UniJSMethod(uiThread = true)
    public void devicesCheck(JSONObject options, final UniJSCallback callback){
        List<Device> deviceList = DeviceStatusCheckService.getInstance().checkDeviceStatus(mUniSDKInstance.getContext());
        Log.i(TAG, "设备检查结果：" + JSONObject.toJSONString(deviceList));
        callback.invoke(Response.success(deviceList));
    }

    /**
     * 导入取货码,上位机直接导入使用
     * @param options
     */
    @UniJSMethod
    public void importPickupCode(JSONObject options, final UniJSCallback callback){
        String filePath = options.getString("path");
        if (StringUtils.isEmpty(filePath)){
            callback.invoke(Response.fail("文件路径为空"));
            return;
        }

        int type = options.getIntValue("type");

        try {
            boolean success = PickupCodeExecutor.importPickupCode(mUniSDKInstance.getContext(), filePath, type);
            if (success){
                callback.invoke(Response.success());
            }
        }catch (Exception e){
            Log.e(TAG, "导入取货码出错: ", e);
            callback.invoke(Response.fail(e.getMessage()));
        }
    }

    /**
     * app端读取excel使用，得到的数据用socket发送到上位机
     * @param options
     * @param callback
     */
    @UniJSMethod(uiThread = false)
    public void readExcel(JSONObject options, final UniJSCallback callback){
        String filePath = options.getString("path");
        if (StringUtils.isEmpty(filePath)){
            callback.invoke(Response.fail("文件路径为空"));
            return;
        }

        try {
            List<Map<String, Object>> dataList = ExcelHelper.readExcel(filePath, mUniSDKInstance.getContext());
            Log.i(TAG, "读取到Excel数据：" + JSONObject.toJSONString(dataList));
            callback.invoke(Response.success(dataList));
        }catch (Exception e){
            callback.invoke(Response.fail(e.getMessage()));
        }

    }

    @UniJSMethod(uiThread = false)
    public void getOperationLog(JSONObject options, final UniJSCallback callback){
        JSONArray typeList = options.getJSONArray("type");
        if (CollectionUtils.isEmpty(typeList)){
            callback.invoke(Response.fail("筛选类型为空"));
            return;
        }

        int pageNum = options.getIntValue("pageNum");
        OperationLogService logService = OperationLogService.getInstance();
        List<String> types = typeList.toJavaList(String.class);
        List<DeviceOperationLog> logListPage = logService.getLogListPage(types, mUniSDKInstance.getContext(), pageNum);
        Log.i(TAG, "获取操作日志：" + JSONObject.toJSONString(logListPage));
        if (CollectionUtils.isNotEmpty(logListPage)){
            callback.invoke(Response.success(logListPage));
            return;
        }
        callback.invoke(Response.success());
    }

    @UniJSMethod(uiThread = false)
    public void getCargoStock(JSONObject options, final UniJSCallback callback){
        CargoStockVo cargoStockVo = CargoStockMonitorService.getInstance().getCargoStockVo(mUniSDKInstance.getContext());
        callback.invoke(Response.success(cargoStockVo));
    }


}