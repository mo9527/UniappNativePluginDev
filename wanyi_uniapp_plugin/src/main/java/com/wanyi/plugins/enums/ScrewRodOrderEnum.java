package com.wanyi.plugins.enums;

public enum ScrewRodOrderEnum {

    MOVE_TO_F1("AA004650C30000000000000B0008080D0A", "AA004650C30000000000000B0008080D0A",  "移动到第一层"),
    MOVE_TO_F2("AA0046A0860100000000000B0008080D0A", "AA0046A0860100000000000B0008080D0A",  "移动到第二层"),
    MOVE_TO_F3("AA0046F0490200000000000B0008080D0A", "AA0046F0490200000000000B0008080D0A",  "移动到第三层"),
    MOVE_TO_F4("AA0046400D0300000000000B0008080D0A", "AA0046400D0300000000000B0008080D0A",  "移动到第四层"),
    MOVE_TO_F5("AA004670820300000000000B0008080D0A", "AA004670820300000000000B0008080D0A",  "移动到第五层"),
    MOVE_TO_F6("AA0046E0930400000000000B0008080D0A", "AA0046E0930400000000000B0008080D0A",  "移动到第六层"),
    MOVE_TO_F7("AA004650A50500000000000B0008080D0A", "AA004650A50500000000000B0008080D0A",  "移动到第七层"),
    MOVE_TO_F8("AA0046801A0600000000000B0008080D0A", "AA0046801A0600000000000B0008080D0A",  "移动到第八层"),

    F1_POSITION("AA00E550C30000000000000B0008080D0A", "", "1层丝杆坐标"),
    F2_POSITION("AA00E5A0860100000000000B0008080D0A", "", "2层丝杆坐标"),
    F3_POSITION("AA00E5F0490200000000000B0008080D0A", "", "3层丝杆坐标"),
    F4_POSITION("AA00E5400D0300000000000B0008080D0A", "", "4层丝杆坐标"),
    F5_POSITION("AA00E570820300000000000B0008080D0A", "", "5层丝杆坐标"),
    F6_POSITION("AA00E5E0930400000000000B0008080D0A", "", "6层丝杆坐标"),
    F7_POSITION("AA00E550A50500000000000B0008080D0A", "", "7层丝杆坐标"),
    F8_POSITION("AA00E5801A0600000000000B0008080D0A", "", "8层丝杆坐标"),

    FETCH_MOTOR_STATUS("AA004100000000000000000B0008080D0A", "AA004100000000000000000B0008080D0A", "查看步进电机状态"),
    MOTOR_ARRIVE_DESTINATION("AA00E550C30000000000000B0008080D0A", "AA00E550C30000000000000B0008080D0A", "丝杆电机到达指定目标"),
    CHECK_MOTOR_CURRENT_FLOOR("AA004300000000000000000B0008080D0A", "", "查看电机当前坐标"),

    CARGO_F1_MOVE("AA0061020100045AAC00000B0008080D0A", "0290000254800D0A",  "第一层货道移动"),

    ;


    private String order;

    private String orderOkRes;

    private String desc;

    public static final int BAUD = 9600;

    ScrewRodOrderEnum( String order, String orderOkRes, String desc) {
        this.order = order;
        this.orderOkRes = orderOkRes;
        this.desc = desc;
    }

    public static ScrewRodOrderEnum getFloorOrder(int floor) {
        switch (floor) {
            case 2:
                return MOVE_TO_F2;
            case 3:
                return MOVE_TO_F3;
            case 4:
                return MOVE_TO_F4;
            case 5:
                return MOVE_TO_F5;
            case 6:
                return MOVE_TO_F6;
            case 7:
                return MOVE_TO_F7;
            case 8:
                return MOVE_TO_F8;
            default:
                return MOVE_TO_F1;
        }
    }

    public static ScrewRodOrderEnum getFloorPosition(int floor) {
        switch (floor) {
            case 2:
                return F2_POSITION;
            case 3:
                return F3_POSITION;
            case 4:
                return F4_POSITION;
            case 5:
                return F5_POSITION;
            case 6:
                return F6_POSITION;
            case 7:
                return F7_POSITION;
            case 8:
                return F8_POSITION;
            default:
                return F1_POSITION;
        }
    }


    public String getOrder() {
        return order;
    }

    public String getOrderOkRes() {
        return orderOkRes;
    }

    public String getDesc() {
        return desc;
    }
}
