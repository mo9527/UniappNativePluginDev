package com.wanyi.plugins.enums;

public enum SocketCommandEnum {
    OPEN_GATE("open", "执行串口开锁命令"),
    IMAGE_SAVE("image_save", "上传图片并保存到相册"),
    IMPORT_PICKUP_CODE("import_pickup_code", "导入取货码"),
    ;

    private String command;

    private String desc;
    private SocketCommandEnum(String command, String desc)
    {
        this.command = command;
        this.desc = desc;
    }

    public String getCommand()
    {
        return command;
    }
}
