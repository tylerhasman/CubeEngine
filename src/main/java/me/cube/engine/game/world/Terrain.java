package me.cube.engine.game.world;

import me.cube.engine.file.VxmFile;
import me.cube.engine.game.world.Chunk;
import me.cube.engine.game.world.ChunkStorage;
import me.cube.engine.game.world.generator.PerlinTerrainGenerator;
import me.cube.engine.game.world.generator.TerrainGenerator;
import org.joml.AABBf;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Random;

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

    public void updateTerrain(Vector3f playerPosition){

        playerPosition.mul(1f / WORLD_SCALE);

        int blockX = (int) playerPosition.x;
        int blockZ = (int) playerPosition.z;

        int centerX = convertWorldToChunk(blockX);
        int centerZ = convertWorldToChunk(blockZ);

        for(int i = -viewDistance;i <= viewDistance;i++){
            for(int j = -viewDistance;j <= viewDistance;j++){

                int chunkX = centerX + i;
                int chunkZ = centerZ + j;

                if(!chunkStorage.isLoaded(chunkX, chunkZ)){
                    generateChunk(chunkX, chunkZ);
                }

            }
        }
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

        if(chunk == null)
            return false;

        //Warning, this wont work for negative chunks!!
        return chunk.isSolid(x % Chunk.CHUNK_WIDTH, y, z % Chunk.CHUNK_WIDTH);
    }

    private static int convertWorldToChunk(int xz){
        int chunkXZ = xz / Chunk.CHUNK_WIDTH;

        if(chunkXZ == 0 && xz < 0){
            return -1;
        }

        return chunkXZ;
    }

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

}
