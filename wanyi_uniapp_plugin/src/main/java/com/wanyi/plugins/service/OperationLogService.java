package com.wanyi.plugins.service;

import android.content.Context;

import com.wanyi.plugins.dao.DeviceOperationLogDao;
import com.wanyi.plugins.database.AppDatabase;
import com.wanyi.plugins.entity.DeviceOperationLog;
import com.wanyi.plugins.enums.OperationLogType;
import com.wanyi.plugins.utils.DateUtils;

import java.util.List;

public class OperationLogService {
    public static final String TAG = "OperationLogService";
    private static OperationLogService INSTANCE;
    public static OperationLogService getInstance() {
        if (INSTANCE == null) {
            synchronized (OperationLogService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new OperationLogService();
                }
            }
        }
        return INSTANCE;
    }

    public void addLog(Context context, OperationLogType type, String description){
        AppDatabase database = AppDatabase.getDatabase(context.getApplicationContext());
        DeviceOperationLogDao logDao = database.deviceOperationLogDao();

        DeviceOperationLog log = new DeviceOperationLog();
        log.setOperator("Wanyi");
        log.setCreateTime(DateUtils.getDateTime());
        log.setUpdateTime(DateUtils.getDateTime());
        log.setType(type.name());
        log.setDescription(description);
        logDao.insert(log);
    }

    public List<DeviceOperationLog> getLogListPage(List<String> typeList, Context context, int pageNum){
        AppDatabase database = AppDatabase.getDatabase(context.getApplicationContext());
        DeviceOperationLogDao logDao = database.deviceOperationLogDao();
        int limit = 20;
        int pageOffset = (pageNum - 1) * limit;

        return logDao.selectListPage(typeList, limit, pageOffset);
    }
}
