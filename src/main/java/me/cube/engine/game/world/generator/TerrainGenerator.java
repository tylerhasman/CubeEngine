package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.Chunk;

public interface TerrainGenerator {

    void generateChunk(int chunkX, int chunkZ, int[][][] blocks);

    //TODO: Remove all  these methods below this
    //They do not need to be public and should not be either.

/*    Biome biomeAt(int x, int z);

    int colorAt(float x, float y, float z);

    int heightAt(int x, int z);*/

}
