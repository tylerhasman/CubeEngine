package me.cube.engine.model;

import me.cube.engine.game.world.Chunk;
import me.cube.engine.game.world.Terrain;
import me.cube.engine.util.FloatArray;

public class AsyncChunkMesh extends VoxelMesh {

    private FloatArray vertices, colors, normals;

    public AsyncChunkMesh(Terrain terrain, Chunk chunk){
        super(Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, Chunk.CHUNK_WIDTH);
        vertices = new FloatArray(4096);
        colors = new FloatArray(4096);
        normals = new FloatArray(4096);

        generate(terrain, chunk);
    }

    /**
     * Must be called from the main thread!
     */
    public void initialize(){
        initialize(vertices.toArray(), colors.toArray(), normals.toArray());
        vertices = null;
        colors = null;
        normals = null;
    }

    private void generate(Terrain terrain, Chunk chunk){
        Cube cube = new Cube();
        for(int i = 0; i < Chunk.CHUNK_WIDTH;i++){
            for(int j = 0; j < Chunk.CHUNK_HEIGHT;j++){
                for(int k = 0;k < Chunk.CHUNK_WIDTH;k++){

                    int color = chunk.blocks[i][j][k];

                    if(color != 0){

                        cube.flags = 0;

                        cube.red = ((color >> 16) & 255) / 255F;
                        cube.green = ((color >> 8) & 255) / 255F;
                        cube.blue = (color & 255) / 255F;

                        cube.x = i;
                        cube.y = j;
                        cube.z = k;

                        cube.top = !isSolid(terrain, chunk, i, j + 1, k);
                        cube.bottom = !isSolid(terrain, chunk, i, j - 1, k);
                        cube.north = !isSolid(terrain, chunk, i, j, k-1);
                        cube.south = !isSolid(terrain, chunk, i, j, k+1);
                        cube.east = !isSolid(terrain, chunk, i-1, j, k);
                        cube.west = !isSolid(terrain, chunk, i+1, j, k);

                        int adjacent = countAdjacentCoveringBlocks(terrain, chunk, i, j, k, 2);

                        cube.red *= (1f - (adjacent) / 250f);
                        cube.green *= (1f - (adjacent) / 250f);
                        cube.blue *= (1f - (adjacent) / 250f);

                        if(cube.top){
                            int above = countAboveBlocks(terrain, chunk, i, j, k, 12);

                            cube.red *= (1f - (above) / 16f);
                            cube.green *= (1f - (above) / 16f);
                            cube.blue *= (1f - (above) / 16f);
                        }

                        cube.flags |= (isCompletelyCovered(terrain, chunk, i, j-1, k) ? Cube.SHADE_SIDES : 0);

                        cube.generate(vertices, normals, colors);
                    }


                }
            }
        }
    }

    private static int countAboveBlocks(Terrain terrain, Chunk chunk, int i, int j, int k, int max){

        int count = 0;
        for(int y = j + 1; y < j + max + 1;y++){
            if(isSolid(terrain, chunk, i, y, k)){
                count++;
            }
        }

        return count;
    }

    private static boolean isCompletelyCovered(Terrain terrain, Chunk chunk, int i, int j, int k){
        boolean top = isSolid(terrain, chunk, i, j + 1, k);
        boolean north = isSolid(terrain, chunk, i, j, k-1);
        boolean south = isSolid(terrain, chunk, i, j, k+1);
        boolean east = isSolid(terrain, chunk, i-1, j, k);
        boolean west = isSolid(terrain, chunk, i+1, j, k);

        return top && north && south && east && west;
    }

    private static int countAdjacentCoveringBlocks(Terrain terrain, Chunk chunk, int i, int j, int k, int radius){
        int adjacentCoveringBlocks = 0;

        for(int x = -radius; x <= radius; x++){
            for(int y = -radius; y <= radius;y++){
                adjacentCoveringBlocks += isSolid(terrain, chunk, i + 1 + x, j + 1, k + y) ? 1 : 0;
                adjacentCoveringBlocks += isSolid(terrain, chunk, i - 1 + x, j + 1, k + y) ? 1 : 0;
                adjacentCoveringBlocks += isSolid(terrain, chunk, i + x, j + 1, k + y + 1) ? 1 : 0;
                adjacentCoveringBlocks += isSolid(terrain, chunk, i + x, j + 1, k + y - 1) ? 1 : 0;
            }
        }

        return adjacentCoveringBlocks;
    }

    private static boolean isSolid(Terrain terrain, Chunk chunk, int i, int worldY, int k){
        if(worldY < 0){//Under the world
            return true;
        }
        if(worldY >= Chunk.CHUNK_HEIGHT){
            return false;
        }
        int worldX = chunk.getChunkX() * Chunk.CHUNK_WIDTH + i;
        int worldZ = chunk.getChunkZ() * Chunk.CHUNK_WIDTH + k;

        if(i < 0 || k < 0 || i >= Chunk.CHUNK_WIDTH || k >= Chunk.CHUNK_WIDTH){//Outside this chunk
            return terrain.isSolid(worldX, worldY, worldZ);
        }

        return chunk.blocks[i][worldY][k] != 0;
    }

}
