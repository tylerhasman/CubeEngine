package me.cube.engine.test;

import me.cube.engine.Game;
import me.cube.engine.Terrain;
import me.cube.engine.Voxel;
import me.cube.engine.VoxelModel;
import me.cube.engine.file.VoxFile;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class TestGame implements Game {

    private float cameraDistance;
    private float pitch, angleAroundCharacter;
    private Matrix4f camera;

    private float time;

    private List<Voxel> voxels;

    private Terrain terrain;

    private Voxel player;

    private boolean[] keys;

    public TestGame(){

        keys = new boolean[1024];

        voxels = new ArrayList<>();
        camera = new Matrix4f().identity();
        terrain = new Terrain();

        cameraDistance = 50f;

        try {
            VoxFile voxFile = new VoxFile("chr_rain.vox");
            voxels.add(player = new Voxel(new VoxelModel(voxFile.toVoxelColorArray(), voxFile.width(), voxFile.height(), voxFile.length())));

            voxFile = new VoxFile("tree.vox");

            VoxelModel treeModel = new VoxelModel(voxFile.toVoxelColorArray(), voxFile.width(), voxFile.height(), voxFile.length());

            Random random = new Random();

            for(int i = 0; i < 6;i++){
                Voxel tree = new Voxel(treeModel);
                tree.position.set((random.nextFloat() - 0.5f) * 50 * 5, 30, (random.nextFloat() - 0.5f) * 50 * 5);
                tree.scale.set(2.5f);
                voxels.add(tree);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        voxels.add(terrain);


    }

    @Override
    public void onKeyPress(int key, int action) {
        if(action == GLFW_PRESS){
            keys[key] = true;
        }else if(action == GLFW_RELEASE){
            keys[key] = false;
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void onCursorMove(double x, double y) {
        pitch -= y / 400f;
        if(pitch < -Math.PI / 2f){
            pitch = (float) (-Math.PI / 2f);
        }else if(pitch > Math.PI / 2f){
            pitch = (float) (Math.PI / 2f);
        }
        angleAroundCharacter += x / 400f;
    }

    @Override
    public void update(float delta) {
        time += delta;

        terrain.scale.set(10, 10, 10);

        player.scale.set(new Vector3f(1f, 1f, 1f));
        player.position.y = 8f;

        Vector3f cameraPosition = new Vector3f(Math.cos(angleAroundCharacter) * cameraDistance, pitch * cameraDistance, Math.sin(angleAroundCharacter) * cameraDistance);

        cameraPosition.add(player.position);

        camera.identity().lookAt(cameraPosition, player.position, new Vector3f(0, 1, 0));

        player.velocity.set(0);

        if(keys[GLFW_KEY_W]){
            player.rotation.set(new AxisAngle4f((float) (-angleAroundCharacter + java.lang.Math.PI / 2f), 0, 1, 0));
            player.velocity.set(Math.cos(angleAroundCharacter + Math.PI) * 75, 0f, Math.sin(angleAroundCharacter + Math.PI) * 75);
        }else if(keys[GLFW_KEY_S]){
            player.rotation.set(new AxisAngle4f((float) (-angleAroundCharacter - java.lang.Math.PI / 2f), 0, 1, 0));
            player.velocity.set(Math.cos(angleAroundCharacter) * 75, 0f, Math.sin(angleAroundCharacter) * 75);
        }

        if(keys[GLFW_KEY_A]){
            player.rotation.set(new AxisAngle4f((float) (-angleAroundCharacter - java.lang.Math.PI), 0, 1, 0));
            player.velocity.add((float) Math.cos(angleAroundCharacter + Math.PI / 2f) * 75, 0f, (float) (Math.sin(angleAroundCharacter + Math.PI / 2f) * 75));
        }else if(keys[GLFW_KEY_D]){
            player.rotation.set(new AxisAngle4f((float) (-angleAroundCharacter), 0, 1, 0));
            player.velocity.add((float) Math.cos(angleAroundCharacter - Math.PI / 2f) * 75, 0f, (float) (Math.sin(angleAroundCharacter - Math.PI / 2f) * 75));
        }

        for(Voxel voxel : voxels){
            voxel.update(delta);
        }

    }

    @Override
    public void render() {

        glMatrixMode(GL11.GL_PROJECTION);
        glLoadIdentity();
        glFrustum(-1, 1, -1, 1, 1f, 5000f);

        glEnable(GL_DEPTH_TEST);

        glBegin(GL_LIGHTING);

        glBegin(GL_QUADS);
        {

            for(Voxel voxel : voxels){
                VoxelModel voxelModel = voxel.model;

                Matrix4f transform = voxel.transform.identity().translate(voxel.position).rotate(voxel.rotation).scale(voxel.scale);

                float[] verts = voxelModel.vertices;
                float[] colors = voxelModel.colors;

                Vector3f pos = new Vector3f();

                int colorIndex = 0;

                for(int i = 0; i < verts.length;i += 12){

                    float red = colors[colorIndex++];
                    float green = colors[colorIndex++];
                    float blue = colors[colorIndex++];

                    glColor3f(red, green, blue);

                    for(int j = 0; j < 12;j += 3){
                        pos.set(verts[i + j], verts[i + j + 1], verts[i + j + 2]);
                        pos = transform.transformPosition(pos);
                        pos = camera.transformPosition(pos);

                        glVertex3f(pos.x, pos.y, pos.z);
                    }

                }
            }


        }
        glEnd();

        glDisable(GL_DEPTH_TEST);

    }


}
