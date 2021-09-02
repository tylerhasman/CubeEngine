package me.cube.game.world;

import me.cube.engine.Renderer;
import me.cube.engine.Voxel;
import me.cube.engine.file.Assets;
import me.cube.game.world.generator.Biome;
import me.cube.engine.model.AsyncChunkMesh;
import org.joml.Vector3f;

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
    public static final int CHUNK_HEIGHT = 128;

    public final int[][][] blocks;
    public final byte[][][] blockFlags;
    protected final int chunkX, chunkZ;

    protected boolean requireMeshRefresh = false;

    private Voxel mesh, transparentMesh;

    private final Terrain terrain;

    private Future<AsyncChunkMesh> meshGeneratedFuture;

    private boolean disposed;

    private Biome biome;

    protected Chunk(Terrain terrain, int x, int z){
        this.terrain = terrain;
        blocks = new int[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
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


    public void renderTransparent(Vector3f ambientLight, List<DiffuseLight> diffuseLights, Vector3f cameraPosition) {
        if(disposed){
            return;
        }

        if(transparentMesh != null){
            for(int i = 0; i < diffuseLights.size();i++){
                DiffuseLight light = diffuseLights.get(i);
                transparentMesh.getMaterial().setUniform3f("DiffuseLight"+i+"_Position", light.position);
                transparentMesh.getMaterial().setUniform3f("DiffuseLight"+i+"_Color", light.color);
                transparentMesh.getMaterial().setUniformf("DiffuseLight"+i+"_Intensity", light.intensity);
            }
            transparentMesh.getMaterial().setUniform3f("u_AmbientLight", ambientLight);

            transparentMesh.getMaterial().setUniform3f("u_CameraPosition", cameraPosition);

            transparentMesh.render();
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
/*            for(int i = 0; i < diffuseLights.size();i++){
                DiffuseLight light = diffuseLights.get(i);
                mesh.getMaterial().setUniform3f("DiffuseLight"+i+"_Position", light.position);
                mesh.getMaterial().setUniform3f("DiffuseLight"+i+"_Color", light.color);
                mesh.getMaterial().setUniformf("DiffuseLight"+i+"_Intensity", light.intensity);
            }
            mesh.getMaterial().setUniform3f("u_AmbientLight", ambientLight);
            mesh.render();*/
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
        if(x < 0 || y < 0 || z < 0 || x >= blocks.length || y >= blocks[0].length || z >= blocks[0][0].length){
            return;
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
