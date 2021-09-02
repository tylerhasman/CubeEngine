package me.cube.game.world;

import me.cube.engine.Voxel;
import me.cube.game.world.generator.*;
import org.joml.AABBf;
import org.joml.Vector3f;

import java.io.File;
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
    private TerrainGenerator terrainGenerator;
    private int viewDistance;

    private File levelDataFolder;

    private List<ChunkPopulator> populators;

    private List<Voxel> fluffs;

    private List<Future<ChunkSnapshot>> chunkLoadFutures;

    public Terrain(int viewDistance){
        this(viewDistance, "none");
    }

    public Terrain(int viewDistance, String levelName){
        chunkStorage = new ChunkStorage();
        terrainGenerator = new PerlinTerrainGenerator();
        populators = new ArrayList<>();

        this.viewDistance = viewDistance;

        levelDataFolder = new File("assets/terrain/"+levelName);
        levelDataFolder.mkdir();

        fluffs = new ArrayList<>();

        chunkLoadFutures = new ArrayList<>();

        //populators.add(new StickStructurePopulator());
        populators.add(new ForestTreePopulator(0x342179FAFAL));
        populators.add(new RockPopulator(0x34, 120, 2));
        populators.add(new RockPopulator(0x34, 350, 3));
        populators.add(new RockPopulator(0x34, 500, 4));
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
                                    chunk.blocks[i][j][k] = chunkSnapshot.blocks[i][j][k];
                                    chunk.blockFlags[i][j][k] = chunkSnapshot.flags[i][j][k];
                                }
                            }
                        }

                        chunk.setBiome(chunkSnapshot.biome);

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
/*
        Random random = new Random();
        int fluffCount = random.nextInt(10);

        for(int i = 0; i < fluffCount;i++){
            float spawnX = chunk.getChunkX() * CHUNK_WIDTH + random.nextFloat() * CHUNK_WIDTH;
            float spawnZ = chunk.getChunkZ() * CHUNK_WIDTH + random.nextFloat() * CHUNK_WIDTH;
            float spawnY = heightAt((int) Math.floor(spawnX), (int) Math.floor(spawnZ));

            Voxel voxel = new Voxel();

            if(random.nextInt(20) == 0){
                voxel.model = Assets.loadModel("flower.vxm");
            }else{
                voxel.model = Assets.loadModel("grass.vxm");
            }

            voxel.getTransform().identity()
                    .translate(spawnX, spawnY + 1.5f, spawnZ)
                    .rotateAxis(random.nextFloat() * MathUtil.PI2, 0, 1, 0)
                    .scale(random.nextFloat() * 1.5f + 0.5f)
                    .scale(0.1f);

            fluffs.add(voxel);

        }*/
    }

    public void updateTerrain(Vector3f playerPosition){

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

        if(unloaded){
            fluffs.removeIf(fluff -> {
                Vector3f position = fluff.getTransform().getPosition();
                int chunkX = (int) Math.floor(position.x / CHUNK_WIDTH);
                int chunkZ = (int) Math.floor(position.z / CHUNK_WIDTH);

                return !chunkStorage.isLoaded(chunkX, chunkZ);
            });
        }

    }

    public TerrainGenerator getTerrainGenerator() {
        return terrainGenerator;
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

/*
    public Biome biomeAt(int x, int z){
        return terrainGenerator.biomeAt(x, z);
    }

    public int heightAt(int x, int z){
        return terrainGenerator.heightAt(x, z);
    }
*/

    public Biome biomeAt(int x, int z){
        int chunkX = Math.floorDiv(x, CHUNK_WIDTH);
        int chunkZ = Math.floorDiv(z, CHUNK_WIDTH);

        if(chunkStorage.isLoaded(chunkX, chunkZ)){
            Chunk chunk = chunkStorage.getChunk(chunkX, chunkZ);

            return chunk.getBiome();
        }

        return terrainGenerator.chunkBiome(chunkX, chunkZ);
    }

    public int groundHeightAt(int x, int z){
        /*int chunkX = Math.floorDiv(x, CHUNK_WIDTH);
        int chunkZ = Math.floorDiv(z, CHUNK_WIDTH);

        int xInChunk = x - chunkX * CHUNK_WIDTH;
        int zInChunk = z - chunkZ * CHUNK_WIDTH;

        if(chunkStorage.isLoaded(chunkX, chunkZ)){
            Chunk chunk = chunkStorage.getChunk(chunkX, chunkZ);

            return chunk.firstEmptyBlock(xInChunk, zInChunk) - 1;
        }

        */

        return terrainGenerator.heightAt(x, z);
    }

    private void generateChunk(final int x, final int z){

        Chunk chunk = new Chunk(this, x, z);

        chunkStorage.addChunk(chunk);

        Future<ChunkSnapshot> future = terrainGeneratorExec.submit(() -> {

            long time = System.currentTimeMillis();

            ChunkSnapshot snapshot = new ChunkSnapshot(x, z);

            //TODO: Just have this be thread safe so we can use terrainGenerator
            PerlinTerrainGenerator perlinTerrainGenerator = new PerlinTerrainGenerator();

            perlinTerrainGenerator.generateChunk(x, z, snapshot);

            time = System.currentTimeMillis() - time;

            for(ChunkPopulator populator : populators){
                populator.populateChunk(this, snapshot);
            }

            //System.out.println("Took "+time+"ms to generate chunk "+x+" "+z);

            return snapshot;

        });


        chunkLoadFutures.add(future);


    }

    public void render(Vector3f ambientLight, List<DiffuseLight> diffuseLights, Vector3f playerPosition) {
        for(Chunk chunk : chunkStorage.getLoadedChunks()){
            chunk.render(ambientLight, diffuseLights);
        }

        for(Voxel fluff : fluffs){
            if(fluff.getTransform().getPosition().distanceSquared(playerPosition) < FLUFF_RENDER_DISTANCE * FLUFF_RENDER_DISTANCE){
                fluff.getMaterial().setUniform3f("u_AmbientLight", ambientLight);

                fluff.render();
            }
        }
    }

    public void renderTransparent(Vector3f ambientLight, List<DiffuseLight> diffuseLights, Vector3f cameraPosition) {

        List<Chunk> sorted = chunkStorage.getLoadedChunks();
        sorted.sort((c1, c2) -> {
            int pcx = (int) Math.floor(cameraPosition.x / CHUNK_WIDTH);
            int pcz = (int) Math.floor(cameraPosition.z / CHUNK_WIDTH);
            return Integer.compare(c2.dst2(pcx, pcz), c1.dst2(pcx, pcz));
        });

        for(Chunk chunk : sorted){
            chunk.renderTransparent(ambientLight, diffuseLights, cameraPosition);
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
