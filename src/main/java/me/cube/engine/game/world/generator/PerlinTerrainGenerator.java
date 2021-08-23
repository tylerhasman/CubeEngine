package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.Chunk;
import me.cube.engine.util.CubicNoise;
import me.cube.engine.util.NoiseGenerator;
import me.cube.engine.util.PerlinNoise;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PerlinTerrainGenerator implements TerrainGenerator{

    private static final NoiseGenerator biomeNoise = new PerlinNoise(213213);//Randomly chosen
    private static final NoiseGenerator colorNoise = new CubicNoise(342121, 6);//Randomly chosen
    private static final NoiseGenerator tempNoise = new PerlinNoise(423555);//Randomly chosen

    private Map<Biome, Float> biomeWeightsAt(int x, int z){
        float genCoordX = x / 800f;
        float genCoordZ = z / 800f;

        return Biome.calculateWeights(genCoordX, genCoordZ);
    }

    public Biome biomeAt(int x, int z){

        Map<Biome, Float> weights = biomeWeightsAt(x, z);

        Biome best = null;
        float weight = 0;

        for(Biome biome : weights.keySet()){
            if(best == null){
                best = biome;
                weight = weights.get(biome);
            }else if(weights.get(biome) > weight){
                best= biome;
                weight = weights.get(biome);
            }
        }

        if(best == null){
            throw new IllegalStateException("best cannot be null!");
        }

        return best;
    }

    public int heightAt(int x, int z){

        Map<Biome, Float> weights = biomeWeightsAt(x, z);

        float height = 0;

        for(Biome biome : weights.keySet()){
            float weight = weights.get(biome);

            height += biome.heightAt(x, z) * weight;//Square the weight for better results
        }

        double sumWeights = weights.values().stream().mapToDouble(Float::doubleValue).sum();

        height /= sumWeights;

        return (int) Math.max(height, 0) + 20;
    }

    @Override
    public int colorAt(float x, float y, float z) {
        int r = 0, g = 0, b = 0;

        Biome biome = biomeAt(Math.round(x), Math.round(z));

        float genCoordX = x / 400f;
        float genCoordZ = z / 400f;
        float coloring = (float) ((colorNoise.noise(genCoordX * 100, genCoordZ * 100) - 0.5f) * 0.025f);
        float coloring2 = (colorNoise.noise(genCoordZ * 50, genCoordX * 50) - 0.5f) * 0.025f;
        float coloring3 = (colorNoise.noise(-genCoordZ * 50, -genCoordX * 50) - 0.5f) * 0.025f;
        float tempurature = tempNoise.noise(genCoordX, genCoordZ);

        if(tempurature < 0.1f){
            g = 0x80;
            b = 0x1C;
        }else{
            r = 50;
            g = 150;
            b = 80;
        }

        if(y < heightAt(Math.round(x), Math.round(z))){
            //131,101,57
            r = 131;
            g = 101;
            b = 57;
        }

        if(biome == Biome.RIVER){
            r = 0;
            g = 0;
            b = 255;
            coloring = 0;
            coloring2 = 0;
            coloring3 = 0;
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
