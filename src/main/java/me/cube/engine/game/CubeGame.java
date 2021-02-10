package me.cube.engine.game;

import me.cube.engine.Camera;
import me.cube.engine.Game;
import me.cube.engine.Input;
import me.cube.engine.file.Assets;
import me.cube.engine.game.entity.Player;
import me.cube.engine.game.world.Chunk;
import me.cube.engine.game.world.Terrain;
import me.cube.engine.game.world.World;
import me.cube.engine.model.Mesh;
import me.cube.engine.util.MathUtil;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.opengl.GL11;

import static me.cube.engine.Input.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class CubeGame implements Game {

    public static CubeGame game;

    private World world;

    private float yaw, pitch;
    private float distanceFromTarget;
    private float visualDistanceFromTarget;

    public static float time;

    private Player player;

    @Override
    public void init() {
        time = 0;
        game = this;
        world = new World();
        Camera.projectionMatrix = new Matrix4f()
                .perspective(Math.toRadians(90f), 1280f / 720f, 0.5f, 5000);

        Camera.cameraMatrix = new Matrix4f();
        //combined = new Matrix4f();

        distanceFromTarget = 60;
        visualDistanceFromTarget = distanceFromTarget;
        yaw = 0f;
        pitch = 45;

        player = new Player(world);

        player.position.set(0, 500, 0);

        world.addEntity(player);

        Input.setCursorMode(GLFW_CURSOR_HIDDEN);

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
            p.add(player.position.x, player.position.y + 10, player.position.z);
            if(world.getTerrain().isSolid(new Vector3f(p.x, p.y, p.z))){
                break;
            }
        }

        Camera.cameraMatrix.identity().lookAt(p.x, p.y, p.z,
                player.position.x, player.position.y + 10, player.position.z,
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
    public void render() {

        {

            glPolygonMode(GL_FRONT, GL_LINE);
            glPolygonMode(GL_BACK, GL_LINE);

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_MULTISAMPLE);

            {
                world.render();
            }

            glDisable(GL_MULTISAMPLE);
            glDisable(GL_DEPTH_TEST);

            glPolygonMode(GL_FRONT, GL_FILL);
            glPolygonMode(GL_BACK, GL_FILL);
        }

    }

    @Override
    public void onMousePress(int button, int action) {
        if(button == GLFW_MOUSE_BUTTON_LEFT){
            Input.setActionState(ACTION_ATTACK_PRIMARY, action == GLFW_PRESS);
        }else if(button == GLFW_MOUSE_BUTTON_MIDDLE){
            Input.setActionState(ACTION_ROLL, action == GLFW_PRESS);
        }else if(button == GLFW_MOUSE_BUTTON_RIGHT){
            Input.setActionState(ACTION_ATTACK_SECONDAY, action == GLFW_PRESS || action == GLFW_REPEAT);
        }



        if(button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
            Input.setCursorMode(GLFW_CURSOR_HIDDEN);
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

        return buffer.toString();
    }

    @Override
    public void destroy() {
        Assets.disposeAll();
        world.getTerrain().dispose();
    }
}
