package me.cube.engine.editor;

import me.cube.engine.Camera;
import me.cube.engine.Game;
import me.cube.engine.Input;
import me.cube.engine.game.world.Terrain;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static me.cube.engine.Input.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_MULTISAMPLE;

public class EditorGame implements Game {

    private Terrain terrain;

    private Vector3f cameraPosition;
    private float yaw, pitch;

    @Override
    public void init() {

        cameraPosition = new Vector3f();

        cameraPosition.y = 250;

        terrain = new Terrain(10);

        Camera.projectionMatrix = new Matrix4f()
                .perspective(Math.toRadians(90f), 1280f / 720f, 0.5f, 5000);

        Camera.cameraMatrix = new Matrix4f();

        yaw = 0f;
        pitch = 45f;

    }

    public Vector3f getCameraForward(){
        float rYaw = Math.toRadians(yaw);
        float rPitch = Math.toRadians(pitch);

        Matrix4f m = new Matrix4f().identity().rotate(rYaw, 0, 1, 0).rotate(rPitch, 1, 0, 0);

        Vector4f forward = new Vector4f(0, 0, 1, 0);

        forward.mul(m);

        return new Vector3f(forward.x, forward.y, forward.z);
    }

    @Override
    public void update(float delta) {

        float speed = 300f;

        if(Input.isActionActive(ACTION_EDITOR_SPEED)){
            speed = 600f;
        }

        speed *= delta;

        Vector3f forward = getCameraForward();

        Vector3f left = new Vector3f();
        forward.mul(speed);

        forward.rotateAxis(Math.toRadians(90f), 0, 1, 0, left);

        left.y = 0;

        if(Input.isActionActive(ACTION_FORWARD)){
            cameraPosition.add(forward);
        }else if(Input.isActionActive(ACTION_BACK)){
            cameraPosition.sub(forward);
        }

        if(Input.isActionActive(ACTION_LEFT)){
            cameraPosition.add(left);
        }else if(Input.isActionActive(ACTION_RIGHT)){
            cameraPosition.sub(left);
        }

        if(Input.isActionActive(ACTION_JUMP)){
            cameraPosition.y += speed;
        }

        Camera.cameraMatrix.identity().lookAt(cameraPosition.x, cameraPosition.y, cameraPosition.z,
                cameraPosition.x + forward.x, cameraPosition.y + forward.y, cameraPosition.z + forward.z,
                0, 1, 0);

        terrain.updateTerrain(new Vector3f(cameraPosition));


    }

    @Override
    public void render() {

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_MULTISAMPLE);

        terrain.render();

        glDisable(GL_MULTISAMPLE);
        glDisable(GL_DEPTH_TEST);
    }

    @Override
    public void onKeyPress(int key, int action) {

        if(key == GLFW_KEY_W){
            Input.setActionState(ACTION_FORWARD, action == GLFW_PRESS || action == GLFW_REPEAT);
        }

        if(key == GLFW_KEY_S){
            Input.setActionState(ACTION_BACK, action == GLFW_PRESS || action == GLFW_REPEAT);
        }

        if(key == GLFW_KEY_A){
            Input.setActionState(ACTION_LEFT, action == GLFW_PRESS || action == GLFW_REPEAT);
        }

        if(key == GLFW_KEY_D){
            Input.setActionState(ACTION_RIGHT, action == GLFW_PRESS || action == GLFW_REPEAT);
        }

        if(key == GLFW_KEY_SPACE){
            Input.setActionState(ACTION_JUMP, action == GLFW_PRESS || action == GLFW_REPEAT);
        }

        if(key == GLFW_KEY_LEFT_SHIFT){
            Input.setActionState(ACTION_EDITOR_SPEED, action == GLFW_PRESS || action == GLFW_REPEAT);
        }
    }

    @Override
    public void onCursorMove(double dx, double dy) {
        yaw -= dx / 9f;
        pitch -= dy / 9f;
        if(pitch > 89){
            pitch = 89;
        }else if(pitch < -89){
            pitch = -89;
        }
    }

    @Override
    public void onMouseScroll(double delta) {

    }

    @Override
    public void onMousePress(int button, int action) {

        if(button == GLFW_MOUSE_BUTTON_2){
            if(action == GLFW_PRESS){
                Input.setCursorMode(GLFW_CURSOR_HIDDEN);
            }else{
                Input.setCursorMode(GLFW_CURSOR_NORMAL);
            }
        }

    }

    @Override
    public void destroy() {

    }
}
