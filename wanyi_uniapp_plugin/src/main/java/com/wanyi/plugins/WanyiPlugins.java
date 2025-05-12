package com.wanyi.plugins;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.lztek.toolkit.Lztek;
import com.lztek.toolkit.SerialPort;
import com.wanyi.plugins.cache.CargoCacheOperator;
import com.wanyi.plugins.cache.LocalCache;
import com.wanyi.plugins.constants.CacheConstants;
import com.wanyi.plugins.constants.GateConstants;
import com.wanyi.plugins.enums.GateOrderEnum;
import com.wanyi.plugins.enums.SerialPortEnum;
import com.wanyi.plugins.model.Response;
import com.wanyi.plugins.order.CargoMachineCommandExecutor;
import com.wanyi.plugins.order.CargoMoveCommandExecutor;
import com.wanyi.plugins.order.PickupCodeExecutor;
import com.wanyi.plugins.order.ScrewRodCommandExecutor;
import com.wanyi.plugins.permissions.PermissionNeed;
import com.wanyi.plugins.qrcode.QrCodeScannerService;
import com.wanyi.plugins.serialport.SerialPortCom0DataCallback;
import com.wanyi.plugins.serialport.SerialPortCom2DataCallback;
import com.wanyi.plugins.serialport.SerialPortManager;
import com.wanyi.plugins.socket.WebsocketServer;
import com.wanyi.plugins.order.GateCommandExecutor;
import com.wanyi.plugins.utils.HexUtil;
import com.wanyi.plugins.utils.LanIpUtil;
import com.wanyi.plugins.utils.SerialPortUtils;
import com.wanyi.plugins.utils.text.StringUtils;

import java.util.HashMap;
import java.util.Map;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;
import io.dcloud.feature.uniapp.utils.UniLogUtils;

public class WanyiPlugins extends UniModule {
    private final static String TAG = "WanyiPlugins";

    private final static String OPEN = GateConstants.DOOR_OPEN;
    private final static String CLOSE = GateConstants.DOOR_CLOSE;

    private SerialPortManager serialPortManager;

    /**
     * 接收远控命令
     * @param options
     * @param callback
     */
    @UniJSMethod(uiThread = false)
    public void startRemoteServer(JSONObject options, final UniJSCallback callback){
        Context context = mUniSDKInstance.getContext();
        WebsocketServer.startServer(context);
        QrCodeScannerService.start(context);
//        DatabaseInitialization.init(context);

        LocalCache localCache = LocalCache.getInstance(context);
        localCache.set(CacheConstants.X_EACH_FLOOR_STOCK, "5");

        Log.i(TAG, "查询缓存：" + localCache.get("cargoStockTotal"));

        //初始化库存
        CargoCacheOperator.getInstance(context).resetCargoStock(40, 5);

        SerialPortManager.getInstance().init(context);
        CargoMachineCommandExecutor.getInstance().resetPosition(context);
    }


    @Override
    public void onActivityDestroy() {
        Log.i(TAG, "开始 onActivityDestroy..........");
        WebsocketServer.stopServer();
        QrCodeScannerService.close(mUniSDKInstance.getContext());

        if (serialPortManager != null){
            serialPortManager.stopAllListening();
        }
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
        boolean success = ScrewRodCommandExecutor.moveTo(mUniSDKInstance.getContext(), floor);
        callback.invoke(success ? Response.success() : Response.fail());
    }

    @UniJSMethod(uiThread = false)
    public void cargoMove(JSONObject options, final UniJSCallback callback){
        int floor = options.getIntValue("floor");
        JSONObject jsonObject = CargoMoveCommandExecutor.cargoPush(mUniSDKInstance.getContext(), floor);
        callback.invoke(jsonObject);
    }

    @UniJSMethod(uiThread = false)
    public void restCargoStock(JSONObject options, final UniJSCallback callback){
        int total = options.getIntValue("total");
        if (total % 8 != 0){
            callback.invoke(Response.fail("请输入8的倍数"));
            return;
        }
        int eachFloorCargos = total/8;
        CargoCacheOperator cargoCacheOperator = CargoCacheOperator.getInstance(mUniSDKInstance.getContext());
        cargoCacheOperator.resetCargoStock(total, eachFloorCargos);
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
        for (int i = 0; i < 5; i++) {
            try {
                Log.i(TAG, "设备检查中：" + i);
                LocalCache localCache = new LocalCache(mUniSDKInstance.getContext());
                String res = localCache.get("cargoStockTotal");
                callback.invokeAndKeepAlive(Response.success("设备"+i+"状态：OK "  + res));
                Thread.sleep(1000);
            }catch (Exception e){
                UniLogUtils.i("设备检查出错", e);
            }
        }
    }

    /**
     * 导入取货码
     * @param options
     */
    @UniJSMethod
    public void importPickupCode(JSONObject options, final UniJSCallback callback){
        String filePath = options.getString("path");
        if (StringUtils.isEmpty(filePath)){
            callback.invoke(Response.fail("文件路径为空"));
            return;
        }

        try {
            boolean success = PickupCodeExecutor.importPickupCode(mUniSDKInstance.getContext(), filePath);
            if (success){
                callback.invoke(Response.success());
            }
        }catch (Exception e){
            callback.invoke(Response.fail(e.getMessage()));
        }
    }

}