package me.cube.engine.game.world.generator;

import me.cube.engine.util.MathUtil;
import org.joml.Vector2f;

import java.util.Random;

public class ForestTreePopulator extends StructureChunkPopulator {

    private static final int BARK_COLOR = 0xFF_817b69;

    public ForestTreePopulator() {
        super(40);
    }

    @Override
    public void generateStructure(GeneratedStructure structure, Random random, Biome biome) {

        if(biome != Biome.PLAINS)
            return;

        int treeHeight = random.nextInt(12) + 6;
        int stumpRadius = random.nextInt(8) + 12;

        for(int x = -stumpRadius; x <= stumpRadius;x++){
            for(int z = -stumpRadius; z <= stumpRadius;z++){

                float dst = new Vector2f(x, z).distance(0, 0);

                for(int y = 0; y < treeHeight;y++){
                    if(dst < Math.sqrt(stumpRadius - Math.log(y * 18 + 1))){
                        structure.setCube(x + stumpRadius, y, z + stumpRadius, BARK_COLOR);
                    }
                }
            }
        }




    }
}
