package com.wanyi.plugins.states;

public class ScrewRodState {
    //空闲状态
    public static final int STATE_IDLE = 0;

    //移动中
    public static final int STATE_MOVING = 1;

    //已到达目标点
    public static final int STATE_ARRIVED = 2;

    //有错误
    public static final int STATE_ERROR = 3;

    private static int currentState = STATE_IDLE;

    public static int getCurrentState() {
        return currentState;
    }

    public static synchronized void setCurrentState(int currentState) {
        ScrewRodState.currentState = currentState;
    }


}
