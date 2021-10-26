package me.cube.game.world.generator;

public class SpawnableStructure {

    private int[][][] cubes;
    private Biome[] spawnableIn;
    private int spawnRarity;
    private int maxPerChunk;
    public final int randomSeed;

    public SpawnableStructure(int[][][] cubes, Biome[] spawnableIn, int spawnRarity, int maxPerChunk, int randomSeed) {
        this.cubes = cubes;
        this.spawnableIn = spawnableIn;
        this.spawnRarity = spawnRarity;
        this.maxPerChunk = maxPerChunk;
        this.randomSeed = randomSeed;
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
        return 0;
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
