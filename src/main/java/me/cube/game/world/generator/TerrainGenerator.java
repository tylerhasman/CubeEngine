package me.cube.game.world.generator;

import me.cube.game.world.ChunkSnapshot;

public interface TerrainGenerator {

    void generateChunk(int chunkX, int chunkZ, ChunkSnapshot chunk);

    /**
     * I'm not a huge fan of this because it forces the generators to always know what the height of somewhere is.
     * However its really useful to be able to get the height of something without generating an entire chunk.
     */
    int heightAt(int x, int z);

    Biome chunkBiome(int chunkX, int chunkZ);
}
