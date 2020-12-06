package me.cube.engine.game.world.generator;

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

    public int width(){
        return cubes.length;
    }

    public int height(){
        return cubes[0].length;
    }

    public int length(){
        return cubes[0][0].length;
    }

    public int getCube(int x, int y, int z){
        return cubes[x][y][z];
    }

    public int getSpawnRadius(){
        return 1;
    }

    public int getSpawnYOffset(){
        return -3;
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