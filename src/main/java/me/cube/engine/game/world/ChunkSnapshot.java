package me.cube.engine.game.world;

public class ChunkSnapshot {

    public int[][][] blocks = new int[Chunk.CHUNK_WIDTH][Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];

    public final int x, z;

    public ChunkSnapshot(int x, int z) {
        this.x = x;
        this.z = z;
    }
}
