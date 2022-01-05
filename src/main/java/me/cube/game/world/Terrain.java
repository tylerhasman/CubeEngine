package me.cube.game.world;

import me.cube.engine.Renderer;
import org.joml.AABBf;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static me.cube.game.world.Chunk.CHUNK_HEIGHT;
import static me.cube.game.world.Chunk.CHUNK_WIDTH;
import static me.cube.game.world.World.WORLD_SCALE;

public class Terrain {

    private static final ScheduledExecutorService terrainGeneratorExec = new ScheduledThreadPoolExecutor(2, (r) -> {

        Thread thread = new Thread(r, "TerrainGenerator");

        thread.setDaemon(true);

        return thread;
    });

    private static final float FLUFF_RENDER_DISTANCE = 150;

    private ChunkStorage chunkStorage;
    private int viewDistance;

    private File levelDataFolder;

    private List<Future<ChunkSnapshot>> chunkLoadFutures;

    private BufferedImage albedoMap, heightMap;
    private long albedoLastEdit, heightLastEdit;

    public Terrain(int viewDistance){
        this(viewDistance, "none");
    }

    public Terrain(int viewDistance, String levelName){
        chunkStorage = new ChunkStorage();

        this.viewDistance = viewDistance;

        levelDataFolder = new File("assets/terrain/"+levelName);
        levelDataFolder.mkdir();

        chunkLoadFutures = new ArrayList<>();

        reloadTerrain();

    }

