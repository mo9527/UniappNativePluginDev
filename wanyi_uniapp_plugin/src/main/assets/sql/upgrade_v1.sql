-- 创建数据库后初始化脚本

DROP TABLE IF EXISTS pickup_code;
CREATE TABLE IF NOT EXISTS
pickup_code (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT NOT NULL, --取货码
    status INTEGER DEFAULT 0,    --状态
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

DROP INDEX pickup_code_uindex;
CREATE UNIQUE INDEX pickup_code_uindex
on pickup_code (code);

DROP TABLE IF EXISTS device_operation_log;
CREATE TABLE IF NOT EXISTS
device_operation_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    type TEXT NOT NULL,  -- 类型
    description TEXT DEFAULT '', --描述
    operator TEXT DEFAULT '', -- 操作人
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

insert into pickup_code (code, status) values ('123321', 0);
insert into pickup_code (code, status) values ('123322', 0);
insert into pickup_code (code, status) values ('123323', 0);
insert into pickup_code (code, status) values ('123324', 0);
insert into pickup_code (code, status) values ('123325', 0);