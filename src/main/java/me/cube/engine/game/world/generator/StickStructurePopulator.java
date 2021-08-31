package me.cube.engine.game.world.generator;

import java.util.Random;

public class StickStructurePopulator extends StructureChunkPopulator{
    public StickStructurePopulator(long seed) {
        super(1000, seed);
    }

    @Override
    public void generateStructure(GeneratedStructure structure, Random random) {

        int width = random.nextInt(50) + 5;
        int length = random.nextInt(50) + 5;

        int height = random.nextInt(15) + 5;

        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);

        int color = 0xFF000000;
        color |= (red << 16);
        color |= (green << 8);
        color |= blue;

        for(int i = 0; i < width;i++){
            for(int j = 0; j < height;j++){
                for(int k = 0; k < length;k++){

                    int x = i;
                    int y = j;
                    int z = k;

                    structure.setCube(x, y, z, color);

                }
            }
        }

    }
}
