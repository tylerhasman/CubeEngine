package me.cube.engine.game.world.generator;

import me.cube.engine.file.Assets;
import me.cube.engine.file.VoxelFile;

import java.util.Random;

public class TreePopulator extends StructureChunkPopulator{

    private VoxelFile treeData;

    public TreePopulator(int cellSize, long seed) {
        super(cellSize, seed);
        treeData = Assets.loadVoxelData("tree.vox", false);
    }

    @Override
    public void generateStructure(GeneratedStructure structure, Random random) {
        int[][][] colors = treeData.toVoxelColorArray();
        for(int i = 0; i < treeData.width();i++){
            for(int j = 0; j < treeData.height();j++){
                for(int k = 0; k < treeData.length();k++){
                    structure.setCube(i, j, k, colors[i][j][k]);
                }
            }
        }
    }
}
