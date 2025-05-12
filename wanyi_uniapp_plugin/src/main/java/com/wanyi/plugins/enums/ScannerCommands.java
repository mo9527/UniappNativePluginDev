package com.wanyi.plugins.enums;

/**
 * Created by jiajia on 2018/12/15.
 */

public enum ScannerCommands {
    CMD_FW_TYPE("查询固件类型",new byte[]{0x02,(byte) 0xf2,0x12,'F','W','T','Y','P','E',0x03}),
    CMD_FW_VERSION("查询固件版本",new byte[]{0x02,(byte) 0xf0,0x03,'0','d','1','3','0','2','?','.'}),
    CMD_INT_TYPE("查询接口类型",new byte[]{0x02,(byte)0xf0,0x03,'0','6','0','5','0','0','?','.'}),
    CMD_MULTI_SCAN("配置连扫模式",new byte[]{0x02,(byte)0xf0,0x03,'0','9','0','9','0','3','.'}),
    CMD_CLOSE_LIGHT("关闭灯光",new byte[]{0x02,(byte)0xf0,0x03,'0','4','0','1','0','2','1','.'}),
    CMD_OPEN_LIGHT("开启灯光",new byte[]{0x02,(byte)0xf0,0x03,'0','4','0','1','0','2','4','.'}),
    CMD_CLOSE_BEEPER("关闭Beep",new byte[]{0x02,(byte)0xf0,0x03,'0','5','0','2','1','0','0','.'}),
    CMD_OPEN_BEEPER("开启Beep",new byte[]{0x02,(byte)0xf0,0x03,'0','5','0','2','1','0','1','.'}),
    CMD_TRIGGER_SCAN("开启触发",new byte[]{0x02,(byte)0xf4,0x03}),
    CMD_UNTRIGGER_SCAN("关闭触发",new byte[]{0x02,(byte)0xf5,0x03});
    private String name;
    private byte[] cmd;
    ScannerCommands(String name, byte[] cmd){
        this.name=name;
        this.cmd=cmd;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public byte[] getCmd() {
        return cmd;
    }

    public String getName() {
        return name;
    }
}
