package me.cube.engine.test;

import me.cube.engine.Game;
import me.cube.engine.Voxel;
import me.cube.engine.VoxelModel;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class TestGame implements Game {

    private Matrix4f camera;

    private float time;

    private List<Voxel> voxels;
    private Voxel testVoxel;

    public TestGame(){

        camera = new Matrix4f().identity();

        int[][][] cube = new int[4][4][4];

        Random random = new Random();

        for(int i = 0; i < 4;i++){
            for(int j = 0; j < 4;j++){
                for(int k = 0; k < 4;k++){
                    if(random.nextBoolean()){
                        continue;
                    }
                    cube[i][j][k] = random.nextInt(0xFFFFFF);
                }
            }
        }

        VoxelModel voxelModel = new VoxelModel(cube, 4, 4, 4);

        voxels = new ArrayList<>();

        voxels.add(testVoxel = new Voxel(voxelModel));

    }

    @Override
    public void update(float delta) {
        time += delta;
        camera.identity().translate(0f, 0f, -2f);
        VoxelModel model = testVoxel.model;
        testVoxel.transform.identity().rotate(time, 1f, 0f, 0f).rotate(time, 0f, 1f, 0f).scale(0.2f);
    }

    @Override
    public void render() {

        glMatrixMode(GL11.GL_PROJECTION);
        glLoadIdentity();
        glFrustum(-1, 1, -1, 1, 1f, 20f);
        glMatrixMode(GL_MODELVIEW);

        glEnable(GL_DEPTH_TEST);

        glBegin(GL_QUADS);
        {

            for(Voxel voxel : voxels){
                VoxelModel voxelModel = voxel.model;

                Matrix4f transform = voxel.transform;

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
