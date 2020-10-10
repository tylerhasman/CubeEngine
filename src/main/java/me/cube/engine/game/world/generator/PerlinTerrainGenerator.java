package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.Chunk;
import me.cube.engine.util.PerlinNoise;

public class PerlinTerrainGenerator implements TerrainGenerator{

    private static final PerlinNoise terrainHeightNoise = new PerlinNoise(423807);//Randomly
    private static final PerlinNoise biomeNoise = new PerlinNoise(213213);//Randomly chosen
    private static final float LARGE_NUMBER = 10_000;

    private Biome biomeAt(int x, int z){
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

                height = Math.max(1, height);

                if(biome == Biome.PLAINS){
                    height /= 10;
                    g = 0x8E;
                    b = 0x1C;
                }else if(biome == Biome.MOUNTAINS){
                    r = 0x40;
                    g = 0x40;
                    b = 0x40;
                }



                for(int y = 0; y < Chunk.CHUNK_HEIGHT;y++){
                    if(y <= height){
                        chunk.blocks[i][y][j] = ((r << 16) + (g << 8) + b);
                    }
                }
            }
        }
    }
}
