package me.cube.engine;

import javafx.scene.paint.Color;

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

        model = new VoxelModel(blocks, width, height, length);
    }

}
