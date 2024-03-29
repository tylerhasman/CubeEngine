package me.cube.game.world.generator;

import me.cube.game.world.BiomeMap;
import me.cube.game.world.Chunk;
import me.cube.game.world.ChunkSnapshot;
import me.cube.engine.util.CubicNoise;
import me.cube.engine.util.NoiseGenerator;
import me.cube.engine.util.PerlinNoise;
import org.joml.Vector3f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static me.cube.game.world.generator.Biome.LAKE;
import static me.cube.game.world.generator.Biome.RIVER;

public class PerlinTerrainGenerator implements TerrainGenerator{

    private static final NoiseGenerator colorNoise = new CubicNoise(342121, 6);//Randomly chosen
    private static final NoiseGenerator tempNoise = new PerlinNoise(423555);//Randomly chosen

    private final int[][] heightCache = new int[Chunk.CHUNK_WIDTH][Chunk.CHUNK_WIDTH];
    private int cacheChunkX, cacheChunkZ;

    //Speeds up calculating colors in the same vertical strip.
    //There are better ways of getting these from the generate method to the colorAt method but this works well for now.
    //TODO
    private float coloring, coloring2, coloring3, tempurature;

    private final BiomeMap biomeMap = new BiomeMap();

    private Map<Biome, Float> biomeWeightsAt(int x, int z){
        return biomeMap.calculateBiomeWeights(x, z);
    }

    private int waterHeightAt(Map<Biome, Float> weights){
        if(weights.getOrDefault(LAKE, 0f) > 0f){
            return 20;
        }
        if(weights.getOrDefault(RIVER, 0f) > 0f){
            return 30;
        }
        return 10;
    }

    private boolean isCacheValidFor(int worldX, int worldZ){
        worldX -= cacheChunkX * Chunk.CHUNK_WIDTH;
        worldZ -= cacheChunkZ * Chunk.CHUNK_WIDTH;

        return worldX >= 0 && worldZ >= 0 && worldX < Chunk.CHUNK_WIDTH && worldZ < Chunk.CHUNK_WIDTH;
    }

    public int heightAt(int x, int z){

        int cachedHeight = heightCache[Math.abs(x % Chunk.CHUNK_WIDTH)][Math.abs(z % Chunk.CHUNK_WIDTH)];

        if(cachedHeight != 0 && isCacheValidFor(x, z)){
            return cachedHeight;
        }

        Map<Biome, Float> weights = biomeWeightsAt(x, z);

        float height = 0;

        for(Biome biome : weights.keySet()){
            float weight = weights.get(biome);

            height += biome.heightAt(x, z) * weight;//Square the weight for better results
        }

        double sumWeights = weights.values().stream().mapToDouble(Float::doubleValue).sum();

        height /= sumWeights;

        int groundHeight = 20;

        int finalHeight = (int) Math.max(height + groundHeight, 1);

        heightCache[Math.abs(x % Chunk.CHUNK_WIDTH)][Math.abs(z % Chunk.CHUNK_WIDTH)] = finalHeight;

        return finalHeight;
    }

    private Vector3f colorAtBiome(Biome biome, float x, float y, float z){
        int groundHeight = heightAt((int) Math.floor(x), (int) Math.floor(z));

        if(y > groundHeight){
            return null;
        }

        int r = 0, g = 0, b = 0, a = 0xFF;

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

        if(biome == LAKE && y < 19){
            r = 194;
            g = 178;
            b = 128;
        }

        if(biome == Biome.MOUNTAINS){
            r = 151;
            g = 124;
            b = 83;
        }

        return new Vector3f(r, g, b);
    }

    private int waterColorAt(Map<Biome, Float> weights, int i, int y, int i1){
        return 0x3C0000FF;
    }

