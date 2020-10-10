package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.Chunk;

public interface TerrainGenerator {

    void generateChunk(Chunk chunk);

    Biome biomeAt(int x, int z);

}
