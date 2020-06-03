package me.cube.engine.test;

import me.cube.engine.Game;
import me.cube.engine.Terrain;
import me.cube.engine.Voxel;
import me.cube.engine.VoxelModel;
import me.cube.engine.file.VoxFile;
import me.cube.engine.shader.ShaderProgram;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class TestGame implements Game {

    private float cameraDistance;
    private float pitch, angleAroundCharacter;
    private Matrix4f camera;

    private float time;

    public static boolean[] keys;

    public TestGame(){

        keys = new boolean[1024];

        camera = new Matrix4f().identity();

        cameraDistance = 50f;

    }

    @Override
    public void init() {

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
/*

        player.scale.set(new Vector3f(1f, 1f, 1f));
        player.position.y = 8f;

        Vector3f cameraPosition = new Vector3f(Math.cos(angleAroundCharacter) * cameraDistance, pitch * cameraDistance, Math.sin(angleAroundCharacter) * cameraDistance);

        cameraPosition.add(player.position);

        camera.identity().perspective(Math.toRadians(90f), 600f / 480f, 1f, 1000f).lookAt(cameraPosition, player.position, new Vector3f(0, 1, 0));
*/

/*        player.velocity.set(0);

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
        }*/

/*
        for(Voxel voxel : voxels){
            voxel.update(delta);
        }
*/

    }

    @Override
    public void render() {
/*


        glMatrixMode(GL11.GL_PROJECTION);

        glLoadIdentity();
        glLoadMatrixf(camera.get(new float[16]));

        glMatrixMode(GL_MODELVIEW);

        glEnable(GL_MULTISAMPLE);

        glEnable(GL_LIGHTING);
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);

        glEnable(GL_LIGHT0);

        glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[] {0.5f, 0.5f, 0.5f, 1f});
        glLightfv(GL_LIGHT0, GL_POSITION, new float[] {player.position.x, player.position.y + 5, player.position.z, 1});

        glEnable(GL_DEPTH_TEST);
        glBegin(GL_QUADS);
        {

            Vector3f position = new Vector3f();

            for(Voxel voxel : voxels){
                VoxelModel voxelModel = voxel.model;

                Matrix4f transform = voxel.transform.identity().translate(voxel.position).rotate(voxel.rotation).scale(voxel.scale);

                float[] verts = voxelModel.vertices;
                float[] colors = voxelModel.colors;
                float[] normals = voxelModel.normals;

                int colorIndex = 0;
                int normalIndex = 0;

                for(int i = 0; i < verts.length;i += 12){

                    float red = colors[colorIndex++];
                    float green = colors[colorIndex++];
                    float blue = colors[colorIndex++];

                    float norX = normals[normalIndex++];
                    float norY = normals[normalIndex++];
                    float norZ = normals[normalIndex++];

                    glColor3f(red, green, blue);
                    glNormal3f(norX, norY, norZ);

                    for(int j = 0; j < 12;j += 3){

                        transform.transformPosition(verts[i + j], verts[i + j + 1], verts[i + j + 2], position);

                        glVertex3f(position.x, position.y, position.z);
                    }

                }


            }


        }
        glEnd();
        glDisable(GL_DEPTH_TEST);

        glDisable(GL_LIGHT0);
        glDisable(GL_COLOR_MATERIAL);
        glDisable(GL_LIGHTING);

        glDisable(GL_MULTISAMPLE);

*/


    }


}
