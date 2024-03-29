package me.cube.game.world;

import me.cube.game.world.generator.Biome;

public class ChunkSnapshot {

    public int[][][] blocks = new int[Chunk.CHUNK_WIDTH][Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];

    public final int x, z;

    public Biome biome = Biome.MOUNTAINS;

    public ChunkSnapshot(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getChunkX(){
        return x;
    }

    public int getChunkZ(){
        return z;
    }
}
