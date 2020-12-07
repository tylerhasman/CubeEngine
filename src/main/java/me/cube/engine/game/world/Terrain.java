package me.cube.engine.game.world;

import me.cube.engine.Window;
import me.cube.engine.file.Assets;
import me.cube.engine.file.VxmFile;
import me.cube.engine.game.entity.Flora;
import me.cube.engine.game.world.Chunk;
import me.cube.engine.game.world.ChunkStorage;
import me.cube.engine.game.world.generator.*;
import org.joml.AABBf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.*;

import static me.cube.engine.game.world.Chunk.CHUNK_HEIGHT;
import static me.cube.engine.game.world.Chunk.CHUNK_WIDTH;
import static me.cube.engine.game.world.World.WORLD_SCALE;

public class Terrain {

    private ChunkStorage chunkStorage;

    private TerrainGenerator terrainGenerator;

    private int viewDistance;

    public Terrain(int viewDistance){
        chunkStorage = new ChunkStorage();
        terrainGenerator = new PerlinTerrainGenerator();

        this.viewDistance = viewDistance;
    }

    public Vector3f rayTrace(Vector3f origin, Vector3f direction, float maxDistance){

        Vector3f out = new Vector3f(origin);
        Vector3f accum = new Vector3f();

        while(!isSolid(out) && accum.lengthSquared() <= maxDistance * maxDistance){
            out.add(direction);
            accum.add(direction);
        }

        return out;
    }

    public void updateTerrain(Vector3f playerPosition){

        playerPosition.mul(1f / WORLD_SCALE);

        int blockX = (int) playerPosition.x;
        int blockZ = (int) playerPosition.z;

        int centerX = convertWorldToChunk(blockX);
        int centerZ = convertWorldToChunk(blockZ);

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

        for(Chunk loaded : chunkStorage.getLoadedChunks()){
            int dst2 = loaded.dst2(centerX, centerZ);
            if(dst2 > viewDistance * viewDistance + 4 * 4){
                loaded.dispose();
                chunkStorage.removeChunk(loaded.getChunkX(), loaded.getChunkZ());
            }
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
        Chunk chunk = new Chunk(this, x, z);
        terrainGenerator.generateChunk(chunk);

        chunk.generateMesh();

        chunkStorage.addChunk(chunk);

        for(int i = -1; i <= 1;i++){
            for(int j = -1; j <= 1;j++){
                if(i == 0 && j == 0){
                    continue;
                }

                Chunk other = chunkStorage.getChunk(i + x, j + z);
                if(other != null){
                    other.requireMeshRefresh = true;
                }

            }
        }
    }

    public void render() {
        for(Chunk chunk : chunkStorage.getLoadedChunks()){
            chunk.render();
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

    //TODO: Fix this in negative x,z's. The collision is offset a little
    public boolean isSolid(int x, int y, int z) {
        if (y >= Chunk.CHUNK_HEIGHT) {
            return false;
        }
        if(y < 0){
            return true;
        }

        int chunkX = convertWorldToChunk(x);
        int chunkZ = convertWorldToChunk(z);

        Chunk chunk = chunkStorage.getChunk(chunkX, chunkZ);

        if(chunk == null){
            return true;
        }

        if(x < 0){
            x += CHUNK_WIDTH * Math.abs(chunkX);
        }

        if(z < 0){
            z += CHUNK_WIDTH * Math.abs(chunkZ);
        }

        int xInChunk = x % Chunk.CHUNK_WIDTH;
        int zInChunk = z % Chunk.CHUNK_WIDTH;

        //Warning, this wont work for negative chunks!!
        return chunk.isSolid(Math.abs(xInChunk), y, Math.abs(zInChunk));
    }

    private static int convertWorldToChunk(int xz){

        if(xz >= 0){
            return xz / Chunk.CHUNK_WIDTH;
        }

        int chunkXZ = xz / Chunk.CHUNK_WIDTH;

        chunkXZ--;

        if(xz % CHUNK_WIDTH == 0){//This is a fucked solution let me tell you
            chunkXZ++;
        }

        return chunkXZ;
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

        int chunkX = convertWorldToChunk(x);
        int chunkZ = convertWorldToChunk(z);

        Chunk chunk = chunkStorage.getChunk(chunkX, chunkZ);

        if(chunk != null){
            chunk.setBlockWorldCoords(x, y, z, cube);
        }

    }

    public int getCube(int x, int y, int z) {
        int chunkX = convertWorldToChunk(x);
        int chunkZ = convertWorldToChunk(z);

        Chunk chunk = chunkStorage.getChunk(chunkX, chunkZ);

        if(chunk != null){
            return chunk.getBlockWorldCoords(x, y, z);
        }

        return 0;
    }
}
