package me.cube.game.world.generator;

import me.cube.engine.util.PerlinNoise;

public enum Biome {

    //seeds chosen randomly through button mashing
    NONE(0, 0, 0, 0, 0),
    ULTRA_PLAINS(0x423789, 5, 0.4f, 48, 1f),
    PLAINS(0x37429, 15, 1, 48, 1f),
    MOUNTAINS(0x921034, 40, 0.8f, 128, 1f),
    RIVER(0x213129, 0, 0, 0, 1f),
    LAKE(0x432423, -20, 1f, 16, 1f);

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

    private final PerlinNoise perlinNoise;
    private final float heightMod;//How much to stretch terrain vertically. Must be positive
    private final float spawnWeight;//Higher value increases chance this biome will spawn
    public final float blendDistance;
    public final float horizontalStretch;

    Biome(int seed, float heightMod, float spawnWeight, float blendDistance, float horizontalStretch){
        perlinNoise = new PerlinNoise(seed);
        this.heightMod = heightMod;
        this.spawnWeight = spawnWeight;
        this.blendDistance = blendDistance;
        this.horizontalStretch = horizontalStretch;
    }

    public int heightAt(int x, int z){
        float genCoordX = x / 40f;
        float genCoordZ = z / 40f;

        return (int) (perlinNoise.noise(genCoordX / horizontalStretch, genCoordZ / horizontalStretch) * heightMod);
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
