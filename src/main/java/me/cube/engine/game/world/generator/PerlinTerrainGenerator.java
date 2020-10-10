package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.Chunk;
import me.cube.engine.util.MathUtil;
import me.cube.engine.util.PerlinNoise;

import java.util.Random;

public class PerlinTerrainGenerator implements TerrainGenerator{

    private static final PerlinNoise terrainHeightNoise = new PerlinNoise(423807);//Randomly chosen
    private static final PerlinNoise biomeNoise = new PerlinNoise(213213);//Randomly chosen
    private static final PerlinNoise colorNoise = new PerlinNoise(342121);//Randomly chosen
    private static final PerlinNoise tempNoise = new PerlinNoise(423555);//Randomly chosen
    private static final float LARGE_NUMBER = 10_000;

    public Biome biomeAt(int x, int z){
        float genCoordX = x / 300f;
        float genCoordZ = z / 300f;

        double noise = biomeNoise.noise(genCoordX, genCoordZ);

        if(noise < 0.2f){
            return Biome.PLAINS;
        }

        return Biome.MOUNTAINS;
    }

    @Override
    public void generateChunk(Chunk chunk) {
        for(int i = 0; i < Chunk.CHUNK_WIDTH;i++){
            for(int j = 0; j < Chunk.CHUNK_WIDTH;j++){
                float genCoordX = (chunk.getChunkX() * Chunk.CHUNK_WIDTH + i) / 85f;
                float genCoordZ = (chunk.getChunkZ() * Chunk.CHUNK_WIDTH + j) / 85f;
                int r = 0, g = 0, b = 0;

                Biome biome = biomeAt(chunk.getChunkX() * Chunk.CHUNK_WIDTH + i, chunk.getChunkZ() * Chunk.CHUNK_WIDTH + j);

                int height = (int) (terrainHeightNoise.noise(genCoordX, genCoordZ) * Chunk.CHUNK_HEIGHT) + 1;

                float coloring = (float) (colorNoise.noise(genCoordX, genCoordZ) * 0.3f) + 0.7f;
                float tempurature = (float) tempNoise.noise(genCoordX, genCoordZ);


                if(biome == Biome.PLAINS){
                    height /= 10;

                    if(tempurature < 0.1f){
                        g = 0x8E / 2;
                        b = 0x1C;
                    }else{
                        r = 28;
                        g = 130;
                        b = 47;
                        coloring = (float) (colorNoise.noise(genCoordX / 4, genCoordZ / 4) * 0.5f) + 0.5f;
                        height--;
                    }

                }else if(biome == Biome.MOUNTAINS){
                    r = 0x40;
                    g = 0x40;
                    b = 0x40;
                }

                height += 5;

                r = (int) (coloring * r);
                g = (int) (coloring * g);
                b = (int) (coloring * b);

                height = Math.max(1, height);
                for(int y = 0; y < Chunk.CHUNK_HEIGHT;y++){
                    if(y <= height){
                        chunk.blocks[i][y][j] = ((r << 16) + (g << 8) + b);
                    }
                }
            }
        }
    }
}
