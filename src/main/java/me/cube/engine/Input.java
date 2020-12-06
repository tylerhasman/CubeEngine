package me.cube.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Input {

    //TODO: This should be in a class in the game package
    public static int ACTION_FORWARD = 0,
            ACTION_BACK = 1,
            ACTION_LEFT = 2,
            ACTION_RIGHT = 3,
            ACTION_JUMP = 4,
            ACTION_ATTACK_PRIMARY = 5,
            ACTION_ROLL = 6,
            ACTION_ATTACK_SECONDAY = 7;


    public static int ACTION_EDITOR_SPEED = 19;

    private static final boolean[] actions = new boolean[20];

    private static long windowHandle = 0;

    public static boolean isActionActive(int action){
        return actions[action];
    }

    public static void setActionState(int action, boolean state){
        actions[action] = state;
    }

    protected static void setWindowHandle(long handle){
        windowHandle = handle;
    }

    /**
     * @see org.lwjgl.glfw.GLFW#glfwSetInputMode(long, int, int)
     */
    public static void setCursorMode(int cursorMode){
        if(windowHandle != NULL){
            glfwSetInputMode(windowHandle, GLFW_CURSOR, cursorMode);

            if(cursorMode == GLFW_CURSOR_HIDDEN){
                int[] width = new int[1];
                int[] height = new int[1];
                glfwGetWindowSize(windowHandle, width, height);
                glfwSetCursorPos(windowHandle, width[0] / 2f, height[0] / 2f);
            }

        }
    }

}
