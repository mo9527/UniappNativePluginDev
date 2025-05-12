package com.wanyi.plugins.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.wanyi.plugins.entity.DeviceOperationLog;

import java.util.List;

@Dao
public interface DeviceOperationLogDao {

    @Insert
    void insert(DeviceOperationLog deviceOperationLog);

    @Query("SELECT * FROM device_operation_log order by id desc")
    List<DeviceOperationLog> selectAll();

    @Query("SELECT * FROM device_operation_log order by id desc limit 100")
    List<DeviceOperationLog> selectAllLimit100();
}
