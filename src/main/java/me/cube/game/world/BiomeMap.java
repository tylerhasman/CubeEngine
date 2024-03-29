package me.cube.game.world;

import me.cube.game.world.generator.Biome;
import me.cube.engine.util.MathUtil;
import org.joml.Vector2f;

import java.util.*;

public class BiomeMap {

    public static final int BIOME_CELL_SIZE = 8;
    //private static final float BLEND_DISTANCE = 64;
    private static final float RIVER_THRESHOLD = 8;

    public BiomeMap(){

    }

    private Random getRandom(int x, int z){
        long seed = MathUtil.hash(x * 31, z * 13);

        return new Random(seed);
    }

    public Vector2f findBiomeCenter(int worldX, int worldZ){
        int chunkX = worldX / Chunk.CHUNK_WIDTH;
        int chunkZ = worldZ / Chunk.CHUNK_WIDTH;

        int cellX = Math.floorDiv(chunkX, BIOME_CELL_SIZE);
        int cellZ = Math.floorDiv(chunkZ, BIOME_CELL_SIZE);

        return biomeCenter(cellX, cellZ, new Vector2f());
    }

    private Vector2f biomeCenter(int cellX, int cellZ, Vector2f out){
        Random random = getRandom(cellX, cellZ);

        float centerX = random.nextFloat() * BIOME_CELL_SIZE + cellX * BIOME_CELL_SIZE;
        float centerZ = random.nextFloat() * BIOME_CELL_SIZE + cellZ * BIOME_CELL_SIZE;

        out.set(centerX * Chunk.CHUNK_WIDTH, centerZ * Chunk.CHUNK_WIDTH);

        return out;
    }

    //Returns biome of a cell
    private Biome biomeAt(int x, int z) {
        Random random = getRandom(x / 2, z / 2);

        return Biome.fromWeight(random.nextFloat());
    }

    public Map<Biome, Float> calculateBiomeWeights(int worldX, int worldZ){
        Map<Float, Biome> distances = calculateBiomeDistances(worldX, worldZ);

        Map<Biome, Float> weights = new HashMap<>();

        List<Float> keys = new ArrayList<>(distances.keySet());
        keys.sort(Float::compare);

        //Shading with 'definite' biomes
        float top = keys.get(0);
        Biome closest = distances.get(top);
        weights.put(closest, 1f);

        for(int i = 1; i < keys.size();i++){
            float top2 = keys.get(i);
            Biome closest2 = distances.get(top2);

            float outValue = weights.getOrDefault(closest2, 0f);

            float blendDistance = 128;//(closest.blendDistance + closest2.blendDistance) / 2f;

            //blendDistance = (float) Math.min(blendDistance, Math.sqrt(Chunk.CHUNK_WIDTH * BIOME_CELL_SIZE));

            if(Math.abs(top - top2) < blendDistance){
                float a = Math.abs(top - top2);
                outValue += 1f - Math.sqrt(a / blendDistance);
            }

            if(outValue > 0)
                weights.put(closest2, outValue);
        }

        return weights;
    }

    private Map<Float, Biome> calculateBiomeDistances(int worldX, int worldZ){
        int chunkX = Math.floorDiv(worldX, Chunk.CHUNK_WIDTH);
        int chunkZ = Math.floorDiv(worldZ, Chunk.CHUNK_WIDTH);

        int cellX = Math.floorDiv(chunkX, BIOME_CELL_SIZE);
        int cellZ = Math.floorDiv(chunkZ, BIOME_CELL_SIZE);

        Map<Float, Biome> biomes = new HashMap<>();

        Vector2f out = new Vector2f();

        for(int i = -1; i <= 1;i++){
            for(int j = -1; j <= 1;j++){
                Biome biome = biomeAt(cellX + i, cellZ + j);
                biomeCenter(cellX + i, cellZ + j, out);

                //Manhattan Distance
                //float distance = Math.abs(worldX - center.x) + Math.abs(worldZ - center.y);

                float distance = out.distance(worldX, worldZ);

                biomes.put(distance, biome);
            }
        }

        return biomes;
    }

}
