package me.cube.game.world.generator;

import me.cube.game.world.ChunkSnapshot;
import me.cube.game.world.Terrain;

public interface ChunkPopulator {

    void populateChunk(Terrain terrain, ChunkSnapshot chunk);

}
