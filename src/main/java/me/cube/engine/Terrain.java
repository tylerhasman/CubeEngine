package me.cube.engine;

import javafx.scene.paint.Color;
import org.joml.Vector3f;

import java.util.Random;

public class Terrain extends Voxel {

    private int[][][] blocks;

    public Terrain(int width, int height, int length){
        blocks = new int[width][height][length];

        Random random = new Random();

        for(int i = 0; i < width;i++){
            for(int j = 0; j < length;j++){
                blocks[i][0][j] = 0x0AAA00 + (random.nextInt(50) << 8);
            }
        }

        model = new VoxelModel(blocks, width, height, length, false);
        scale.mul(10f);
    }

    public boolean isSolid(Vector3f position){
        Vector3f inverseScale = new Vector3f(1f / scale.x, 1f / scale.y, 1f / scale.z);
        Vector3f pos = new Vector3f();

        position.mul(inverseScale, pos);

        return isSolid((int) pos.x, (int) pos.y, (int) pos.z);
    }

    private boolean isSolid(int x, int y, int z){
        if(x < 0 || y < 0 || z < 0 || x >= blocks.length || y >= blocks[0].length || z >= blocks[0][0].length){
            return true;
        }
        return blocks[x][y][z] != 0;
    }

}
