package me.cube.engine.model;

import me.cube.engine.game.world.Chunk;
import me.cube.engine.game.world.Terrain;
import me.cube.engine.util.FloatArray;

import static me.cube.engine.game.world.Chunk.CHUNK_HEIGHT;
import static me.cube.engine.game.world.Chunk.CHUNK_WIDTH;
import static org.lwjgl.opengl.GL11C.GL_QUADS;

public class AsyncChunkMesh extends VoxelMesh {

    //Opaque mesh
    private FloatArray vertices, colors, normals;

    //Transparent mesh
    private FloatArray tVertices, tColors, tNormals;
    private boolean hasTransparency;

    public AsyncChunkMesh(Terrain terrain, Chunk chunk){
        super();
        vertices = new FloatArray(4096);
        colors = new FloatArray(4096);
        normals = new FloatArray(4096);

        tVertices = new FloatArray(4096);
        tColors = new FloatArray(4096);
        tNormals = new FloatArray(4096);
        hasTransparency = false;

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

    public VoxelMesh createTransparentMesh(){
        VoxelMesh mesh = new VoxelMesh();
        mesh.initialize(tVertices.toArray(), tColors.toArray(), tNormals.toArray());
        tVertices = null;
        tColors = null;
        tNormals = null;
        hasTransparency = false;
        return mesh;
    }

    public boolean hasTransparency(){
        return hasTransparency;
    }

    private void generate(Terrain terrain, Chunk chunk){

        long time = System.currentTimeMillis();

        Cube cube = new Cube();

        for(int i = 0; i < CHUNK_WIDTH;i++){
            for(int j = 0; j < CHUNK_HEIGHT;j++){
                for(int k = 0;k < CHUNK_WIDTH;k++){

                    int color = chunk.blocks[i][j][k];
                    byte flags = chunk.blockFlags[i][j][k];

                    if(color != 0){

                        cube.flags = 0;

                        cube.alpha = ((color >> 24) & 255) / 255f;

                        cube.red = ((color >> 16) & 255) / 255F;
                        cube.green = ((color >> 8) & 255) / 255F;
                        cube.blue = (color & 255) / 255F;

                        cube.neighbors = calculateNeighbors(terrain, chunk, i, j, k);

                        cube.x = i;
                        cube.y = j;
                        cube.z = k;

                        cube.top = !isOpaque(terrain, chunk, i, j + 1, k);
                        cube.bottom = !isOpaque(terrain, chunk, i, j - 1, k);
                        cube.north = !isOpaque(terrain, chunk, i, j, k-1);
                        cube.south = !isOpaque(terrain, chunk, i, j, k+1);
                        cube.east = !isOpaque(terrain, chunk, i-1, j, k);
                        cube.west = !isOpaque(terrain, chunk, i+1, j, k);

                        if(cube.alpha < 1f){
                            cube.top = !isSolid(terrain, chunk, i, j + 1, k);
                            cube.bottom = !isSolid(terrain, chunk, i, j - 1, k);
                            cube.north = !isSolid(terrain, chunk, i, j, k-1);
                            cube.south = !isSolid(terrain, chunk, i, j, k+1);
                            cube.east = !isSolid(terrain, chunk, i-1, j, k);
                            cube.west = !isSolid(terrain, chunk, i+1, j, k);
                        }

                        if(cube.isVisible()){
                            if(cube.alpha == 1) {
                                cube.generate(vertices, normals, colors);
                            }else{
                                cube.generate(tVertices, tNormals, tColors);
                                hasTransparency = true;
                            }
                        }

                    }

                }
            }
        }

        //I have absolutely no clue why but
        //If we try to render meshes that are small glDrawArrays segfaults
        //This is a mystery for another day :^)
        if(tVertices.count() < 256) {
            tVertices.addRepeat(0, 256);
            tColors.addRepeat(0, 256 / 3 * 4);
            tNormals.addRepeat(0, 256);
        }

/*        if(hasTransparency){
            System.out.println(tVertices.count()+" "+tNormals.count()+" "+tColors.count());
        }*/

        //System.out.println("Took "+(System.currentTimeMillis()-time)+"ms to generate chunk mesh "+chunk.getChunkX()+"/"+chunk.getChunkZ()+" "+(vertices.count() + tVertices.count())+" vertices");
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

    private static int[][][] calculateNeighbors(Terrain terrain, Chunk chunk, int x, int y, int z){
        int[][][] neighbors = new int[3][3][3];

        for(int i = 0; i < 3;i++){

            int xOffset = i - 1;

            for(int k = 0; k < 3;k++){

                int yOffset = k - 1;

                for(int j = 0;j < 3;j++){
                    int zOffset = j - 1;

                    neighbors[i][k][j] = colorOf(terrain, chunk, x + xOffset, y + yOffset, z + zOffset);
                }
            }

        }

        return neighbors;
    }

/*    private static Vector3f calculateColor(Terrain terrain, Chunk chunk, int x, int y, int z){
        Vector3f outputColor = new Vector3f();

        int[] neighbors = new int[5];

        neighbors[0] = colorOf(terrain, chunk, x + 1, y, z);
        neighbors[1] = colorOf(terrain, chunk, x - 1, y, z);
        neighbors[2] = colorOf(terrain, chunk, x, y, z + 1);
        neighbors[3] = colorOf(terrain, chunk, x, y, z - 1);

        float valid = 1;

        outputColor.add(rgbToVector(colorOf(terrain, chunk, x, y, z)));//Self

        for(int i = 0; i < neighbors.length;i++){

            if(neighbors[i] != 0){
                valid++;
                outputColor.add(rgbToVector(neighbors[i]));
            }

        }

        return outputColor.mul(1f / valid);
    }*/



    private static int countAdjacentCoveringBlocks(Terrain terrain, Chunk chunk, int i, int j, int k, int radius){
        int adjacentCoveringBlocks = 0;

        int count = 0;

        for(int x = -radius; x <= radius; x++){
            for(int y = -radius; y <= radius;y++){
                adjacentCoveringBlocks += isSolid(terrain, chunk, i + 1 + x, j + 1, k + y) ? 1 : 0;
                adjacentCoveringBlocks += isSolid(terrain, chunk, i - 1 + x, j + 1, k + y) ? 1 : 0;
                adjacentCoveringBlocks += isSolid(terrain, chunk, i + x, j + 1, k + y + 1) ? 1 : 0;
                adjacentCoveringBlocks += isSolid(terrain, chunk, i + x, j + 1, k + y - 1) ? 1 : 0;

                count += 4;
            }
        }


        return adjacentCoveringBlocks;
    }

    private static int colorOf(Terrain terrain, Chunk chunk, int chunkX, int worldY, int chunkZ){
        if(worldY < 0){//Under the world
            return 0;
        }
        if(worldY >= CHUNK_HEIGHT){
            return 0;
        }
        int worldX = chunk.getChunkX() * CHUNK_WIDTH + chunkX;
        int worldZ = chunk.getChunkZ() * CHUNK_WIDTH + chunkZ;

        if(chunkX < 0 || chunkZ < 0 || chunkX >= CHUNK_WIDTH || chunkZ >= CHUNK_WIDTH){//Outside this chunk
            return terrain.getCube(worldX, worldY, worldZ);
        }

        return chunk.blocks[chunkX][worldY][chunkZ];
    }

    private static boolean isOpaque(Terrain terrain, Chunk chunk, int i, int worldY, int k){
        if(worldY < 0){//Under the world
            return true;
        }
        if(worldY >= CHUNK_HEIGHT){
            return false;
        }
        int worldX = chunk.getChunkX() * CHUNK_WIDTH + i;
        int worldZ = chunk.getChunkZ() * CHUNK_WIDTH + k;

        int color;

        if(i < 0 || k < 0 || i >= CHUNK_WIDTH || k >= CHUNK_WIDTH){//Outside this chunk
            color = terrain.getCube(worldX, worldY, worldZ);
        }else{
            color = chunk.blocks[i][worldY][k];
        }

        int alpha = (color >> 24) & 0xFF;

        return alpha == 255;
    }

    private static boolean isSolid(Terrain terrain, Chunk chunk, int i, int worldY, int k){
        if(worldY < 0){//Under the world
            return true;
        }
        if(worldY >= CHUNK_HEIGHT){
            return false;
        }
        int worldX = chunk.getChunkX() * CHUNK_WIDTH + i;
        int worldZ = chunk.getChunkZ() * CHUNK_WIDTH + k;

        if(i < 0 || k < 0 || i >= CHUNK_WIDTH || k >= CHUNK_WIDTH){//Outside this chunk
/*            if(!terrain.isLoaded(worldX, worldZ)){
                return true;
            }*/
            return terrain.isSolid(worldX, worldY, worldZ);
        }

        return chunk.blocks[i][worldY][k] != 0;
    }

}
