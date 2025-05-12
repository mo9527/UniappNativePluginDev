package com.wanyi.plugins.order;

import android.content.Context;

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
    public static boolean importPickupCode(Context context, String filePath) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        pickupCodeDao = appDatabase.pickupCodeDao();

        List<Map<String, Object>> dataMaps = ExcelHelper.readExcel(filePath, context);
        Map<String, Object> dataMap = dataMaps.get(0);
        if (MapUtils.isEmpty(dataMap)){
            throw new BusinessException("取货码不能为空");
        }

        PickupCode[] insertList = {};
        dataMap.forEach((key, value) -> {
            String code = String.valueOf(value);
            if (StringUtils.isNotEmpty(code)){
                PickupCode existed = pickupCodeDao.selectByCode(code);
                if (existed != null && existed.getStatus() == 0){
                    throw new BusinessException("取货码已存在");
                }
                ArrayUtils.add(insertList, new PickupCode(code));
            }
        });

        pickupCodeDao.insertAll(insertList);
        return true;
    }

}
