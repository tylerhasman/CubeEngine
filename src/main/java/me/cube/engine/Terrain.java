package me.cube.engine;

import javafx.scene.paint.Color;
import me.cube.engine.file.VxmFile;
import org.joml.AABBf;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Random;

public class Terrain extends Voxel {

    private int[][][] blocks;

    public Terrain(int width, int height, int length){
        blocks = new int[width][height][length];

        Random random = new Random();

        for(int i = 0; i < width;i++){
            for(int j = 0; j < length;j++){
                if(i == 0 || j == 0 || i == width-1 || j == length-1){
                    blocks[i][1][j] = 0x0AAA00 + (random.nextInt(50) << 8);
                }
                blocks[i][0][j] = 0x0AAA00 + (random.nextInt(50) << 8);
            }
        }

        try {
            VxmFile tree = new VxmFile("assets/models/tree.vxm");

            int[][][] pixels = tree.toVoxelColorArray();

            for(int i = 0; i < tree.width();i++){
                for(int j = 0; j < tree.height();j++){
                    for(int k = 0; k < tree.length();k++){
                        blocks[i + 30][j + 1][k + 30] = pixels[i][j][k];
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        model = new VoxelModel(blocks, width, height, length, false);
        scale.mul(10f);
    }

    public boolean isSolid(Vector3f position){
        return isSolid(position.x, position.y, position.z);
    }

    public boolean isSolid(float x, float y, float z){
        Vector3f inverseScale = new Vector3f(1f / scale.x, 1f / scale.y, 1f / scale.z);

        x *= inverseScale.x;
        y *= inverseScale.y;
        z *= inverseScale.z;

        return isSolid((int) x, (int) y, (int) z);
    }

    private boolean isSolid(int x, int y, int z){
        if(x < 0 || y < 0 || z < 0 || x >= blocks.length || y >= blocks[0].length || z >= blocks[0][0].length){
            return false;
        }
        return blocks[x][y][z] != 0;
    }

    public boolean isColliding(AABBf boundingBox) {
        for(float x = boundingBox.minX;x <= boundingBox.maxX; x += scale.x / 2f){
            for(float y = boundingBox.minY;y <= boundingBox.maxY; y += scale.y / 2f){
                for(float z = boundingBox.minZ;z <= boundingBox.maxZ; z += scale.z / 2f){
                    if(isSolid(x, y, z)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
