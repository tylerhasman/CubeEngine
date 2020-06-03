package me.cube.engine.test;

import me.cube.engine.Game;
import me.cube.engine.Terrain;
import me.cube.engine.Voxel;
import me.cube.engine.VoxelModel;
import me.cube.engine.file.VoxFile;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MathUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class TestGame implements Game {

    private Vector3f cameraPosition;
    private Vector3f cameraRotation;
    private float cameraDistance;
    private Matrix4f camera;

    private float time;

    private List<Voxel> voxels;

    private Terrain terrain;

    private Voxel lookAt;

    private boolean[] keys;

    public TestGame(){

        keys = new boolean[1024];

        voxels = new ArrayList<>();
        cameraPosition = new Vector3f();
        cameraRotation = new Vector3f();
        camera = new Matrix4f().identity();
        terrain = new Terrain(50, 30, 50);

        cameraDistance = 3f;

        try {
            VoxFile voxFile = new VoxFile("chr_rain.vox");
            voxels.add(lookAt = new Voxel(new VoxelModel(voxFile.toVoxelColorArray(), voxFile.width(), voxFile.height(), voxFile.length())));

            lookAt.position.set(0, 0, 15);

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
        cameraRotation.y += x / 400f;
    }

    @Override
    public void update(float delta) {
        time += delta;

        lookAt.scale.set(new Vector3f(0.1f, 0.1f, 0.1f));

        camera.identity().translate(cameraPosition).rotate(cameraRotation.y, 0f, 1f, 0f);

        camera.lookAt(cameraPosition, lookAt.position, new Vector3f(0, 1, 0));

        terrain.scale.set(10f, 10f, 10f);

    }

    @Override
    public void render() {

        glMatrixMode(GL11.GL_PROJECTION);
        glLoadIdentity();
        glFrustum(-1, 1, -1, 1, 1f, 1000f);

        glEnable(GL_DEPTH_TEST);

        glBegin(GL_QUADS);
        {

            for(Voxel voxel : voxels){
                VoxelModel voxelModel = voxel.model;

                Matrix4f transform = voxel.transform.identity().scale(voxel.scale).translate(voxel.position).rotate(voxel.rotation);

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
