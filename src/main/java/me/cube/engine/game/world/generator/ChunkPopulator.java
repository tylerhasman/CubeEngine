package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.Chunk;
import me.cube.engine.game.world.Terrain;

public interface ChunkPopulator {

    void populateChunk(Terrain terrain, Chunk chunk);

}
