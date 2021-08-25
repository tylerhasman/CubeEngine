package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.BiomeMap;
import me.cube.engine.util.PerlinNoise;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Biome {

    //seeds chosen randomly through button mashing
    ULTRA_PLAINS(0x423789, 5),
    PLAINS(0x37429, 15),
    MOUNTAINS(0x921034, 65),
    RIVER(0x213129, 0);

    public static final Biome[] GENERATED = new Biome[] {
      Biome.ULTRA_PLAINS,
      Biome.PLAINS,
      Biome.MOUNTAINS
    };

    private static final BiomeMap biomeMap = new BiomeMap(0x783912);

    private final PerlinNoise perlinNoise;
    private final float heightMod;//How much to stretch terrain vertically. Must be positive

    Biome(int seed, float heightMod){
        perlinNoise = new PerlinNoise(seed);
        this.heightMod = heightMod;
    }

    public int heightAt(int x, int z){
        float genCoordX = x / 40f;
        float genCoordZ = z / 40f;

        return (int) (perlinNoise.noise(genCoordX, genCoordZ) * heightMod);
    }

}
