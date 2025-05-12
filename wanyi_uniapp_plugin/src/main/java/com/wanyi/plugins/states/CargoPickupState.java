package com.wanyi.plugins.states;

/**
 * 货架感应器状态
 */
public class CargoPickupState {

    //货栏空
    public static final int EMPTY = 0;

    //货栏满
    public static final int FULL = 1;


    private static int currentState = EMPTY;

    public static int getCurrentState() {
        return currentState;
    }

    public static synchronized void setCurrentState(int currentState) {
        CargoPickupState.currentState = currentState;
    }
}
