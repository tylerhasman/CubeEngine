package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.BiomeMap;
import me.cube.engine.util.PerlinNoise;

public enum Biome {

    //seeds chosen randomly through button mashing
    ULTRA_PLAINS(0x423789, 5, 0.4f, 64),
    PLAINS(0x37429, 15, 1, 64),
    MOUNTAINS(0x921034, 40, 0.8f, 64),
    RIVER(0x213129, 0, 0, 0),
    LAKE(0x432423, -10, 0.25f, -32);

    public static final Biome[] GENERATED = new Biome[] {
            Biome.ULTRA_PLAINS,
            Biome.PLAINS,
            Biome.MOUNTAINS,
            Biome.LAKE
    };

    private static float totalWeight = 0;

    static{
        for(Biome b : GENERATED){
            totalWeight += b.spawnWeight;
        }
    }

    private static final BiomeMap biomeMap = new BiomeMap(0x783912);

    private final PerlinNoise perlinNoise;
    private final float heightMod;//How much to stretch terrain vertically. Must be positive
    private final float spawnWeight;//Higher value increases chance this biome will spawn
    public final float blendDistance;

    Biome(int seed, float heightMod, float spawnWeight, float blendDistance){
        perlinNoise = new PerlinNoise(seed);
        this.heightMod = heightMod;
        this.spawnWeight = spawnWeight;
        this.blendDistance = blendDistance;
    }

    public float getSpawnWeight() {
        return spawnWeight;
    }

    public int heightAt(int x, int z){
        float genCoordX = x / 40f;
        float genCoordZ = z / 40f;

        return (int) (perlinNoise.noise(genCoordX, genCoordZ) * heightMod);
    }

    /**
     * Selects a biome based off of their weights
     * @param weight number from 0.0 to 1.0
     */
    public static Biome fromWeight(float weight){
        if(weight < 0 || weight > 1)
            throw new IllegalArgumentException("weight must be between 0.0 and 1.0. Got "+weight);
        weight *= totalWeight;

        float accumWeight = 0;

        for(Biome biome : GENERATED){
            if(accumWeight <= weight && weight <= biome.spawnWeight + accumWeight){
                return biome;
            }
            accumWeight += biome.spawnWeight;
        }

        //Should never happen
        throw new IllegalStateException("How did we get here... "+weight);
    }

}
