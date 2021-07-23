package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.Chunk;
import me.cube.engine.game.world.Terrain;
import me.cube.engine.game.world.World;

import java.util.List;
import java.util.Random;

public class StructurePopulator implements ChunkPopulator {

    private List<SpawnableStructure> spawnableStructures;

    public StructurePopulator(List<SpawnableStructure> spawnableStructures) {
        this.spawnableStructures = spawnableStructures;
    }

    @Override
    public void populateChunk(Terrain terrain, Chunk chunk) {
        for(SpawnableStructure structure : spawnableStructures){

            for(int i = -structure.getSpawnRadius(); i <= structure.getSpawnRadius();i++){
                for(int j = -structure.getSpawnRadius();j <= structure.getSpawnRadius();j++){

                    int chunkX = chunk.getChunkX() + i;
                    int chunkZ = chunk.getChunkZ() + j;

                    Random r = getRandomForChunk(chunkX + structure.randomSeed, chunkZ - structure.randomSeed);
                    int toSpawn = r.nextInt(structure.getMaxPerChunk() + 1);

                    for(int n = 0; n < toSpawn;n++){
                        int chance = r.nextInt(structure.getSpawnRarity());
                        if(chance == 0){
                            int x = r.nextInt(Chunk.CHUNK_WIDTH) + chunkX * Chunk.CHUNK_WIDTH;
                            int z = r.nextInt(Chunk.CHUNK_WIDTH) + chunkZ * Chunk.CHUNK_WIDTH;
                            int y = terrain.heightAt(x, z);

                            Biome biome = terrain.biomeAt(x, z);

                            if(structure.isSpawnableIn(biome)){
                                pasteIntoChunk(chunk, structure, x, y + structure.getSpawnYOffset(), z);
                            }


                        }
                    }

                }
            }

        }

    }

    private static void pasteIntoChunk(Chunk chunk, SpawnableStructure structure, int worldX, int worldY, int worldZ){
        for(int i = 0; i < structure.width();i++){
            for(int j = 0; j < structure.height();j++){
                for(int k = 0; k < structure.length();k++){

                    int x = worldX + i;
                    int y = worldY + j;
                    int z = worldZ + k;

                    if(y < 0 || y >= Chunk.CHUNK_HEIGHT)
                        continue;

                    if(chunk.isWithinChunk(x, z)){
                        if( chunk.blocks[x - chunk.getChunkX() * Chunk.CHUNK_WIDTH][y][z - chunk.getChunkZ() * Chunk.CHUNK_WIDTH] == 0){
                            chunk.blocks[x - chunk.getChunkX() * Chunk.CHUNK_WIDTH][y][z - chunk.getChunkZ() * Chunk.CHUNK_WIDTH] = structure.getCube(i, j, k);
                            chunk.blockFlags[x - chunk.getChunkX() * Chunk.CHUNK_WIDTH][y][z - chunk.getChunkZ() * Chunk.CHUNK_WIDTH] |= Chunk.FLAG_NO_COLOR_BLEED;
                        }
                    }

                }
            }
        }
    }

    private static Random getRandomForChunk(int x, int z){
        if(x < 0){
            x = x * 7;
        }
        if(z < 0){
            z = z * 71;
        }
        return new Random(Math.abs(x * 31 + z * 13));
    }
}
