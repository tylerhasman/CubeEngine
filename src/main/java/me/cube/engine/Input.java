package me.cube.engine;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Input {

    //TODO: This should be in a class in the game package
    public static final int ACTION_FORWARD = 0,
            ACTION_BACK = 1,
            ACTION_LEFT = 2,
            ACTION_RIGHT = 3,
            ACTION_JUMP = 4,
            ACTION_ATTACK_PRIMARY = 5,
            ACTION_ROLL = 6,
            ACTION_ATTACK_SECONDAY = 7;


    public static final int ACTION_EDITOR_SPEED = 19;

    public static final int MODIFIER_CONTROL = 0;

    private static final boolean[] actions = new boolean[20];

    private static final boolean[] modifiers = new boolean[10];

    private static long windowHandle = 0;

    public static boolean isActionActive(int action){
        return actions[action];
    }

    public static void setModifier(int modifier, boolean state){
        modifiers[modifier] = state;
    }

    public static boolean isModifierActive(int modifier){
        return modifiers[modifier];
    }

    public static void setActionState(int action, boolean state){
        actions[action] = state;
    }

    protected static void setWindowHandle(long handle){
        windowHandle = handle;
    }

    public static Vector2f getCursorPosition(){
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(windowHandle, x, y);

        return new Vector2f((float) x[0], (float) y[0]);
    }

    public static Vector2f getNormalizedCursorPosition(){

        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetWindowSize(windowHandle, width, height);

        Vector2f cursor = getCursorPosition();

        cursor.x = (2.0f * (cursor.x / width[0])) - 1.0f;
        cursor.y = (2.0f * ((height[0] - cursor.y) / height[0])) - 1.0f;

        return cursor;
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

    public static int getCursorMode(){
        return glfwGetInputMode(windowHandle, GLFW_CURSOR);
    }

}
