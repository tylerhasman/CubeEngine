package me.cube.game.world.generator;

import me.cube.game.world.Chunk;
import me.cube.game.world.ChunkSnapshot;
import me.cube.game.world.Terrain;
import me.cube.engine.util.MathUtil;
import org.joml.Vector2f;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static me.cube.game.world.Chunk.CHUNK_WIDTH;

/**
 * Generates structures that can be larger than a single chunk.
 * Structures are distributed evenly in square cells of size {@link #cellSize}
 *
 * Structures will be bound to their own cell and cannot place blocks outside of them.
 */
public abstract class StructureChunkPopulator implements ChunkPopulator {

    private final int cellSize;

    private Map<Long, GeneratedStructure> generated;

    private final long seed;

    public StructureChunkPopulator(int cellSize, long seed){
        this.cellSize = cellSize;
        this.seed = seed;
        this.generated = Collections.synchronizedMap(new HashMap<>());
    }

    private Random getRandom(int cellX, int cellZ){

        int seedX = (int) (seed >> 32);
        int seedZ = (int) seed;

        return new Random(MathUtil.hash(cellX + seedX, cellZ + seedZ));
    }

    private Vector2f findStructurePosition(int cellX, int cellZ){
        Random random = getRandom(cellX, cellZ);

        float x = random.nextFloat() * cellSize + cellX * cellSize;
        float y = random.nextFloat() * cellSize + cellZ * cellSize;

        return new Vector2f(x, y);
    }

    protected int getSpawnHeight(Terrain terrain, int spawnX, int spawnZ, Random random){
        return terrain.groundHeightAt(spawnX, spawnZ);
    }

    public abstract void generateStructure(GeneratedStructure structure, Random random, Biome biome);

    /**
     * This method finds all cells interesecting this chunk, generates structures for those cells and then places any
     * interesecting blocks into this chunk.
     *
     * There are three cases to check.
     *
     * 1.
     * {@link #cellSize} < {@link Chunk#CHUNK_WIDTH}
     *
     * 2.
     * {@link #cellSize} > {@link Chunk#CHUNK_WIDTH}
     */
    @Override
    public void populateChunk(Terrain terrain, ChunkSnapshot chunk) {
        int startCellX = Math.floorDiv(chunk.getChunkX() * CHUNK_WIDTH, cellSize);
        int startCellZ = Math.floorDiv(chunk.getChunkZ() * CHUNK_WIDTH, cellSize);


        //This wont work entirely
        //It will miss the last intersecting chunks
        //TODO Fix ^^^^

        for(int i= -1; i <= 1;i += 1){
            for(int j = -1; j <= 1;j += 1){

                int cellX = startCellX + i;
                int cellZ = startCellZ + j;

                Vector2f position = findStructurePosition(cellX, cellZ);

                int spawnX = (int) Math.floor(position.x);
                int spawnZ = (int) Math.floor(position.y);
                int spawnY = getSpawnHeight(terrain, spawnX, spawnZ, getRandom(cellX, cellZ));//chunk.firstEmptyBlock(spawnX, spawnZ);

                GeneratedStructure structure = loadStructure(cellX, cellZ, terrain.biomeAt(spawnX, spawnZ));

                structure.pasteInto(chunk, spawnX, spawnY, spawnZ);
            }
        }


    }

    private GeneratedStructure getStructure(int cellX, int cellZ){
        long key = MathUtil.hash(cellX, cellZ);

        return generated.get(key);
    }

    private void setStructure(int cellX, int cellZ, GeneratedStructure structure){
        long key = MathUtil.hash(cellX, cellZ);

        generated.put(key, structure);
    }

    private GeneratedStructure loadStructure(int cellX, int cellZ, Biome biome){

        GeneratedStructure structure = getStructure(cellX, cellZ);

        if(structure != null){
            return structure;
        }

        structure = new GeneratedStructure();

        generateStructure(structure, getRandom(cellX, cellZ), biome);

/*        if(structure.blocks.size() > 0)
            setStructure(cellX, cellZ, structure);*/

        return structure;
    }