    private void reloadTerrain(){
        unloadAll();

        try {

            File albedoFile = new File(levelDataFolder, "albedo.png");
            File heightFile = new File(levelDataFolder, "heightmap.png");

            albedoLastEdit = albedoFile.lastModified();
            heightLastEdit = heightFile.lastModified();

            albedoMap = ImageIO.read(albedoFile);
            heightMap = ImageIO.read(heightFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean reloadNeeded(){
        File albedoFile = new File(levelDataFolder, "albedo.png");
        File heightFile = new File(levelDataFolder, "heightmap.png");

        return albedoFile.lastModified() > albedoLastEdit || heightFile.lastModified() > heightLastEdit;
    }

    public int countLoadedChunks(){
        return chunkStorage.getLoadedChunks().size();
    }

    public Vector3f rayTrace(Vector3f origin, Vector3f direction, float maxDistance){

        Vector3f out = new Vector3f(origin);
        Vector3f accum = new Vector3f();

        while(!isSolid(out) && accum.lengthSquared() <= maxDistance * maxDistance){
            out.add(direction);
            accum.add(direction);
        }

        return out.sub(direction);
    }

    private void checkChunkLoads(){
        Iterator<Future<ChunkSnapshot>> iterator = chunkLoadFutures.iterator();

        while(iterator.hasNext()){
            Future<ChunkSnapshot> future = iterator.next();

            if(future.isDone()){
                iterator.remove();

                try {
                    ChunkSnapshot chunkSnapshot = future.get();

                    Chunk chunk = chunkStorage.getChunk(chunkSnapshot.x, chunkSnapshot.z);
                    if(chunk != null){
                        for(int i = 0; i < CHUNK_WIDTH;i++){
                            for(int j = 0; j < CHUNK_HEIGHT;j++){
                                for(int k = 0; k < CHUNK_WIDTH;k++){
                                    chunk.setBlock(i, j, k, chunkSnapshot.blocks[i][j][k]);
                                    //chunk.blockFlags[i][j][k] = chunkSnapshot.flags[i][j][k];
                                }
                            }
                        }

                        initializeChunk(chunk);

                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void initializeChunk(Chunk chunk){
        chunk.generateMesh();

        for(int i = -1; i <= 1;i++){
            for(int j = -1; j <= 1;j++){
                if(i == 0 && j == 0){
                    continue;
                }

                Chunk other = chunkStorage.getChunk(i + chunk.getChunkX(), j + chunk.getChunkZ());
                if(other != null){
                    other.requireMeshRefresh = true;
                }

            }
        }
    }

    public void updateTerrain(Vector3f playerPosition){

        if(reloadNeeded()){
            reloadTerrain();
        }

        checkChunkLoads();

        playerPosition.mul(1f / WORLD_SCALE);

        int blockX = (int) playerPosition.x;
        int blockZ = (int) playerPosition.z;

        int centerX = Chunk.worldToChunk(blockX);
        int centerZ = Chunk.worldToChunk(blockZ);

        List<ChunkStorage.ChunkCoordinate> loadNeeded = new ArrayList<>();

        for(int i = -viewDistance;i <= viewDistance;i++){
            for(int j = -viewDistance;j <= viewDistance;j++){

                int chunkX = centerX + i;
                int chunkZ = centerZ + j;

                int dst2 = i * i + j * j;

                if(dst2 < viewDistance * viewDistance){
                    if(!chunkStorage.isLoaded(chunkX, chunkZ)){
                        loadNeeded.add(new ChunkStorage.ChunkCoordinate(chunkX, chunkZ));
                    }
                }

            }
        }

        loadNeeded.sort((c1, c2) -> {
            int d1 = (centerX - c1.x) * (centerX - c1.x) + (centerZ - c1.z) * (centerZ - c1.z);
            int d2 = (centerX - c2.x) * (centerX - c2.x) + (centerZ - c2.z) * (centerZ - c2.z);
            return Integer.compare(d1, d2);
        });

        for(ChunkStorage.ChunkCoordinate chunkToLoad : loadNeeded){
            generateChunk(chunkToLoad.x, chunkToLoad.z);
        }

        boolean unloaded = false;

        float unloadDistance = viewDistance * 2;

        for(Chunk loaded : chunkStorage.getLoadedChunks()){
            int dst2 = loaded.dst2(centerX, centerZ);
            if(dst2 > unloadDistance * unloadDistance){
                loaded.dispose();
                chunkStorage.removeChunk(loaded.getChunkX(), loaded.getChunkZ());
                unloaded = true;
            }
        }

    }

    public void dispose(){
        for(Chunk loaded : chunkStorage.getLoadedChunks()){
            loaded.dispose();
        }
    }

    public int firstEmptyBlockY(int x, int z){
        for(int i = 0; i < CHUNK_HEIGHT;i++){
            if(!isSolid(x, i, z)){
                return i;
            }
        }
        return -1;
    }

    private void generateChunk(final int x, final int z){

        Chunk chunk = new Chunk(this, x, z);

        chunkStorage.addChunk(chunk);

        Future<ChunkSnapshot> future = terrainGeneratorExec.submit(() -> {

            ChunkSnapshot snapshot = new ChunkSnapshot(x, z);

            if(x >= 0 && z >= 0 && x < heightMap.getWidth() / CHUNK_WIDTH && z < heightMap.getHeight() / CHUNK_WIDTH){
                for(int i = 0; i < CHUNK_WIDTH;i++){
                    for(int k = 0; k < CHUNK_WIDTH;k++){

                        int height = 0xFF - heightMap.getRGB(x * CHUNK_WIDTH + i, z * CHUNK_WIDTH + k) & 0xFF;
                        int albedo = albedoMap.getRGB(x * CHUNK_WIDTH + i, z * CHUNK_WIDTH + k);

                        for(int j = 0; j < height;j++){
                            snapshot.blocks[i][j][k] = 0xFF000000 | albedo;
                        }

                    }
                }
            }

            return snapshot;

        });


        chunkLoadFutures.add(future);


    }

    public void render(Renderer renderer) {
        for(Chunk chunk : chunkStorage.getLoadedChunks()){
            chunk.render(renderer);
        }
    }

    public void renderTransparent(Renderer renderer) {
        for(Chunk chunk : chunkStorage.getLoadedChunks()){
            chunk.renderTransparent(renderer);
        }
    }

    public boolean isSolid(Vector3f position){
        return isSolid(position.x, position.y, position.z);
    }

    private boolean isSolid(float x, float y, float z){
        Vector3f inverseScale = new Vector3f(1f / WORLD_SCALE, 1f / WORLD_SCALE, 1f / WORLD_SCALE);

        x *= inverseScale.x;
        y *= inverseScale.y;
        z *= inverseScale.z;

        return isSolid((int) x, (int) y, (int) z);
    }

    public boolean isSolid(int x, int y, int z) {
        if (y >= Chunk.CHUNK_HEIGHT) {
            return false;
        }
        if(y < 0){
            return true;
        }

        return getCube(x, y, z) != 0;
    }

    //TODO: Optimize this function!
    public boolean isColliding(AABBf boundingBox) {
        for(float x = boundingBox.minX;x <= boundingBox.maxX; x += WORLD_SCALE / 2f){
            for(float y = boundingBox.minY;y <= boundingBox.maxY; y += WORLD_SCALE / 2f){
                for(float z = boundingBox.minZ;z <= boundingBox.maxZ; z += WORLD_SCALE / 2f){
                    if(isSolid(x, y, z)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setCube(int x, int y, int z, int cube) {

        int chunkX = Chunk.worldToChunk(x);
        int chunkZ = Chunk.worldToChunk(z);

        Chunk chunk = chunkStorage.getChunk(chunkX, chunkZ);

        if(chunk != null){
            chunk.setBlock(x - chunkX * CHUNK_WIDTH, y, z - chunkZ * CHUNK_WIDTH, cube);
        }

    }

    /**
     * Checks if a chunk is loaded using world coordinates
     * @param worldX world x
     * @param worldZ world z
     */
    public boolean isLoaded(int worldX, int worldZ){
        int chunkX = Chunk.worldToChunk(worldX);
        int chunkZ = Chunk.worldToChunk(worldZ);

        return chunkStorage.isLoaded(chunkX, chunkZ);
    }

    public int getCube(int x, int y, int z) {
        int chunkX = Chunk.worldToChunk(x);
        int chunkZ = Chunk.worldToChunk(z);

        Chunk chunk = chunkStorage.getChunk(chunkX, chunkZ);

        if(chunk != null){
            return chunk.getBlock(x - chunkX * CHUNK_WIDTH, y, z - chunkZ * CHUNK_WIDTH);
        }

        return 0;
    }

    public void unloadAll() {
        for(Chunk loaded : chunkStorage.getLoadedChunks()){
            loaded.dispose();
            chunkStorage.removeChunk(loaded.getChunkX(), loaded.getChunkZ());
        }
    }
}
