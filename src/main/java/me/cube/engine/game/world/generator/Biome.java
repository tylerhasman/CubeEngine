package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.Chunk;
import me.cube.engine.util.PerlinNoise;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Biome {

    //seeds chosen randomly through button mashing
    PLAINS(0x423789, 50, 10),
    MOUNTAINS(0x921034, 50, 120),
    FOREST(0x3216821, 50, 20);

    private final PerlinNoise perlinNoise;
    private final float stretch;//How much to stretch positions on the noise generator. Must be positive
    private final float heightMod;//How much to stretch terrain vertically. Must be positive

    Biome(int seed, float stretch, float heightMod){
        if(stretch < 0){
            throw new IllegalArgumentException("stretch must be >0");
        }
        if(heightMod < 0){
            throw new IllegalArgumentException("heightMod must be >0");
        }
        perlinNoise = new PerlinNoise(seed);
        this.stretch = stretch;
        this.heightMod = heightMod;
    }

    public float biomeThreshold(){
        return ((float) ordinal() / (float) values().length);
    }

    public int heightAt(int x, int z){
        float genCoordX = x / stretch;
        float genCoordZ = z / stretch;

        return (int) (perlinNoise.noise(genCoordX, genCoordZ) * heightMod);
    }

    /**
     * Calculates the weights for all biomes
     */
    public static Map<Biome, Float> calculateWeights(float x, float y){
        Map<Biome, Float> biomes = new HashMap<>();

        for(Biome biome : values()){
            biomes.put(biome, biome.perlinNoise.noise(x, y, biome.ordinal()));
            //biomes.put(biome, 1f - Math.abs(biome.biomeThreshold() - noise));
        }

        return biomes;
    }

}
