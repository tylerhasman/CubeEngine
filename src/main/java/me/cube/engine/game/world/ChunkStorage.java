package me.cube.engine.game.world;

import java.util.*;

public class ChunkStorage {

    private Map<ChunkCoordinate, Chunk> chunks;

    public ChunkStorage(){
        chunks = new HashMap<>();
    }

    public void addChunk(Chunk chunk){
        ChunkCoordinate coordinate = new ChunkCoordinate(chunk.chunkX, chunk.chunkZ);
        if(chunks.containsKey(coordinate)){
            throw new IllegalStateException("Chunk already loaded.");
        }
        chunks.put(coordinate, chunk);
    }

    public void removeChunk(int x, int z){
        chunks.remove(new ChunkCoordinate(x, z));
    }

    public Chunk getChunk(int x, int z){
        ChunkCoordinate coordinate = new ChunkCoordinate(x, z);

        return chunks.get(coordinate);
    }

    public List<Chunk> getLoadedChunks(){
        return new ArrayList<>(chunks.values());//Not fast but shouldnt be a big problem
    }

    public boolean isLoaded(int x, int z){
        return getChunk(x, z) != null;
    }

    static class ChunkCoordinate {
        public final int x, z;

        ChunkCoordinate(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public int hashCode() {
            return x + z * 31;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof ChunkCoordinate){
                return ((ChunkCoordinate) obj).x == x && ((ChunkCoordinate) obj).z == z;
            }
            return false;
        }
    }

}
