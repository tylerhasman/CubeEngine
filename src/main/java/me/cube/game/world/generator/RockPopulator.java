package me.cube.game.world.generator;

import me.cube.game.world.Terrain;

import java.util.Random;
import java.util.Stack;

public class RockPopulator extends StructureChunkPopulator {

    private static final int DARK_COLOR = 0xFF_7E7B87;
    private static final int MID_COLOR = 0xFF_B4B1C2;
    private static final int LIGHT_COLOR = 0xFF_C4C0D3;

    private final int rockSize;
    private final int spawnProbability;

    public RockPopulator(long seed, int cellSize, int rockSize, int spawnProbability) {
        super(cellSize, seed);
        if(rockSize < 1){
            throw new IllegalArgumentException("rockSize must be >= 1");
        }
        this.rockSize = rockSize;
        this.spawnProbability = spawnProbability;
    }

    @Override
    protected int getSpawnHeight(Terrain terrain, int spawnX, int spawnZ, Random random) {
        return super.getSpawnHeight(terrain, spawnX, spawnZ, random) - 5;
    }

    @Override
    public void generateStructure(GeneratedStructure structure, Random random, Biome biome) {

        if(biome != Biome.MOUNTAINS)
            return;

        if(random.nextInt(spawnProbability) > 0){
            return;
        }

        Stack<RockData> data = new Stack<>();

        int sWidth = random.nextInt(3) + 5;
        int sHeight = random.nextInt(2) + 3;
        int sLength = random.nextInt(3) + 4;

        data.push(new RockData( - sWidth / 2, 0, -sLength / 2, sWidth, sHeight, sLength, DARK_COLOR, 0));

        while (!data.isEmpty()){

            RockData rock = data.pop();

            placeRock(structure, rock);

            int count = 0;
            while(random.nextInt(1 + rock.age + count) < rockSize){

                int width = random.nextInt(3) + 2;
                int height = random.nextInt(3) + 2;
                int length = random.nextInt(3) + 2;

                int offsetX = random.nextInt(rock.width) - rock.width / 2;
                int offsetY = random.nextInt(rock.width) - rock.width / 2;
                int offsetZ = random.nextInt(rock.width) - rock.width / 2;

                int age = rock.age + 1;

                int color = (age < 3 ? DARK_COLOR : (age < 5 ? MID_COLOR : LIGHT_COLOR));

                RockData rockData = new RockData(rock.x + offsetX, rock.y + offsetY, rock.z + offsetZ, width, height, length, color, age);

                data.push(rockData);

                count++;
            }

        }

    }

    private static void placeRock(GeneratedStructure structure, RockData data){
        for(int i = data.x; i < data.x + data.width;i++){
            for(int j = data.y; j < data.y + data.height;j++){
                for(int k = data.z; k < data.z + data.length;k++){

                    structure.setCube(i, j, k, data.color);

                }
            }
        }
    }

    private static class RockData {
        public final int x, y, z;
        public final int width, height, length;
        public final int color;
        public final int age;

        private RockData(int x, int y, int z, int width, int height, int length, int color, int age) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.width = width;
            this.height = height;
            this.length = length;
            this.color = color;
            this.age = age;
        }
    }

}
