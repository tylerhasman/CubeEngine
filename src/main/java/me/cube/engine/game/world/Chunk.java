package me.cube.engine.game.world;

import me.cube.engine.Voxel;
import me.cube.engine.VoxelModel;
import me.cube.engine.game.CubeGame;
import sun.awt.WindowIDProvider;

import static me.cube.engine.game.world.World.WORLD_SCALE;

public class Chunk {

    public static final int CHUNK_WIDTH = 32;
    public static final int CHUNK_HEIGHT = 128;

    public final int[][][] blocks;
    protected final int chunkX, chunkZ;

    protected boolean requireMeshRefresh = false;

    private Voxel mesh;

    private final Terrain terrain;

    protected Chunk(Terrain terrain, int x, int z){
        this.terrain = terrain;
        blocks = new int[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
        this.chunkX = x;
        this.chunkZ = z;
        mesh = null;
    }

    public int firstEmptyBlockY(int x, int z){
        for(int i = 0; i < CHUNK_HEIGHT;i++){
            if(!isSolid(x, i, z)){
                return i;
            }
        }
        return -1;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public void render(){
        if(requireMeshRefresh){
            requireMeshRefresh = false;
            generateMesh();
        }
        if(mesh != null){
            mesh.position.set(chunkX * CHUNK_WIDTH * WORLD_SCALE, 0, chunkZ * CHUNK_WIDTH * WORLD_SCALE);
            mesh.origin.set(0, 0, 0);
            mesh.render();
        }
    }

    protected void generateMesh(){
        if(mesh != null && mesh.model != null){
            mesh.model.dispose();
        }
        VoxelModel model = new VoxelModel(terrain, this);
        mesh = new Voxel("Chunk "+chunkX+" "+chunkZ, model);
        mesh.scale.set(World.WORLD_SCALE);
    }

    protected void setBlock(int x, int y, int z, int color){
        if(x < 0 || y < 0 || z < 0 || x >= blocks.length || y >= blocks[0].length || z >= blocks[0][0].length){
            return;
        }
        blocks[x][y][z] = color;
    }

    protected void setBlockWorldCoords(int x, int y, int z, int color){
        //TODO: Implement
    }

    protected boolean isSolid(int x, int y, int z){
        if(x < 0 || y < 0 || z < 0 || x >= blocks.length || y >= blocks[0].length || z >= blocks[0][0].length){
            return false;
        }
        return blocks[x][y][z] != 0;
    }

}