    private int colorAt(Map<Biome, Float> biomeWeights, float x, float y, float z) {

        Vector3f colorSum = new Vector3f();

        float totalWeight = 0;

        for(Biome biome : biomeWeights.keySet()){
            float weight = biomeWeights.get(biome);

            if (weight > 0){
                Vector3f color = colorAtBiome(biome, x, y, z);

                if(color != null){
                    colorSum.add(color.mul(weight));
                    totalWeight += weight;
                }
            }

        }

        colorSum.mul(1f / totalWeight);

        int r = (int) colorSum.x;
        int g = (int) colorSum.y;
        int b = (int) colorSum.z;

        float[] hslBuffer = new float[3];//Reuse

        Color.RGBtoHSB(r, g, b, hslBuffer);

        hslBuffer[0] += coloring;
        hslBuffer[0] += coloring2;
        hslBuffer[0] += coloring3;

        int outColor = Color.HSBtoRGB(hslBuffer[0], hslBuffer[1], hslBuffer[2]);

        return (0xFF << 24) | (outColor & 0x00FFFFFF);
    }

    private void clearCaches(){
        for(int i = 0; i < Chunk.CHUNK_WIDTH;i++){
            for(int j = 0; j < Chunk.CHUNK_WIDTH;j++){
                heightCache[i][j] = 0;
            }
        }
    }

    private Biome strongestBiome(Map<Biome, Float> biomes){

        if(biomes.size() == 0){
            throw new IllegalArgumentException("biomes size == 0");
        }

        Biome best = null;
        float f = 0;

        for(Biome b : biomes.keySet()){
            if(best == null){
                best = b;
                f = biomes.get(b);
            }else if(biomes.get(b) > f){
                best = b;
                f = biomes.get(b);
            }
        }

        return best;
    }

    @Override
    public Biome chunkBiome(int chunkX, int chunkZ) {
        Map<Biome, Float> weights = biomeWeightsAt(chunkX * Chunk.CHUNK_WIDTH, chunkZ * Chunk.CHUNK_WIDTH);
        return strongestBiome(weights);
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ, ChunkSnapshot chunk) {

        clearCaches();

        cacheChunkX = chunkX;
        cacheChunkZ = chunkZ;

        Map<Biome, Float> totals = new HashMap<>();

        for(int i = 0; i < Chunk.CHUNK_WIDTH;i++){
            for(int j = 0; j < Chunk.CHUNK_WIDTH;j++){

                int height = heightAt(chunkX * Chunk.CHUNK_WIDTH + i, chunkZ * Chunk.CHUNK_WIDTH + j);

                float genCoordX = (chunkX * Chunk.CHUNK_WIDTH + i) / 400f;
                float genCoordZ = (chunkZ * Chunk.CHUNK_WIDTH + j) / 400f;

                coloring = (colorNoise.noise(genCoordX * 100, genCoordZ * 100) - 0.5f) * 0.025f;
                coloring2 = (colorNoise.noise(genCoordZ * 50, genCoordX * 50) - 0.5f) * 0.025f;
                coloring3 = (colorNoise.noise(-genCoordZ * 50, -genCoordX * 50) - 0.5f) * 0.025f;
                tempurature = tempNoise.noise(genCoordX, genCoordZ);

                Map<Biome, Float> weights = biomeWeightsAt(i + chunkX * Chunk.CHUNK_WIDTH, j + chunkZ * Chunk.CHUNK_WIDTH);

                for(Biome b : weights.keySet()){
                    totals.put(b, totals.getOrDefault(b, 0f) + weights.get(b));
                }

                height = Math.max(1, height);
                for(int y = 0; y <= height;y++){
                    int argb = colorAt(weights,i + chunkX * Chunk.CHUNK_WIDTH, y, j + chunkZ * Chunk.CHUNK_WIDTH);

                    chunk.blocks[i][y][j] = argb;
                }

                int waterHeight = waterHeightAt(weights);

                for(int y = height+1; y < waterHeight;y++){
                    int argb = waterColorAt(weights,i + chunkX * Chunk.CHUNK_WIDTH, y, j + chunkZ * Chunk.CHUNK_WIDTH);

                    chunk.blocks[i][y][j] = argb;
                }

            }
        }

        chunk.biome = strongestBiome(totals);
    }
}
