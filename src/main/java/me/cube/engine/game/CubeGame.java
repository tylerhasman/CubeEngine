package me.cube.engine.game;

import me.cube.engine.Game;
import me.cube.engine.file.Assets;
import me.cube.engine.util.MathUtil;
import org.joml.*;
import org.joml.Math;

import static me.cube.engine.game.Input.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class CubeGame implements Game {

    public static CubeGame game;

    private World world;

    private Matrix4f projectionMatrix;
    private Matrix4f cameraMatrix;
    private Matrix4f combined;

    private float yaw, pitch;
    private float distanceFromTarget;
    private float visualDistanceFromTarget;

    @Override
    public void init() {
        game = this;
        world = new World();
        projectionMatrix = new Matrix4f()
                .perspective(Math.toRadians(90f), 1280f / 720f, 0.01f, 2000);

        cameraMatrix = new Matrix4f();
        combined = new Matrix4f();

        distanceFromTarget = 60;
        visualDistanceFromTarget = distanceFromTarget;
        yaw = 0f;
        pitch = 45;
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

        float distance = Math.abs(distanceFromTarget - visualDistanceFromTarget);
        float speed = distance / 10f;

        visualDistanceFromTarget = MathUtil.moveValueTo(visualDistanceFromTarget, distanceFromTarget, speed);

        projectionMatrix.mul(cameraMatrix, combined);

        Entity player = world.getPlayer();

        Vector3f forward = getCameraForward();

        Vector3f p = new Vector3f();

        for(float f = 0f; f < visualDistanceFromTarget;f += 0.25f){
            forward.mul(f, p);
            p.add(player.position.x, player.position.y + 10, player.position.z);
            if(world.getTerrain().isSolid(new Vector3f(p.x, p.y, p.z))){
                break;
            }
        }

        p.sub(forward);


        cameraMatrix.identity().lookAt(p.x, p.y, p.z,
                        player.position.x, player.position.y + 10, player.position.z,
                        0, 1, 0);


        world.update(delta);
    }

    @Override
    public void render() {

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glLoadMatrixf(combined.get(new float[16]));
        {
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_MULTISAMPLE);

            {
                world.render();
            }

            glDisable(GL_MULTISAMPLE);
            glDisable(GL_DEPTH_TEST);
        }


    }

    @Override
    public void onMousePress(int button, int action) {
        if(button == GLFW_MOUSE_BUTTON_LEFT){
            Input.setActionState(ACTION_ATTACK_PRIMARY, action == GLFW_PRESS);
        }else if(button == GLFW_MOUSE_BUTTON_MIDDLE){
            Input.setActionState(ACTION_ROLL, action == GLFW_PRESS);
        }
    }

    @Override
    public void onMouseScroll(double delta) {
        distanceFromTarget += -delta * 10;
        if(distanceFromTarget < 20){
            distanceFromTarget = 20;
        }else if(distanceFromTarget > 150){
            distanceFromTarget = 150;
        }
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
            Input.setActionState(ACTION_JUMP, action == GLFW_PRESS);
        }
    }

    @Override
    public void onCursorMove(double dx, double dy) {
        yaw -= dx / 9f;
        pitch += dy / 9f;
        if(pitch > 89){
            pitch = 89;
        }else if(pitch < -89){
            pitch = -89;
        }
    }

    @Override
    public void destroy() {
        Assets.disposeAll();
    }
}
