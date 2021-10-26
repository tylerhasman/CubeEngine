package me.cube.game.world.generator;

import me.cube.game.world.Chunk;
import me.cube.game.world.Terrain;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Random;
import java.util.Stack;

public class CloudPopulator extends StructureChunkPopulator {

    public CloudPopulator(long seed) {
        super(80, seed);
    }

    @Override
    protected int getSpawnHeight(Terrain terrain, int spawnX, int spawnZ, Random random) {
        return random.nextInt(10) + 160 - Chunk.CHUNK_HEIGHT / 2;
    }

    @Override
    public void generateStructure(GeneratedStructure structure, Random random, Biome biome) {

        int stumpRadius = random.nextInt(10) + 5;

        Stack<Vector3f> bushes = new Stack<>();
        Stack<Integer> ages = new Stack<>();

        bushes.push(new Vector3f(0, Chunk.CHUNK_HEIGHT / 2f, 0));
        ages.push(0);

        while(!bushes.isEmpty()){
            Vector3f position = bushes.pop();
            int age = ages.pop();

            createLeaves(structure, (int) (-stumpRadius + position.x), (int) (position.y - stumpRadius * 0.3f), (int) (-stumpRadius + position.z), (int) (stumpRadius + position.x), (int) (position.y + stumpRadius), (int) (stumpRadius + position.z), stumpRadius, 0xFF_FAFAFA);

            int chance = age;
            while(random.nextInt(1 + chance) == 0){

                float theta = (float) (random.nextFloat() * Math.PI * 2);

                float x = (float) (Math.cos(theta) * stumpRadius);
                float z = (float) (Math.sin(theta) * stumpRadius);
                float y = (random.nextFloat() - 0.5f) * 10;

                bushes.push(new Vector3f(position.x + x, position.y + y, position.z + z));
                ages.push(age + 1);
                chance++;
            }

        }

    }

    private static void createLeaves(GeneratedStructure structure, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, float radius, int color){
        for(int x = minX; x <= maxX;x++){
            for(int z = minZ; z <= maxZ;z++){
                for(int y = minY; y < maxY;y++){
                    float dst = new Vector3f(x,y,z).distance((maxX - minX) / 2f + minX, (maxY - minY) / 2f + minY, (maxZ - minZ) / 2f + minZ);

                    if(dst < radius){
                        structure.setCube(x, y, z, color);
                    }

                }
            }
        }
    }

}
