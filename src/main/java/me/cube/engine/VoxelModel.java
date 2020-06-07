package me.cube.engine;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL30C.*;

public class VoxelModel {
/*

    private float[] vertices;//To be passed to glQuad
    private float[] colors;
    private float[] normals;
*/

    public final int width, height, length;

    public final Vector3f pivot = new Vector3f();

    private final int vertexHandle, colorHandle, normalHandle;

    private final int indices;

    public VoxelModel(int[][][] cubes, int width, int height, int length){
        this(cubes, width, height, length, true);
    }

    public VoxelModel(int[][][] cubes, int width, int height, int length, boolean center){
        this.width = width;
        this.height = height;
        this.length = length;
        if(!center){
            pivot.set(width / 2f, height / 2f, length / 2f);
        }

        List<Float> vertices = new ArrayList<>();
        List<Float> colors = new ArrayList<>();
        List<Float> normals = new ArrayList<>();

        indices = generateVertices(cubes, center, vertices, colors, normals);

        float[] vertexBufferData = toArray(vertices);
        float[] colorBufferData = toArray(colors);
        float[] normalBufferData = toArray(normals);

        vertices.clear();
        colors.clear();
        normals.clear();

        vertexHandle = glGenBuffers();
        colorHandle = glGenBuffers();
        normalHandle = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexBufferData, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, colorHandle);
        glBufferData(GL_ARRAY_BUFFER, colorBufferData, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, normalHandle);
        glBufferData(GL_ARRAY_BUFFER, normalBufferData, GL_STATIC_DRAW);

    }

    public void render(){
        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
        glVertexPointer(3,GL_FLOAT, 0, 0);

        glEnableClientState(GL_COLOR_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, colorHandle);
        glColorPointer(4, GL_FLOAT, 0, 0);

        glEnableClientState(GL_NORMAL_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, normalHandle);
        glNormalPointer(GL_FLOAT, 0, 0);

        GL11.glDrawArrays(GL11.GL_QUADS, 0, indices);

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);
        glDisableClientState(GL_NORMAL_ARRAY);
    }

    public void dispose(){
        glDeleteBuffers(new int[] {vertexHandle, colorHandle, normalHandle});
    }

    private int generateVertices(int[][][] cubes, boolean center, List<Float> vertices, List<Float> colors, List<Float> normals){

        int indices = 0;

        for(int i = 0; i < width;i++){
            for(int j = 0; j < height;j++){
                for(int k = 0; k < length;k++){

                    int color = cubes[i][j][k];

                    if(color != 0){

                        float red = ((color >> 16) & 255) / 255F;
                        float green = ((color >> 8) & 255) / 255F;
                        float blue = (color & 255) / 255F;

                        float x = i;
                        float y = j;
                        float z = k;

                        if(center){
                            x -= width / 2f;
                            y -= height / 2f;
                            z -= length / 2f;
                        }

                        boolean top = getOrZero(cubes, width, height, length, i, j + 1, k) == 0;
                        boolean bottom = getOrZero(cubes, width, height, length, i, j - 1, k) == 0;
                        boolean north = getOrZero(cubes, width, height, length, i, j, k-1) == 0;
                        boolean south = getOrZero(cubes, width, height, length, i, j, k+1) == 0;
                        boolean east = getOrZero(cubes, width, height, length, i-1, j, k) == 0;
                        boolean west = getOrZero(cubes, width, height, length, i+1, j, k) == 0;

                        int verts = generateCube(vertices, normals, x, y, z, top, bottom, north, south, east, west);

                        indices += verts;

                        for(int vertColor = 0; vertColor < verts / 3;vertColor++){
                            colors.add(red);
                            colors.add(green);
                            colors.add(blue);//For each quad add the color in
                            colors.add(1.0f);//For each quad add the color in
                        }

                    }

                }
            }
        }

        return indices;
    }

    private static int getOrZero(int[][][] cube, int w, int h, int l, int x, int y, int z){
        if(x < 0 || y < 0 || z < 0 || x >= w || y >= h || z >= l){
            return 0;
        }
        return cube[x][y][z];
    }

    private static int generateCube(List<Float> vertOut, List<Float> norOut, float x, float y, float z, boolean top, boolean bottom, boolean north, boolean south, boolean east, boolean west){
        int start = vertOut.size();

        if(north){
            vertOut.add(x);
            vertOut.add(y);
            vertOut.add(z);

            vertOut.add(x + 1f);
            vertOut.add(y);
            vertOut.add(z);

            vertOut.add(x + 1f);
            vertOut.add(y + 1f);
            vertOut.add(z);

            vertOut.add(x);
            vertOut.add(y + 1f);
            vertOut.add(z);

            for(int i = 0; i < 4;i++){
                norOut.add(0f);
                norOut.add(0f);
                norOut.add(-1f);
            }
        }

        if(south){
            vertOut.add(x);
            vertOut.add(y);
            vertOut.add(z + 1f);

            vertOut.add(x);
            vertOut.add(y + 1f);
            vertOut.add(z + 1f);

            vertOut.add(x + 1f);
            vertOut.add(y + 1f);
            vertOut.add(z + 1f);

            vertOut.add(x + 1f);
            vertOut.add(y);
            vertOut.add(z + 1f);

            for(int i = 0; i < 4;i++){
                norOut.add(0f);
                norOut.add(0f);
                norOut.add(1f);
            }
        }

        if(top){
            vertOut.add(x);
            vertOut.add(y + 1f);
            vertOut.add(z);

            vertOut.add(x + 1f);
            vertOut.add(y + 1f);
            vertOut.add(z);

            vertOut.add(x + 1f);
            vertOut.add(y + 1f);
            vertOut.add(z + 1f);

            vertOut.add(x);
            vertOut.add(y + 1f);
            vertOut.add(z + 1f);

            for(int i = 0; i < 4;i++) {
                norOut.add(0f);
                norOut.add(1f);
                norOut.add(0f);
            }
        }

        if(bottom){

            vertOut.add(x);
            vertOut.add(y);
            vertOut.add(z);

            vertOut.add(x);
            vertOut.add(y);
            vertOut.add(z + 1f);

            vertOut.add(x + 1f);
            vertOut.add(y);
            vertOut.add(z + 1f);

            vertOut.add(x + 1f);
            vertOut.add(y);
            vertOut.add(z);

            for(int i = 0; i < 4;i++) {
                norOut.add(0f);
                norOut.add(-1f);
                norOut.add(0f);
            }
        }

        if(east){
            vertOut.add(x);
            vertOut.add(y);
            vertOut.add(z);

            vertOut.add(x);
            vertOut.add(y + 1f);
            vertOut.add(z);

            vertOut.add(x);
            vertOut.add(y + 1f);
            vertOut.add(z + 1f);

            vertOut.add(x);
            vertOut.add(y);
            vertOut.add(z + 1f);

            for(int i = 0; i < 4;i++){
                norOut.add(-1f);
                norOut.add(0f);
                norOut.add(0f);
            }
        }

        if(west){
            vertOut.add(x + 1f);
            vertOut.add(y);
            vertOut.add(z);

            vertOut.add(x + 1f);
            vertOut.add(y);
            vertOut.add(z + 1f);

            vertOut.add(x + 1f);
            vertOut.add(y + 1f);
            vertOut.add(z + 1f);

            vertOut.add(x + 1f);
            vertOut.add(y + 1f);
            vertOut.add(z);

            for(int i = 0; i < 4;i++){
                norOut.add(0f);
                norOut.add(0f);
                norOut.add(1f);
            }
        }

        return vertOut.size() - start;
    }

    private static float[] toArray(List<Float> vertices){
        float[] out = new float[vertices.size()];
        for(int i = 0; i < out.length;i++){
            out[i] = vertices.get(i);
        }
        return out;
    }

}
