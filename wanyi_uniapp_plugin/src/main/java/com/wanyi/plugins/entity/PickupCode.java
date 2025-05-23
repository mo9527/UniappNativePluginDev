package com.wanyi.plugins.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.wanyi.plugins.utils.DateUtils;

@Entity(tableName = "pickup_code", indices = {@Index(value = {"code"}, unique = true)})
public class PickupCode {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "project_code")
    private String project_code;

    private String code;

    //0 未使用 1 已使用
    private int status;

    @ColumnInfo(name = "create_time")
    private String createTime;

    @ColumnInfo(name = "update_time")
    private String updateTime;

    public PickupCode(String projectCode, String code) {
        this.project_code = projectCode;
        this.code = code;
        this.status = 0;
        this.createTime = DateUtils.getDateTime();
        this.updateTime = DateUtils.getDateTime();
    }

    public PickupCode() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
