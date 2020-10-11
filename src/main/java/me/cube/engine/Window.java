package me.cube.engine;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window implements Runnable {

    private static long DEBUG_CHANGE_TIME = 0;

    private static long handle;

    private Game game;

    private String title;
    private int width, height;

    private boolean mouseLocked;

    public Window(Game game, String title, int width, int height){
        this.game = game;
        this.title = title;
        this.width = width;
        this.height = height;
        mouseLocked = false;
        init(title, width, height);
    }

    public static void DEBUG_SetTitle(String title){
        if(System.currentTimeMillis() - DEBUG_CHANGE_TIME < 100){
            return;
        }
        glfwSetWindowTitle(handle, title);
        DEBUG_CHANGE_TIME = System.currentTimeMillis();
    }

    @Override
    public void run() {
        GL.createCapabilities();
        //GLUtil.setupDebugMessageCallback(System.err);

        glClearColor(135f / 255f, 206f / 255f, 235f / 255f, 0.0f);

        long lastFrame = System.currentTimeMillis();

        int fps = 0;
        int fpsCounter = 0;
        long fpsResetTimer = 0;

        game.init();

        while(!glfwWindowShouldClose(handle)){

            long timeSinceLast = System.currentTimeMillis() - lastFrame;
            lastFrame = System.currentTimeMillis();

            fpsResetTimer += timeSinceLast;

            double[] x = new double[1];
            double[] y = new double[1];

            if(mouseLocked){
                glfwGetCursorPos(handle, x, y);

                game.onCursorMove(x[0] - width / 2f, height / 2f - y[0]);

                glfwSetCursorPos(handle, width / 2f, height / 2f);
            }

            game.update(Math.min(timeSinceLast / 1000F, 1F / 60F));

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            game.render();

            glfwSwapBuffers(handle);

            glfwPollEvents();


            fpsCounter++;
            if(fpsResetTimer >= 1000){
                fpsResetTimer = 0;
                fps = fpsCounter;
                fpsCounter = 0;
                //glfwSetWindowTitle(handle, title+" FPS: "+fps);
            }
        }

        game.destroy();


        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);

        glfwTerminate();

        Objects.requireNonNull(glfwSetErrorCallback(null)).free();

    }

    private void init(String title, int width, int height) {
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_SAMPLES, 4);

        handle = glfwCreateWindow(width, height, title, NULL, NULL);

        if(handle == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(handle, new GLFWKeyCallback() {
            @Override
            public void invoke (long window, int key, int scancode, int action, int mods) {
                game.onKeyPress(key, action);

                if(key == GLFW_KEY_ESCAPE){
                    glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                    mouseLocked = false;
                }
            }
        });

        glfwSetMouseButtonCallback(handle, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {

                game.onMousePress(button, action);

                if(button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
                    glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
                    mouseLocked = true;
                }
            }
        });

        glfwSetScrollCallback(handle, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                game.onMouseScroll(yoffset);
            }
        });

        glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

        mouseLocked = true;

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(handle, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    handle,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(handle);

        glfwSwapInterval(1);//V-Sync

        glfwShowWindow(handle);
    }

}
