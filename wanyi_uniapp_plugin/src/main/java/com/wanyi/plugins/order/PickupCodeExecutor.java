package com.wanyi.plugins.order;

import android.content.Context;
import android.util.Log;

import com.wanyi.plugins.dao.PickupCodeDao;
import com.wanyi.plugins.database.AppDatabase;
import com.wanyi.plugins.entity.PickupCode;
import com.wanyi.plugins.exception.BusinessException;
import com.wanyi.plugins.utils.ExcelHelper;
import com.wanyi.plugins.utils.text.StringUtils;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 取货码操作
 */
public class PickupCodeExecutor {
    public static final String TAG = "PickupCodeExecutor";

    private static PickupCodeDao pickupCodeDao;
    public static boolean importPickupCode(Context context, String filePath, int insertType) {
        List<Map<String, Object>> dataMaps = ExcelHelper.readExcel(filePath, context);
        Map<String, Object> dataMap = dataMaps.get(0);
        if (MapUtils.isEmpty(dataMap)){
            throw new BusinessException("取货码不能为空");
        }

        new Thread(() -> {
            try {
                executeInsertPickupCode(context, dataMaps, insertType);
            } catch (Exception e) {
                Log.e(TAG, "导入取货码出错: ", e);
            }
        }).start();

        return true;
    }

    /**
     *
     * @param context
     * @param dataMaps
     * @param insertType 1 覆盖更新 2增量导入 3清空
     */
    public static void executeInsertPickupCode(Context context, List<Map<String, Object>> dataMaps, int insertType) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        pickupCodeDao = appDatabase.pickupCodeDao();

        if (insertType == 3){
            Log.i(TAG, "清空取货码");
            pickupCodeDao.deleteAll();
            return;
        }


        PickupCode[] insertList = new PickupCode[dataMaps.size()];
        for (int i = 0; i < dataMaps.size(); i++) {
            Map<String, Object> dataMap = dataMaps.get(i);
            String code = MapUtils.getString(dataMap, "A");
            String projectCode = MapUtils.getString(dataMap, "B");
            PickupCode existed = pickupCodeDao.selectByCode(code);
            if (existed != null && existed.getStatus() == 0){
                if (insertType == 1){
                    existed.setStatus(0);
                    pickupCodeDao.updateRecord(existed);
                }else {
                    Log.i(TAG, "取货码已存在，跳过：" + code);
                }
            }
            insertList[i] = new PickupCode(projectCode, code);
        }
        pickupCodeDao.insertAll(insertList);
        Log.i(TAG, "导入取货码成功，数量：" + insertList.length);
    }

}
