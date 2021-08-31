package me.cube.engine.game.world.generator;

public interface TerrainGenerator {

    void generateChunk(int chunkX, int chunkZ, int[][][] blocks);

    /**
     * I'm not a huge fan of this because it forces the generators to always know what the height of somewhere is.
     * However its really useful to be able to get the height of something without generating an entire chunk.
     */
    int heightAt(int x, int z);

}
