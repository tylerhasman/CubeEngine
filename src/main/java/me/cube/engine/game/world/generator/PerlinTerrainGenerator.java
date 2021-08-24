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

    private static final NoiseGenerator colorNoise = new CubicNoise(342121, 6);//Randomly chosen
    private static final NoiseGenerator tempNoise = new PerlinNoise(423555);//Randomly chosen

    private Biome[][] biomeCache = new Biome[Chunk.CHUNK_WIDTH][Chunk.CHUNK_WIDTH];
    private int[][] heightCache = new int[Chunk.CHUNK_WIDTH][Chunk.CHUNK_WIDTH];

    //Speeds up calculating colors in the same vertical strip.
    //There are better ways of getting these from the generate method to the colorAt method but this works well for now.
    //TODO
    private float coloring, coloring2, coloring3, tempurature;

    private Map<Biome, Float> biomeWeightsAt(int x, int z){
        return Biome.calculateWeights(x, z);
    }

    public Biome biomeAt(int x, int z){

        if(biomeCache[Math.abs(x % Chunk.CHUNK_WIDTH)][Math.abs(z % Chunk.CHUNK_WIDTH)] != null){
            return biomeCache[Math.abs(x % Chunk.CHUNK_WIDTH)][Math.abs(z % Chunk.CHUNK_WIDTH)];
        }

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

        return biomeCache[Math.abs(x % Chunk.CHUNK_WIDTH)][Math.abs(z % Chunk.CHUNK_WIDTH)] = best;
    }

    public int heightAt(int x, int z){

        if(heightCache[Math.abs(x % Chunk.CHUNK_WIDTH)][Math.abs(z % Chunk.CHUNK_WIDTH)] != 0){
            return heightCache[Math.abs(x % Chunk.CHUNK_WIDTH)][Math.abs(z % Chunk.CHUNK_WIDTH)];
        }

        Map<Biome, Float> weights = biomeWeightsAt(x, z);

        float height = 0;

        for(Biome biome : weights.keySet()){
            float weight = weights.get(biome);

            height += biome.heightAt(x, z) * weight;//Square the weight for better results
        }

        double sumWeights = weights.values().stream().mapToDouble(Float::doubleValue).sum();

        height /= sumWeights;

        if(weights.getOrDefault(Biome.RIVER, 0f) == 1){
            return heightCache[Math.abs(x % Chunk.CHUNK_WIDTH)][Math.abs(z % Chunk.CHUNK_WIDTH)] = (int) Math.max((height + 20) / 1.35f, 1);
        }

        return heightCache[Math.abs(x % Chunk.CHUNK_WIDTH)][Math.abs(z % Chunk.CHUNK_WIDTH)] = (int) Math.max(height + 20, 1);
    }

    @Override
    public int colorAt(float x, float y, float z) {
        int groundHeight = heightAt((int) Math.floor(x), (int) Math.floor(z));

        if(y > groundHeight){
            return 0;
        }
        int r = 0, g = 0, b = 0, a = 0xFF;

        Biome biome = biomeAt(Math.round(x), Math.round(z));

        if(tempurature < 0.1f){
            g = 0x80;
            b = 0x1C;
        }else{
            r = 50;
            g = 150;
            b = 80;
        }

        if(y < groundHeight){
            r = 131;
            g = 101;
            b = 57;
        }

        if(biome == Biome.RIVER){

            if(y < groundHeight - 8){
                r = 131;
                g = 101;
                b = 57;
            }else{
                r = 100;
                g = 100;
                b = 253;
                a = 64;
                coloring = 0;
                coloring2 = 0;
                coloring3 = 0;
            }

        }else if(biome == Biome.MOUNTAINS){
            r = 151;
            g = 124;
            b = 83;
        }

        float[] hslBuffer = new float[3];//Reuse

        Color.RGBtoHSB(r, g, b, hslBuffer);

        hslBuffer[0] += coloring;
        hslBuffer[0] += coloring2;
        hslBuffer[0] += coloring3;

        int outColor = Color.HSBtoRGB(hslBuffer[0], hslBuffer[1], hslBuffer[2]);

        return (a << 24) | (outColor & 0x00FFFFFF);
    }

    private void clearCaches(){
        for(int i = 0; i < Chunk.CHUNK_WIDTH;i++){
            for(int j = 0; j < Chunk.CHUNK_WIDTH;j++){
                biomeCache[i][j] = null;
                heightCache[i][j] = 0;
            }
        }
    }

    @Override
    public void generateChunk(Chunk chunk) {

        clearCaches();

        for(int i = 0; i < Chunk.CHUNK_WIDTH;i++){
            for(int j = 0; j < Chunk.CHUNK_WIDTH;j++){

                int height = heightAt(chunk.getChunkX() * Chunk.CHUNK_WIDTH + i, chunk.getChunkZ() * Chunk.CHUNK_WIDTH + j);

                float genCoordX = (chunk.getChunkX() * Chunk.CHUNK_WIDTH + i) / 400f;
                float genCoordZ = (chunk.getChunkZ() * Chunk.CHUNK_WIDTH + j) / 400f;

                coloring = (colorNoise.noise(genCoordX * 100, genCoordZ * 100) - 0.5f) * 0.025f;
                coloring2 = (colorNoise.noise(genCoordZ * 50, genCoordX * 50) - 0.5f) * 0.025f;
                coloring3 = (colorNoise.noise(-genCoordZ * 50, -genCoordX * 50) - 0.5f) * 0.025f;
                tempurature = tempNoise.noise(genCoordX, genCoordZ);


                height = Math.max(1, height);
                for(int y = 0; y < Chunk.CHUNK_HEIGHT;y++){
                    if(y <= height){
                        int argb = colorAt(i + chunk.getChunkX() * Chunk.CHUNK_WIDTH, y, j + chunk.getChunkZ() * Chunk.CHUNK_WIDTH);

                        chunk.blocks[i][y][j] = argb;
                    }
                }
            }
        }
    }
}
