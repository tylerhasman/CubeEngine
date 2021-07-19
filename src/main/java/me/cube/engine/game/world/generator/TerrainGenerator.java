package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.Chunk;

public interface TerrainGenerator {

    void generateChunk(Chunk chunk);

    Biome biomeAt(int x, int z);

    int colorAt(float x, float y, float z);

    int heightAt(int x, int z);

}
