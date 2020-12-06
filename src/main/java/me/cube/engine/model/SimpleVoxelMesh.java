package me.cube.engine.model;

import me.cube.engine.util.FloatArray;
import org.joml.Vector3f;

import java.util.List;

public class SimpleVoxelMesh extends VoxelMesh {

    public SimpleVoxelMesh(int[][][] cubes, int width, int height, int length){
        this(cubes, width, height, length, true);
    }

    public SimpleVoxelMesh(int[][][] cubes, int width, int height, int length, boolean center){
        super(width, height, length);

        FloatArray vertices = new FloatArray(1024);
        FloatArray colors = new FloatArray(1024);
        FloatArray normals = new FloatArray(1024);

        if(!center){
            pivot.set(width / 2f, height / 2f, length / 2f);
        }

        generate(cubes, vertices, colors, normals, width, height, length, center);

        initialize(vertices.toArray(), colors.toArray(), normals.toArray());
    }

    private void generate(int[][][] cubes, FloatArray vertices, FloatArray colors, FloatArray normals, int width, int height, int length, boolean center) {

        Cube cube = new Cube();

        for(int i = 0; i < width;i++){
            for(int j = 0; j < height;j++){
                for(int k = 0; k < length;k++){

                    int color = cubes[i][j][k];

                    if(color != 0){
                        cube.red = ((color >> 16) & 255) / 255F;
                        cube.green = ((color >> 8) & 255) / 255F;
                        cube.blue = (color & 255) / 255F;

                        cube.x = i;
                        cube.y = j;
                        cube.z = k;

                        if(center){
                            cube.x -= width / 2f;
                            cube.y -= height / 2f;
                            cube.z -= length / 2f;
                        }

                        cube.top = getOrZero(cubes, width, height, length, i, j + 1, k) == 0;
                        cube.bottom = getOrZero(cubes, width, height, length, i, j - 1, k) == 0;
                        cube.north = getOrZero(cubes, width, height, length, i, j, k-1) == 0;
                        cube.south = getOrZero(cubes, width, height, length, i, j, k+1) == 0;
                        cube.east = getOrZero(cubes, width, height, length, i-1, j, k) == 0;
                        cube.west = getOrZero(cubes, width, height, length, i+1, j, k) == 0;

                        cube.flags = 0;

                        cube.generate(vertices, normals, colors);
                    }

                }
            }
        }

    }

    private static int getOrZero(int[][][] cube, int w, int h, int l, int x, int y, int z){
        if(x < 0 || y < 0 || z < 0 || x >= w || y >= h || z >= l){
            return 0;
        }
        return cube[x][y][z];
    }

}