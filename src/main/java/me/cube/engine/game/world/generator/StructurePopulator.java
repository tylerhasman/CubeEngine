package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.Chunk;
import me.cube.engine.game.world.World;

import java.util.List;
import java.util.Random;

public class StructurePopulator implements ChunkPopulator {

    private List<SpawnableStructure> spawnableStructures;
    private World world;

    public StructurePopulator(World world, List<SpawnableStructure> spawnableStructures) {
        this.spawnableStructures = spawnableStructures;
        this.world = world;
    }

    @Override
    public void populateChunk(Chunk chunk) {
        for(SpawnableStructure structure : spawnableStructures){

            for(int i = -structure.getSpawnRadius(); i <= structure.getSpawnRadius();i++){
                for(int j = -structure.getSpawnRadius();j <= structure.getSpawnRadius();j++){

                    int chunkX = chunk.getChunkX() + i;
                    int chunkZ = chunk.getChunkZ() + j;

                    Random r = getRandomForChunk(chunkX, chunkZ);
                    int toSpawn = r.nextInt(structure.getMaxPerChunk() + 1);

                    for(int n = 0; n < toSpawn;n++){
                        int chance = r.nextInt(structure.getSpawnRarity());
                        if(chance == 0){
                            int x = r.nextInt(Chunk.CHUNK_WIDTH) + chunkX * Chunk.CHUNK_WIDTH;
                            int z = r.nextInt(Chunk.CHUNK_WIDTH) + chunkZ * Chunk.CHUNK_WIDTH;
                            int y = world.getTerrain().firstEmptyBlockY(x, z);

                            Biome biome = world.getTerrain().biomeAt(x, z);

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
                        if( chunk.blocks[x - chunk.getChunkX() * Chunk.CHUNK_WIDTH][y][z - chunk.getChunkZ() * Chunk.CHUNK_WIDTH] == 0)
                            chunk.blocks[x - chunk.getChunkX() * Chunk.CHUNK_WIDTH][y][z - chunk.getChunkZ() * Chunk.CHUNK_WIDTH] = structure.getCube(i, j, k);
                    }

                }
            }
        }
    }

    private static Random getRandomForChunk(int x, int z){
        return new Random(x * 31 + z * 13);
    }
}
