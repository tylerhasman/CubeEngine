package me.cube.game.world;

import me.cube.engine.Camera;
import me.cube.engine.Renderer;
import me.cube.engine.Voxel;
import me.cube.engine.file.Assets;
import me.cube.game.world.generator.Biome;
import me.cube.engine.model.AsyncChunkMesh;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Chunk {

    private static final ScheduledExecutorService meshGeneratorExec = new ScheduledThreadPoolExecutor(2, (r) -> {
        Thread thread = new Thread(r, "ChunkMeshGenerator");

        thread.setDaemon(true);

        return thread;
    });

    public static final byte FLAG_NO_COLOR_BLEED = 1;

    public static final int CHUNK_WIDTH = 32;
    public static final int CHUNK_HEIGHT = 256;
    private static final int INNER_CHUNKS = 8;

    //private final int[][][] blocks;

    private final int[][][][] innerChunks;

    private final byte[][][] blockFlags;
    protected final int chunkX, chunkZ;

    protected boolean requireMeshRefresh = false;

    private Voxel mesh, transparentMesh;

    private final Terrain terrain;

    private Future<AsyncChunkMesh> meshGeneratedFuture;

    private boolean disposed;

    private Biome biome;

    protected Chunk(Terrain terrain, int x, int z){
        this.terrain = terrain;
        //blocks = new int[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
        innerChunks = new int[INNER_CHUNKS][0][0][0];

        for(int i = 0; i < INNER_CHUNKS;i++){
            innerChunks[i] = null;
        }

        blockFlags = new byte[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
        this.chunkX = x;
        this.chunkZ = z;
        mesh = null;
        disposed = false;
        biome = Biome.PLAINS;
    }

    public Voxel getMesh() {
        return mesh;
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
            if(transparentMesh != null && transparentMesh.model != null){
                transparentMesh.model.dispose();
            }
            disposed = true;
        }
    }


    public void renderTransparent(Renderer renderer) {
        if(disposed){
            return;
        }

        if(transparentMesh != null){
            transparentMesh.getMaterial().setUniform3f("u_CameraPosition", Camera.getCameraPosition());

            renderer.render(transparentMesh);
        }
    }

    public void render(Renderer renderer){
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

                    if(transparentMesh != null && transparentMesh.model != null){
                        transparentMesh.model.dispose();
                        transparentMesh = null;
                    }


                    AsyncChunkMesh chunkMesh = meshGeneratedFuture.get();
                    chunkMesh.initialize();

                    mesh = new Voxel("Chunk "+chunkX+" "+chunkZ, chunkMesh);
                    mesh.scale.set(World.WORLD_SCALE);
                    mesh.position.add(chunkX * CHUNK_WIDTH, 0, chunkZ * CHUNK_WIDTH);

                    if(chunkMesh.hasTransparency()){
                        transparentMesh = new Voxel("ChunkTrasparent"+chunkX+" "+chunkZ, chunkMesh.createTransparentMesh(), Assets.loadMaterial("transparent.json"));
                        transparentMesh.transparent = true;
                        transparentMesh.scale.set(World.WORLD_SCALE);
                        transparentMesh.position.add(chunkX * CHUNK_WIDTH, 0, chunkZ * CHUNK_WIDTH);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }finally{
                    meshGeneratedFuture = null;
                }
            }
        }

        if(mesh != null){
            renderer.render(mesh);
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

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    public Biome getBiome() {
        return biome;
    }

    public boolean isWithinChunk(int worldX, int worldZ){
        worldX -= chunkX * Chunk.CHUNK_WIDTH;
        worldZ -= chunkZ * Chunk.CHUNK_WIDTH;

        return worldX >= 0 && worldZ >= 0 && worldX < CHUNK_WIDTH && worldZ < CHUNK_WIDTH;
    }

    protected void setBlock(int x, int y, int z, int color){
        if(x < 0 || y < 0 || z < 0 || x >= CHUNK_WIDTH || y >= CHUNK_HEIGHT || z >= CHUNK_WIDTH){
            return;
        }

        int innerChunkIndex = y / (CHUNK_HEIGHT / INNER_CHUNKS);

        if(innerChunks[innerChunkIndex] == null){
            if(color != 0){
                innerChunks[innerChunkIndex] = new int[CHUNK_WIDTH][(CHUNK_HEIGHT / INNER_CHUNKS)][CHUNK_WIDTH];
            }
        }

        if(innerChunks[innerChunkIndex] != null){
            innerChunks[innerChunkIndex][x][y % (CHUNK_HEIGHT / INNER_CHUNKS)][z] = color;
            requireMeshRefresh = true;
        }

    }

    public int getBlock(int x, int y, int z){
        if(x < 0 || y < 0 || z < 0 || x >= CHUNK_WIDTH|| y >= CHUNK_HEIGHT || z >= CHUNK_WIDTH){
            return 0;
        }

        int innerChunkIndex = y / (CHUNK_HEIGHT / INNER_CHUNKS);

        if(innerChunks[innerChunkIndex] != null){
            return innerChunks[innerChunkIndex][x][y % (CHUNK_HEIGHT / INNER_CHUNKS)][z];
        }

        return 0;
    }

    public int firstEmptyBlock(int x, int z){
        for(int y = 0; y < CHUNK_HEIGHT;y++){
            if(getBlock(x, y, z) == 0){
                return y;
            }
        }
        return CHUNK_HEIGHT-1;
    }

    /**
     * Calculates which chunk a world coordinate lies in
     */
    public static int worldToChunk(int worldCoord){
        return (int) Math.floor((float) worldCoord / (float) CHUNK_WIDTH);
    }


}
