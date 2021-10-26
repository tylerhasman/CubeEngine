package me.cube.game;

import me.cube.engine.Camera;
import me.cube.engine.Game;
import me.cube.engine.Input;
import me.cube.engine.Renderer;
import me.cube.engine.file.Assets;
import me.cube.game.entity.Player;
import me.cube.game.world.Chunk;
import me.cube.game.world.World;
import me.cube.engine.util.MathUtil;
import org.joml.*;
import org.joml.Math;

import static me.cube.engine.Input.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class CubeGame extends Game {

    public static CubeGame game;

    private World world;

    private float yaw, pitch;
    private float distanceFromTarget;
    private float visualDistanceFromTarget;

    public static float time;

    private Player player;

    private boolean wireFrame;

    @Override
    public void init() {
        time = 0;
        game = this;
        world = new World();
        wireFrame = false;
        Camera.projectionMatrix = new Matrix4f()
                .perspective(Math.toRadians(90f), 1280f / 720f, 0.5f, 5000);

        Camera.cameraMatrix = new Matrix4f();
        //combined = new Matrix4f();

        distanceFromTarget = 6;
        visualDistanceFromTarget = distanceFromTarget;
        yaw = 0f;
        pitch = 45;

        player = new Player(world);

        player.position.set(10000, 35, 10000);

        world.addEntity(player);

    }

    public float getYaw() {
        return yaw;
    }

    public Vector3f getCameraForward(){
        float rYaw = Math.toRadians(yaw);
        float rPitch = Math.toRadians(pitch);

        Matrix4f m = new Matrix4f().identity().rotate(rYaw, 0, 1, 0).rotate(rPitch, 1, 0, 0);

        Vector4f forward = new Vector4f(0, 0, 1, 0);

        forward.mul(m);

        return new Vector3f(forward.x, forward.y, forward.z);
    }

    private void updateCamera(){
        float distance = Math.abs(distanceFromTarget - visualDistanceFromTarget);
        float speed = distance / 10f;

        visualDistanceFromTarget = MathUtil.moveValueTo(visualDistanceFromTarget, distanceFromTarget, speed);

        Vector3f forward = getCameraForward();

        Vector3f p = new Vector3f();

        for(float f = 0f; f < visualDistanceFromTarget;f += 0.25f){
            forward.mul(f, p);
            p.add(player.position.x, player.position.y + 2, player.position.z);
            if(world.getTerrain().isSolid(new Vector3f(p.x, p.y, p.z))){
                break;
            }
        }

        Camera.cameraMatrix.identity().lookAt(p.x, p.y, p.z,
                player.position.x, player.position.y + 2, player.position.z,
                0, 1, 0);


    }

    public World getWorld() {
        return world;
    }

    @Override
    public void update(float delta) {

        world.update(delta, player.position);
        updateCamera();

        time += delta;
    }

    @Override
    public void render(Renderer renderer) {

        world.render(renderer, player.position);


    }

    @Override
    public void onMousePress(int button, int action) {

        if(button == GLFW_MOUSE_BUTTON_LEFT){
            if(action == GLFW_PRESS){
                Input.setActionState(ACTION_CAMERA_LOCK_TURN, true);
                Input.setCursorMode(GLFW_CURSOR_HIDDEN);
            }else{
                Input.setActionState(ACTION_CAMERA_LOCK_TURN, false);
                Input.setCursorMode(GLFW_CURSOR_NORMAL);
            }
        }

        if(button == GLFW_MOUSE_BUTTON_RIGHT){
            if(action == GLFW_PRESS){
                Input.setActionState(ACTION_STRAFE, true);
                Input.setCursorMode(GLFW_CURSOR_HIDDEN);
            }else{
                Input.setActionState(ACTION_STRAFE, false);
                Input.setCursorMode(GLFW_CURSOR_NORMAL);
            }
        }

    }

    @Override
    public void onMouseScroll(double delta) {
        distanceFromTarget += -delta * 1;
        if(distanceFromTarget < 2){
            distanceFromTarget = 2;
        }else if(distanceFromTarget > 15){
            distanceFromTarget = 15;
        }
    }

    @Override
    public void onKeyPress(int key, int action) {

        if(key == GLFW_KEY_ESCAPE){
            Input.setCursorMode(GLFW_CURSOR_NORMAL);
        }

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

        if(key == GLFW_KEY_F6){
            //TODO: Reload all materials
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
    public String getTitle() {

        StringBuffer buffer = new StringBuffer();
        buffer.append("Cube Game - ");

        if(player != null){

            int x = (int)(player.position.x / World.WORLD_SCALE);
            int z = (int)(player.position.z / World.WORLD_SCALE);

            buffer.append("Position: (");
            buffer.append(x);
            buffer.append("/");
            buffer.append(z);
            buffer.append(")");

            int chunkX = Chunk.worldToChunk((int)(player.position.x / World.WORLD_SCALE));
            int chunkZ = Chunk.worldToChunk((int)(player.position.z / World.WORLD_SCALE));

            buffer.append("Chunk: (");
            buffer.append(chunkX);
            buffer.append("/");
            buffer.append(chunkZ);
            buffer.append(")");

            x -= chunkX * Chunk.CHUNK_WIDTH;
            z -= chunkZ * Chunk.CHUNK_WIDTH;

            buffer.append("Pos in Chunk: (");
            buffer.append(x);
            buffer.append("/");
            buffer.append(z);
            buffer.append(")");


        }

        if(world != null)
            buffer.append("     ").append(world.getWorldTimeFormatted());

        return buffer.toString();
    }

    @Override
    public void destroy() {
        Assets.disposeAll();
        world.getTerrain().dispose();
    }
}
