package me.cube.engine.game.world.generator;

import java.util.Random;

/**
 * Test populator.
 * Places randomly sized rectangular boxes in mountain biome
 */
public class StickStructurePopulator extends StructureChunkPopulator{
    public StickStructurePopulator() {
        super(100, 4328);
    }

    @Override
    public void generateStructure(GeneratedStructure structure, Random random, Biome biome) {

        if(biome == Biome.MOUNTAINS){
            int width = 50;//random.nextInt(50) + 5;
            int length = 50;//random.nextInt(50) + 5;

            int height = random.nextInt(15) + 5;

            int red = random.nextInt(255);
            int green = random.nextInt(255);
            int blue = random.nextInt(255);

            int color = 0xFF000000;
            color |= (red << 16);
            color |= (green << 8);
            color |= blue;

            for(int i = -width/2; i <= width/2;i++){
                for(int j = 0; j < height;j++){
                    for(int k = -length/2; k <= length/2;k++){

                        int x = i;
                        int y = j;
                        int z = k;

                        structure.setCube(x, y, z, color);

                    }
                }
            }
        }



    }
}
