package com.wanyi.plugins.states;

/**
 * 货道推送电机状态
 */
public class CargoPusherState {

    //停止状态
    public static final int STATE_STOP = 0;

    //移动中
    public static final int STATE_MOVING = 1;


    private static int currentState = STATE_STOP;

    public static int getCurrentState() {
        return currentState;
    }

    public static synchronized void setCurrentState(int currentState) {
        CargoPusherState.currentState = currentState;
    }
}
