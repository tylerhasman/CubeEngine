package me.cube.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VoxelModel {

    public float[] vertices;//To be passed to glQuad
    public float[] colors;
    public float[] normals;

    public final int width, height, length;

    public VoxelModel(int[][][] cubes, int width, int height, int length){
        this.width = width;
        this.height = height;
        this.length = length;
        generateVertices(cubes);
    }

    private void generateVertices(int[][][] cubes){
        List<Float> vertices = new ArrayList<Float>();
        List<Float> colors = new ArrayList<>();
        List<Float> normals = new ArrayList<>();

        for(int i = 0; i < width;i++){
            for(int j = 0; j < height;j++){
                for(int k = 0; k < length;k++){

                    int color = cubes[i][j][k];

                    if(color != 0){

                        float red = ((color >> 16) & 255) / 255F;
                        float green = ((color >> 8) & 255) / 255F;
                        float blue = (color & 255) / 255F;

                        float x = i - width / 2f;
                        float y = j - height / 2f;
                        float z = k - length / 2f;

                        boolean top = getOrZero(cubes, width, height, length, i, j + 1, k) == 0;
                        boolean bottom = getOrZero(cubes, width, height, length, i, j - 1, k) == 0;
                        boolean north = getOrZero(cubes, width, height, length, i, j, k-1) == 0;
                        boolean south = getOrZero(cubes, width, height, length, i, j, k+1) == 0;
                        boolean east = getOrZero(cubes, width, height, length, i-1, j, k) == 0;
                        boolean west = getOrZero(cubes, width, height, length, i+1, j, k) == 0;

                        int verts = generateCube(vertices, normals, x, y, z, top, bottom, north, south, east, west);

                        for(int vertColor = 0; vertColor < verts / 12;vertColor++){
                            colors.add(red);
                            colors.add(green);
                            colors.add(blue);//For each quad add the color in
                        }

                    }

                }
            }
        }

        this.vertices = toArray(vertices);
        this.colors = toArray(colors);
        this.normals = toArray(normals);
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

            norOut.add(0f);
            norOut.add(0f);
            norOut.add(-1f);
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

            norOut.add(0f);
            norOut.add(0f);
            norOut.add(1f);
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

            norOut.add(0f);
            norOut.add(1f);
            norOut.add(0f);
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

            norOut.add(0f);
            norOut.add(-1f);
            norOut.add(0f);
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

            norOut.add(-1f);
            norOut.add(0f);
            norOut.add(0f);
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

            norOut.add(0f);
            norOut.add(0f);
            norOut.add(1f);
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
