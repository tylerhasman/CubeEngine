package me.cube.engine.game.world;

import me.cube.engine.Voxel;
import me.cube.engine.file.Assets;
import me.cube.engine.file.ChunkSave;
import me.cube.engine.file.VoxelFile;
import me.cube.engine.game.world.generator.*;
import me.cube.engine.util.MathUtil;
import org.joml.AABBf;
import org.joml.Vector3f;

import java.io.File;
import java.util.*;

import static me.cube.engine.game.world.Chunk.CHUNK_HEIGHT;
import static me.cube.engine.game.world.Chunk.CHUNK_WIDTH;
import static me.cube.engine.game.world.World.WORLD_SCALE;

public class Terrain {

    private static final float FLUFF_RENDER_DISTANCE = 150;

    private ChunkStorage chunkStorage;
    private TerrainGenerator terrainGenerator;
    private int viewDistance;

    private File levelDataFolder;

    private List<ChunkPopulator> populators;

    private List<Voxel> fluffs;

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

        initializeStructures();
    }

    private void initializeStructures(){

        VoxelFile treeData = Assets.loadVoxelData("tree.vxm", false);
        VoxelFile treeData2 = Assets.loadVoxelData("tree.vox", false);

        VoxelFile rockData = Assets.loadVoxelData("rock.vxm", false);

   /*     SpawnableStructure tree = new SpawnableStructure(treeData.toVoxelColorArray(), new Biome[] {
                Biome.FOREST
        }, 1, 2, 0x3231) ;*//*
        SpawnableStructure tree2 = new SpawnableStructure(treeData2.toVoxelColorArray(), new Biome[] {
                Biome.FOREST
        }, 10, 2, 0x1221) ;*//*
        SpawnableStructure rock = new SpawnableStructure(treeData2.toVoxelColorArray(), new Biome[] {
                Biome.MOUNTAINS
        }, 45, 3, 0x3125) ;

        StructurePopulator structurePopulator = new StructurePopulator(Arrays.asList(tree, rock));
*/
//        populators.add(structurePopulator);

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

    public void updateTerrain(Vector3f playerPosition){

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

        for(Chunk loaded : chunkStorage.getLoadedChunks()){
            int dst2 = loaded.dst2(centerX, centerZ);
            if(dst2 > viewDistance * viewDistance + 64){
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

    public Biome biomeAt(int x, int z){
        return terrainGenerator.biomeAt(x, z);
    }

    public int heightAt(int x, int z){
        return terrainGenerator.heightAt(x, z);
    }

    private void generateChunk(int x, int z){

        File chunkFile = new File(levelDataFolder, "chunk-"+x+"-"+z+".dat");

        ChunkSave chunkSave = new ChunkSave(chunkFile);

        Chunk chunk = new Chunk(this, x, z, chunkSave);

        long time = System.currentTimeMillis();

        terrainGenerator.generateChunk(chunk);

        System.out.println("Took "+(System.currentTimeMillis()-time)+"ms to generate chunk "+x+" "+z);

        if(chunkSave.hasChanges()){
            chunkSave.applyTo(chunk);
        }

        for(ChunkPopulator chunkPopulator : populators){
            chunkPopulator.populateChunk(this, chunk);
        }

        chunk.generateMesh();

        chunkStorage.addChunk(chunk);

        for(int i = -1; i <= 1;i++){
            for(int j = -1; j <= 1;j++){
                if(i == 0 && j == 0){
                    continue;
                }

                Chunk other = chunkStorage.getChunk(i + x, j + z);
                if(other != null){
                    //other.requireMeshRefresh = true;
                }

            }
        }

        Random random = new Random();
        int fluffCount = random.nextInt(10);

        for(int i = 0; i < fluffCount;i++){
            float spawnX = x * CHUNK_WIDTH + random.nextFloat() * CHUNK_WIDTH;
            float spawnZ = z * CHUNK_WIDTH + random.nextFloat() * CHUNK_WIDTH;
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

        }

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

    public void renderTransparent(Vector3f ambientLight, List<DiffuseLight> diffuseLights, Vector3f playerPosition) {
        for(Chunk chunk : chunkStorage.getLoadedChunks()){
            chunk.renderTransparent(ambientLight, diffuseLights);
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

/*    public static int convertWorldToChunk(int xz) {
        return (int) Math.floor((float) xz / (float) CHUNK_WIDTH);
    }*/

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

    public int getCube(int x, int y, int z) {
        int chunkX = Chunk.worldToChunk(x);
        int chunkZ = Chunk.worldToChunk(z);

        Chunk chunk = chunkStorage.getChunk(chunkX, chunkZ);

        if(chunk != null){
            return chunk.getBlock(x - chunkX * CHUNK_WIDTH, y, z - chunkZ * CHUNK_WIDTH);
        }

        return 0;
    }
}
