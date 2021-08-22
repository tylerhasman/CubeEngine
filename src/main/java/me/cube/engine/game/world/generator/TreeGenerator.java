package me.cube.engine.game.world.generator;

import me.cube.engine.game.world.Chunk;
import me.cube.engine.game.world.Terrain;
import org.joml.Vector3f;

import java.util.Random;

/**
 * Generates a random tree.
 *
 * The tree is generated internally using normalized numbers (0.0 to 1.0)
 * Then the tree is 'rasterized' to fit certain dimensions.
 *
 */
public class TreeGenerator implements ChunkPopulator {

    public int[][][] generateTree(long seed, int sideLength, int height){
        Random random = new Random(seed);

        Tree tree = new Tree();
        tree.trunkHeight = random.nextFloat();
        tree.trunkRadius = random.nextFloat();

        return tree.rasterize(sideLength, height);
    }

    @Override
    public void populateChunk(Terrain terrain, Chunk chunk) {

    }

    private class Tree {

        private float trunkRadius = 1;
        private float trunkHeight = 1;

        private Branch[] branches = new Branch[0];

        public int[][][] rasterize(int sideLength, int height){
            int[][][] blocks = new int[sideLength][height][sideLength];

            int rasterizedHeight = (int) (height * trunkHeight);
            int rasterizedSideLength = (int) trunkRadius * sideLength;

            Vector3f center = new Vector3f(sideLength / 2f, 0, sideLength / 2f);

            for(int x = 0; x < sideLength;x++){
                for(int y = 0; y < rasterizedHeight;y++){
                    for(int z = 0; z < sideLength;z++){

                        Vector3f blockPosition = new Vector3f(x, y, z);

                        if(blockPosition.distanceSquared(center) <= rasterizedSideLength){
                            blocks[x][y][z] = 0xFF000000;
                        }

                    }
                }
            }

            return blocks;
        }

    }

    private class Branch {
        private Branch[] branches;

        private Vector3f position;

        private float thickness;
    }

}
