package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.Chunk;
import me.cube.engine.util.CubicNoise;
import me.cube.engine.util.NoiseGenerator;
import me.cube.engine.util.PerlinNoise;

import java.awt.*;

public class PerlinTerrainGenerator implements TerrainGenerator{

    private static final NoiseGenerator terrainHeightNoise = new PerlinNoise(423807);//Randomly chosen
    private static final NoiseGenerator biomeNoise = new PerlinNoise(213213);//Randomly chosen
    private static final NoiseGenerator colorNoise = new PerlinNoise(342121);//Randomly chosen
    private static final NoiseGenerator tempNoise = new PerlinNoise(423555);//Randomly chosen
    private static final float LARGE_NUMBER = 10_000;

    public Biome biomeAt(int x, int z){
        float genCoordX = x / 300f;
        float genCoordZ = z / 300f;

        double noise = biomeNoise.noise(genCoordX, genCoordZ);


        if(noise < 0.4f){
            return Biome.FOREST;
        }

        if(noise < 0.2f){
            return Biome.PLAINS;
        }

        return Biome.MOUNTAINS;
    }

    public int heightAt(int x, int z){
        float genCoordX = x / 400f;
        float genCoordZ = z / 400f;
        int height = (int) (terrainHeightNoise.noise(genCoordX, genCoordZ) * Chunk.CHUNK_HEIGHT) + 1;
        Biome biome = biomeAt(x, z);
        float tempurature = tempNoise.noise(genCoordX, genCoordZ);

        if(biome == Biome.PLAINS) {

            if (tempurature >= 0.1f) {
                height--;
            }

        }

        height += 15;

        if(height < 0){
            throw new IllegalArgumentException("Height is "+height+" at "+genCoordX+" "+genCoordZ);
        }

        return height;
    }

    @Override
    public int colorAt(float x, float y, float z) {
        int r = 0, g = 0, b = 0;

        Biome biome = biomeAt(Math.round(x), Math.round(z));

        float genCoordX = x / 400f;
        float genCoordZ = z / 400f;
        float coloring = (float) ((colorNoise.noise(genCoordX * 2, genCoordZ * 2) - 0.5f) * 0.1);
        float coloring2 = (colorNoise.noise(genCoordZ * 2, genCoordX * 2) - 0.5f) * 0.1f;
        float coloring3 = (colorNoise.noise(-genCoordZ * 2, -genCoordX * 2) - 0.5f) * 0.1f;
        float tempurature = tempNoise.noise(genCoordX, genCoordZ);

        if(biome == Biome.PLAINS || biome == Biome.FOREST){

            if(tempurature < 0.1f){
                g = 0x80;
                b = 0x1C;
            }else{
                r = 50;
                g = 150;
                b = 80;
            }

        }else if(biome == Biome.MOUNTAINS){
            r = 187;
            g = 187;
            b = 187;
        }
        float[] hslBuffer = new float[3];//Reuse

        Color.RGBtoHSB(r, g, b, hslBuffer);

        hslBuffer[0] += coloring;
        hslBuffer[0] += coloring2;
        hslBuffer[0] += coloring3;

        return Color.HSBtoRGB(hslBuffer[0], hslBuffer[1], hslBuffer[2]);
    }

    @Override
    public void generateChunk(Chunk chunk) {
        for(int i = 0; i < Chunk.CHUNK_WIDTH;i++){
            for(int j = 0; j < Chunk.CHUNK_WIDTH;j++){

                int height = heightAt(chunk.getChunkX() * Chunk.CHUNK_WIDTH + i, chunk.getChunkZ() * Chunk.CHUNK_WIDTH + j);

                int rgb = colorAt(i + chunk.getChunkX() * Chunk.CHUNK_WIDTH, height, j + chunk.getChunkZ() * Chunk.CHUNK_WIDTH);

                height = Math.max(1, height);
                for(int y = 0; y < Chunk.CHUNK_HEIGHT;y++){
                    if(y <= height){
                        chunk.blocks[i][y][j] = rgb;
                    }
                }
            }
        }
    }
}