    /*    @Override
    public void populateChunk(Terrain terrain, Chunk chunk) {
        int cellX = Math.floorDiv(chunk.getChunkX() * Chunk.CHUNK_WIDTH, cellWidth);
        int cellZ = Math.floorDiv(chunk.getChunkZ() * Chunk.CHUNK_WIDTH, cellHeight);

        long key = cellX;
        key = (key << 32) | cellZ;

        Vector2f position = findStructurePosition(cellX, cellZ);

        int spawnX = (int) Math.floor(position.x);
        int spawnZ = (int) Math.floor(position.y);
        int spawnY = terrain.groundHeightAt(spawnX, spawnZ);//chunk.firstEmptyBlock(spawnX, spawnZ);

        if(!generated.containsKey(key)){
            GeneratedStructure structure = new GeneratedStructure();

            generateStructure(structure, getRandom(cellX, cellZ));

            generated.put(key, structure);
        }

        generated.get(key).pasteInto(chunk, spawnX, spawnY, spawnZ);

    }*/

    public static class GeneratedStructure {

        private static final int AIR_BLOCK = -1;

        private final Map<Long, int[][][]> blocks;

        public GeneratedStructure() {
            blocks = new HashMap<>();
        }

        public void setAir(int x, int y, int z){
            setCube(x, y, z, AIR_BLOCK);
        }

        /**
         * Sets a block in the generated structure.
         */
        public void setCube(int x, int y, int z, int color){

            if(y < 0 || y >= Chunk.CHUNK_HEIGHT){
                return;
            }

            int chunkX = Math.floorDiv(x, CHUNK_WIDTH);
            int chunkZ = Math.floorDiv(z, CHUNK_WIDTH);

            long key = MathUtil.hash(chunkX, chunkZ);

            if(!blocks.containsKey(key)){
                blocks.put(key, new int[CHUNK_WIDTH][Chunk.CHUNK_HEIGHT][CHUNK_WIDTH]);
            }

            int xInChunk = x - chunkX * CHUNK_WIDTH;
            int zInChunk = z - chunkZ * CHUNK_WIDTH;

            blocks.get(key)[xInChunk][y][zInChunk] = color;
        }

        public int getCube(int x, int y, int z){
            if(y < 0 || y >= Chunk.CHUNK_HEIGHT){
                return 0;
            }

            int chunkX = Math.floorDiv(x, CHUNK_WIDTH);
            int chunkZ = Math.floorDiv(z, CHUNK_WIDTH);

            long key = MathUtil.hash(chunkX, chunkZ);

            if(!blocks.containsKey(key)){
                return 0;
            }

            int xInChunk = x - chunkX * CHUNK_WIDTH;
            int zInChunk = z - chunkX * CHUNK_WIDTH;

            return blocks.get(key)[xInChunk][y][zInChunk];
        }

        public int pasteInto(ChunkSnapshot chunk, int worldX, int worldY, int worldZ){

            int centerChunkX = Math.floorDiv(worldX, CHUNK_WIDTH);
            int centerChunkZ = Math.floorDiv(worldZ, CHUNK_WIDTH);

            int offsetX = chunk.getChunkX() - centerChunkX;
            int offsetZ = chunk.getChunkZ() - centerChunkZ;

            long key = MathUtil.hash(offsetX, offsetZ);

            if(!blocks.containsKey(key)){
                return 0;
            }

            int changed = 0;

            int[][][] selected = blocks.get(key);

            for(int i = 0; i < CHUNK_WIDTH;i++){
                for(int j = 0; j < Chunk.CHUNK_HEIGHT;j++){
                    for(int k = 0; k < CHUNK_WIDTH;k++){

                        int x = worldX + i;
                        int y = worldY + j;
                        int z = worldZ + k;

                        if(y >= Chunk.CHUNK_HEIGHT)
                            continue;

                        int color = selected[i][j][k];

                        if(color == AIR_BLOCK){
                            chunk.blocks[i][y][k] = 0;
                            changed++;
                        }else if(color != 0){
                            chunk.blocks[i][y][k] = color;
                            changed++;
                        }

                    }
                }
            }
            return changed;
        }

    }

}
