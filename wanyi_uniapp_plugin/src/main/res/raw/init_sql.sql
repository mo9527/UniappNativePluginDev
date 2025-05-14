-- 创建数据库后初始化脚本

DROP TABLE IF EXISTS pickup_code;
CREATE TABLE IF NOT EXISTS
pickup_code (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    project_code  TEXT NOT NULL, --项目代码
    code TEXT NOT NULL, --取货码
    status INTEGER DEFAULT 0,    --状态
    create_time TEXT DEFAULT (datetime('now')),
    update_time TEXT DEFAULT (datetime('now'))
);

CREATE UNIQUE INDEX pickup_code_uindex
on pickup_code (code);

DROP TABLE IF EXISTS device_operation_log;
CREATE TABLE IF NOT EXISTS
device_operation_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    type TEXT NOT NULL,  -- 类型
    description TEXT DEFAULT '', --描述
    operator TEXT DEFAULT '', -- 操作人
    create_time TEXT DEFAULT (datetime('now')),
    update_time TEXT DEFAULT (datetime('now'))
);

insert into pickup_code (project_code, code, status) values ('ABC', '123321', 0);
insert into pickup_code (project_code, code, status) values ('ABC', '123322', 0);
insert into pickup_code (project_code, code, status) values ('ABC', '123323', 0);
insert into pickup_code (project_code, code, status) values ('ABC', '123324', 0);
insert into pickup_code (project_code, code, status) values ('ABC', '123325', 0);
