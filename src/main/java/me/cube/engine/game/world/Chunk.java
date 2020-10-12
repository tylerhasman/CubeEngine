package me.cube.engine.game.world;

import me.cube.engine.Voxel;
import me.cube.engine.model.AsyncChunkMesh;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static me.cube.engine.game.world.World.WORLD_SCALE;

public class Chunk {

    private static final ScheduledExecutorService meshGeneratorExec = new ScheduledThreadPoolExecutor(2, (r) -> {

        Thread thread = new Thread(r, "ChunkMeshGenerator");

        thread.setDaemon(true);

        return thread;
    });

    public static final int CHUNK_WIDTH = 32;
    public static final int CHUNK_HEIGHT = 128;

    public final int[][][] blocks;
    protected final int chunkX, chunkZ;

    protected boolean requireMeshRefresh = false;

    private Voxel mesh;

    private final Terrain terrain;

    private Future<AsyncChunkMesh> meshGeneratedFuture;

    protected Chunk(Terrain terrain, int x, int z){
        this.terrain = terrain;
        blocks = new int[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
        this.chunkX = x;
        this.chunkZ = z;
        mesh = null;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public void render(){
        if(requireMeshRefresh){
            requireMeshRefresh = false;
            generateMesh();
        }

        if(meshGeneratedFuture != null){
            if(meshGeneratedFuture.isDone()){
                try {

                    if(mesh != null && mesh.model != null){
                        mesh.model.dispose();
                    }

                    AsyncChunkMesh chunkMesh = meshGeneratedFuture.get();
                    chunkMesh.initialize();

                    mesh = new Voxel("Chunk "+chunkX+" "+chunkZ, chunkMesh);
                    mesh.scale.set(World.WORLD_SCALE);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }finally{
                    meshGeneratedFuture = null;
                }
            }
        }

        if(mesh != null){
            mesh.position.set(chunkX * CHUNK_WIDTH * WORLD_SCALE, 0, chunkZ * CHUNK_WIDTH * WORLD_SCALE);
            mesh.origin.set(0, 0, 0);
            mesh.render();
        }
    }

    protected void generateMesh(){

        if(meshGeneratedFuture != null){
            meshGeneratedFuture.cancel(true);
            meshGeneratedFuture = null;
        }

/*        if(mesh != null && mesh.model != null){
            mesh.model.dispose();
            mesh = null;
        }*/

        meshGeneratedFuture = meshGeneratorExec.submit(() -> new AsyncChunkMesh(terrain, this));
    }

    public boolean isWithinChunk(int worldX, int worldZ){
        worldX -= chunkX * Chunk.CHUNK_WIDTH;
        worldZ -= chunkZ * Chunk.CHUNK_WIDTH;

        return worldX >= 0 && worldZ >= 0 && worldX < blocks.length && worldZ < blocks[0][0].length;
    }

    protected void setBlock(int x, int y, int z, int color){
        if(x < 0 || y < 0 || z < 0 || x >= blocks.length || y >= blocks[0].length || z >= blocks[0][0].length){
            return;
        }
        blocks[x][y][z] = color;
    }

    protected void setBlockWorldCoords(int x, int y, int z, int color){
        //TODO: Implement
    }

    protected boolean isSolid(int x, int y, int z){
        if(x < 0 || y < 0 || z < 0 || x >= blocks.length || y >= blocks[0].length || z >= blocks[0][0].length){
            return false;
        }
        return blocks[x][y][z] != 0;
    }

}
