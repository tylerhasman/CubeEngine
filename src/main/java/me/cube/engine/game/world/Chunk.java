package me.cube.engine.game.world;

import me.cube.engine.Voxel;
import me.cube.engine.file.ChunkSave;
import me.cube.engine.model.AsyncChunkMesh;

import java.io.IOException;
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

    private boolean disposed;

    private ChunkSave chunkSave;

    protected Chunk(Terrain terrain, int x, int z, ChunkSave chunkSave){
        this.terrain = terrain;
        blocks = new int[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
        this.chunkX = x;
        this.chunkZ = z;
        mesh = null;
        disposed = false;
        this.chunkSave = chunkSave;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public int dst2(int x, int z){
        return (chunkX - x) * (chunkX - x) + (chunkZ - z) * (chunkZ - z);
    }

    public void dispose(){
        if(!disposed){
            if(meshGeneratedFuture != null){
                meshGeneratedFuture.cancel(true);
                meshGeneratedFuture = null;
            }
            if(mesh != null && mesh.model != null){
                mesh.model.dispose();
            }
            disposed = true;
            try {
                chunkSave.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void render(){
        if(disposed){
            return;
        }
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
                    mesh.getTransform().scale(World.WORLD_SCALE);
                    mesh.getTransform().translate(chunkX * CHUNK_WIDTH, 0, chunkZ * CHUNK_WIDTH);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }finally{
                    meshGeneratedFuture = null;
                }
            }
        }

        if(mesh != null){

            mesh.render();
        }
    }

    protected void generateMesh(){

        if(disposed){
            return;
        }

        if(meshGeneratedFuture != null){
            meshGeneratedFuture.cancel(true);
            meshGeneratedFuture = null;
        }

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

        if(blocks[x][y][z] != color){
            chunkSave.modify(x, y, z, color);
        }

        blocks[x][y][z] = color;
        requireMeshRefresh = true;

    }

    public int getBlock(int x, int y, int z){
        if(x < 0 || y < 0 || z < 0 || x >= blocks.length || y >= blocks[0].length || z >= blocks[0][0].length){
            return 0;
        }

        return blocks[x][y][z];
    }

    /**
     * Calculates which chunk a world coordinate lies in
     */
    public static int worldToChunk(int worldCoord){
        return (int) Math.floor((float) worldCoord / (float) CHUNK_WIDTH);
    }

/*    protected void setBlockWorldCoords(int x, int y, int z, int color){
        x -= chunkX * Chunk.CHUNK_WIDTH;
        z -= chunkZ * Chunk.CHUNK_WIDTH;

        if(x < 0 || y < 0 || z < 0 || x >= blocks.length || y >= blocks[0].length || z >= blocks[0][0].length){
            return;
        }

        setBlock(x, y, z, color);
    }

    public int getBlockWorldCoords(int x, int y, int z) {
        x -= chunkX * Chunk.CHUNK_WIDTH;
        z -= chunkZ * Chunk.CHUNK_WIDTH;

        if(x < 0 || y < 0 || z < 0 || x >= blocks.length || y >= blocks[0].length || z >= blocks[0][0].length){
            return 0;
        }

        return blocks[x][y][z];
    }*/
}
