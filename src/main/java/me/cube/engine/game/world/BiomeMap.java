package me.cube.engine.game.world;

import me.cube.engine.game.world.generator.Biome;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

public class BiomeMap {

    public static final int BIOME_CELL_SIZE = 16;
    private static final float BLEND_DISTANCE = 32;

    private final long seed;

    public BiomeMap(long seed){
        this.seed = seed;
    }

    private Random getRandom(int x, int z){
        long biomeSeed = x;

        biomeSeed = seed + ((biomeSeed << 32) | z);

        Random random = new Random(biomeSeed);
        random.setSeed(random.nextLong());

        return random;
    }

    public Vector2f findBiomeCenter(int worldX, int worldZ){
        int chunkX = worldX / Chunk.CHUNK_WIDTH;
        int chunkZ = worldZ / Chunk.CHUNK_WIDTH;

        int cellX = Math.floorDiv(chunkX, BIOME_CELL_SIZE);
        int cellZ = Math.floorDiv(chunkZ, BIOME_CELL_SIZE);

        return biomeCenter(cellX, cellZ);
    }

    private Vector2f biomeCenter(int cellX, int cellZ){
        Random random = getRandom(cellX, cellZ);

        float centerX = random.nextFloat() * BIOME_CELL_SIZE + cellX * BIOME_CELL_SIZE;
        float centerZ = random.nextFloat() * BIOME_CELL_SIZE + cellZ * BIOME_CELL_SIZE;

        return new Vector2f(centerX * Chunk.CHUNK_WIDTH, centerZ * Chunk.CHUNK_WIDTH);
    }

    //Returns biome of a cell
    private Biome biomeAt(int x, int z){
        Random random = getRandom(x, z);

        return Biome.GENERATED[random.nextInt(Biome.GENERATED.length)];
    }

    public Map<Biome, Float> calculateBiomeWeights(int worldX, int worldZ){
        Map<Float, Biome> distances = calculateBiomeDistances(worldX, worldZ);

        Map<Biome, Float> weights = new HashMap<>();

        List<Float> keys = new ArrayList<>(distances.keySet());
        keys.sort(Float::compare);

        float top = keys.get(0);
        Biome closest = distances.get(top);
        weights.put(closest, 1f);

        for(int i = 1; i < keys.size();i++){
            float top2 = keys.get(i);
            Biome closest2 = distances.get(top2);

            float outValue = weights.getOrDefault(closest2, 0f);

            if(Math.abs(top - top2) < BLEND_DISTANCE){
                outValue += 1f - Math.abs(top - top2) / BLEND_DISTANCE;
            }

            weights.put(closest2, outValue);
        }

        return weights;
    }

    public Map<Float, Biome> calculateBiomeDistances(int worldX, int worldZ){
        int chunkX = worldX / Chunk.CHUNK_WIDTH;
        int chunkZ = worldZ / Chunk.CHUNK_WIDTH;

        int cellX = Math.floorDiv(chunkX, BIOME_CELL_SIZE);
        int cellZ = Math.floorDiv(chunkZ, BIOME_CELL_SIZE);

        Map<Float, Biome> biomes = new HashMap<>();

        for(int i = -1; i <= 1;i++){
            for(int j = -1; j <= 1;j++){
                Biome biome = biomeAt(cellX + i, cellZ + j);
                Vector2f center = biomeCenter(cellX + i, cellZ + j);

                //Manhattan Distance
                //float distance = Math.abs(worldX - center.x) + Math.abs(worldZ - center.y);

                float distance = center.distance(worldX, worldZ);

                biomes.put(distance, biome);
            }
        }

        return biomes;
    }

}