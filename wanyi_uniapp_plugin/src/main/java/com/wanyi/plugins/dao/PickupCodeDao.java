package com.wanyi.plugins.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.wanyi.plugins.entity.PickupCode;

import java.util.List;

@Dao
public interface PickupCodeDao {

    @Query("SELECT * FROM pickup_code")
    List<PickupCode> selectAll();

    //批量插入数据（重复数据覆盖）
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(PickupCode... records);

    //取一个未使用的code
    @Query("SELECT * FROM pickup_code WHERE status = 0 LIMIT 1")
    PickupCode pickOneUnUsed();

    @Query("SELECT * FROM pickup_code WHERE code = :code")
    PickupCode selectByCode(String code);

    @Update
    int updateRecord(PickupCode record);

    @Query("DELETE FROM pickup_code")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM pickup_code WHERE status = 0")
    int countOfUnUsed();

    @Query("SELECT COUNT(*) FROM pickup_code WHERE status = 1")
    int countOfUsed();
}
