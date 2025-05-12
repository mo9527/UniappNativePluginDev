package com.wanyi.plugins.states;

public class GateState {
    public static final int STATE_CLOSE = 0;
    public static final int STATE_OPEN = 1;

    private static int currentState = STATE_CLOSE;

    public static int getCurrentState() {
        return currentState;
    }

    public static synchronized void setCurrentState(int currentState) {
        GateState.currentState = currentState;
    }
}
