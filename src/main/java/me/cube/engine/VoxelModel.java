package me.cube.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VoxelModel {

    public float[] vertices;//To be passed to glQuad
    public float[] colors;

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

        Random random = new Random();

        for(int i = 0; i < width;i++){
            for(int j = 0; j < height;j++){
                for(int k = 0; k < length;k++){

                    int color = cubes[i][j][k];

                    if(color != 0){

                        float red = ((color >> 16) & 255) / 255F;
                        float green = ((color >> 8) & 255) / 255F;
                        float blue = (color & 255) / 255F;

                        red += (random.nextFloat() - 0.5f) * 0.03f;
                        green += (random.nextFloat() - 0.5f) * 0.03f;
                        blue += (random.nextFloat() - 0.5f) * 0.03f;

                        float x = i - width / 2f;
                        float y = j - height / 2f;
                        float z = k - length / 2f;

                        boolean top = getOrZero(cubes, width, height, length, i, j + 1, k) == 0;
                        boolean bottom = getOrZero(cubes, width, height, length, i, j - 1, k) == 0;
                        boolean north = getOrZero(cubes, width, height, length, i, j, k-1) == 0;
                        boolean south = getOrZero(cubes, width, height, length, i, j, k+1) == 0;
                        boolean east = getOrZero(cubes, width, height, length, i-1, j, k) == 0;
                        boolean west = getOrZero(cubes, width, height, length, i+1, j, k) == 0;

                        int verts = generateCube(vertices, x, y, z, top, bottom, north, south, east, west);

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
    }

    private static int getOrZero(int[][][] cube, int w, int h, int l, int x, int y, int z){
        if(x < 0 || y < 0 || z < 0 || x >= w || y >= h || z >= l){
            return 0;
        }
        return cube[x][y][z];
    }

    private static int generateCube(List<Float> out, float x, float y, float z, boolean top, boolean bottom, boolean north, boolean south, boolean east, boolean west){
        int start = out.size();

        if(north){
            out.add(x);
            out.add(y);
            out.add(z);

            out.add(x + 1f);
            out.add(y);
            out.add(z);

            out.add(x + 1f);
            out.add(y + 1f);
            out.add(z);

            out.add(x);
            out.add(y + 1f);
            out.add(z);
        }

        if(south){
            out.add(x);
            out.add(y);
            out.add(z + 1f);

            out.add(x + 1f);
            out.add(y);
            out.add(z + 1f);

            out.add(x + 1f);
            out.add(y + 1f);
            out.add(z + 1f);

            out.add(x);
            out.add(y + 1f);
            out.add(z + 1f);
        }

        if(top){
            out.add(x);
            out.add(y + 1f);
            out.add(z);

            out.add(x + 1f);
            out.add(y + 1f);
            out.add(z);

            out.add(x + 1f);
            out.add(y + 1f);
            out.add(z + 1f);

            out.add(x);
            out.add(y + 1f);
            out.add(z + 1f);
        }

        if(bottom){
            out.add(x);
            out.add(y);
            out.add(z);

            out.add(x + 1f);
            out.add(y);
            out.add(z);

            out.add(x + 1f);
            out.add(y);
            out.add(z + 1f);

            out.add(x);
            out.add(y);
            out.add(z + 1f);
        }

        if(east){
            out.add(x);
            out.add(y);
            out.add(z);

            out.add(x);
            out.add(y);
            out.add(z + 1f);

            out.add(x);
            out.add(y + 1f);
            out.add(z + 1f);

            out.add(x);
            out.add(y + 1f);
            out.add(z);
        }

        if(west){
            out.add(x + 1f);
            out.add(y);
            out.add(z);

            out.add(x + 1f);
            out.add(y);
            out.add(z + 1f);

            out.add(x + 1f);
            out.add(y + 1f);
            out.add(z + 1f);

            out.add(x + 1f);
            out.add(y + 1f);
            out.add(z);
        }

        return out.size() - start;
    }

    private static float[] toArray(List<Float> vertices){
        float[] out = new float[vertices.size()];

        for(int i = 0; i < out.length;i++){
            out[i] = vertices.get(i);
        }
        return out;
    }

}
