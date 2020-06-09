package me.cube.engine.game;

public class Input {

    public static int ACTION_FORWARD = 0,
            ACTION_BACK = 1,
            ACTION_LEFT = 2,
            ACTION_RIGHT = 3,
            ACTION_JUMP = 4,
            ACTION_ATTACK_PRIMARY = 5,
            ACTION_ROLL = 6,
            ACTION_ATTACK_SECONDAY = 7;

    private static final boolean[] actions = new boolean[20];

    public static boolean isActionActive(int action){
        return actions[action];
    }

    protected static void setActionState(int action, boolean state){
        actions[action] = state;
    }

}
