package me.cube.engine.editor;

import me.cube.engine.file.VoxelFile;
import me.cube.engine.game.world.Terrain;

public class PasteStructureAction implements EditAction {

    private final int[][][] snapshot, structure;

    private final int width, height, length;

    private final int pasteX, pasteY, pasteZ;

    public PasteStructureAction(VoxelFile file, int pasteX, int pasteY, int pasteZ){
        structure = file.toVoxelColorArray();
        width = file.width();
        height = file.height();
        length = file.length();

        this.pasteX = pasteX;
        this.pasteY = pasteY;
        this.pasteZ = pasteZ;

        snapshot = new int[width][height][length];
    }

    @Override
    public void execute(Terrain terrain) {

        for(int i = 0; i < width;i++){
            for(int j = 0; j < height;j++){
                for(int k = 0; k <  length;k++){

                    snapshot[i][j][k] = terrain.getCube(pasteX + i, pasteY + j, pasteZ + k);

                    int cube = structure[i][j][k];

                    if(cube == 0){
                        continue;
                    }

                    terrain.setCube(pasteX + i, pasteY + j, pasteZ + k, cube);

                }
            }
        }
    }

    @Override
    public void undo(Terrain terrain) {
        for(int i = 0; i < width;i++){
            for(int j = 0; j < height;j++){
                for(int k = 0; k <  length;k++){
                    terrain.setCube(pasteX + i, pasteY + j, pasteZ + k, snapshot[i][j][k]);
                }
            }
        }
    }
}
