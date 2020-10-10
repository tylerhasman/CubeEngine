package me.cube.engine.game.world.generator;

import me.cube.engine.VoxelModel;
import me.cube.engine.file.VxmFile;
import me.cube.engine.game.world.generator.Biome;

public class SpawnableStructure {

    private int[][][] cubes;
    private Biome[] spawnableIn;
    private int spawnRarity;
    private int maxPerChunk;

    public SpawnableStructure(int[][][] cubes, Biome[] spawnableIn, int spawnRarity, int maxPerChunk) {
        this.cubes = cubes;
        this.spawnableIn = spawnableIn;
        this.spawnRarity = spawnRarity;
        this.maxPerChunk = maxPerChunk;
    }

    public int getCube(int x, int y, int z){
        return cubes[x][y][z];
    }

    public boolean isSpawnableIn(Biome biome){
        for(Biome b : spawnableIn){
            if(b == biome)
                return true;
        }
        return false;
    }

    public int getSpawnRarity() {
        return spawnRarity;
    }

    public int getMaxPerChunk() {
        return maxPerChunk;
    }
}
