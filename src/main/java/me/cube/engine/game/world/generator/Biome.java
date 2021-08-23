package me.cube.engine.game.world.generator;

import me.cube.engine.util.PerlinNoise;

import java.util.HashMap;
import java.util.Map;

public enum Biome {

    //seeds chosen randomly through button mashing
    ULTRA_PLAINS(0x423789, -40, 1f),
    PLAINS(0x37429, -20, 1f),
    MOUNTAINS(0x921034, 80, 1f),
    RIVER(0x213129, -40, 1f);

    private static final Biome[] GENERATED = new Biome[] {
      Biome.ULTRA_PLAINS,
      Biome.PLAINS,
      Biome.MOUNTAINS
    };

    private final PerlinNoise perlinNoise, weightNoise;
    private final float heightMod;//How much to stretch terrain vertically. Must be positive
    private final float weightMod;

    Biome(int seed, float heightMod, float weightMod){
        perlinNoise = new PerlinNoise(seed);
        weightNoise = new PerlinNoise(seed >> 8);
        this.heightMod = heightMod;
        this.weightMod = weightMod;
    }

    public int heightAt(int x, int z){
        float genCoordX = x / 40f;
        float genCoordZ = z / 40f;

        return (int) (perlinNoise.noise(genCoordX, genCoordZ) * heightMod);
    }

    /**
     * Calculates the weights for all biomes
     */
    public static Map<Biome, Float> calculateWeights(float x, float y){
        Map<Biome, Float> biomes = new HashMap<>();

        for(Biome biome : GENERATED){
            biomes.put(biome, biome.weightNoise.noise(x, y) * biome.weightMod);
        }

        if(biomes.size() > 1){
            Map<Biome, Float> copy = new HashMap<>(biomes);

            Biome top = calculateBiome(copy);
            copy.remove(top);
            Biome top2 = calculateBiome(copy);

            float topWeight = biomes.get(top);
            float top2Weight = biomes.get(top2);

            if(Math.abs(topWeight - top2Weight) < 0.01f){
                biomes.put(Biome.RIVER, 1f);
            }

        }

        return biomes;
    }

    private static Biome calculateBiome(Map<Biome, Float> weights){
        if (weights.size() == 0) {
            throw new IllegalArgumentException("weights cannot be empty");
        }
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

        return best;
    }

}
