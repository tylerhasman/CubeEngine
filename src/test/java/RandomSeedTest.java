import org.junit.Test;

import java.util.Random;

public class RandomSeedTest {

    private Random getRandom(int cellX, int cellZ){
/*
        if(cellX < 0){
            cellX = Math.abs(cellX) + 100;
        }

        if(cellZ < 0){
            cellZ = Math.abs(cellZ) + 432;
        }

        long biomeSeed = cellX;

        biomeSeed = ((biomeSeed << 32) | cellZ);

        Random random = new Random(biomeSeed);

        return random;*/


        long one = Math.abs(cellX) & 0x8FFFFFFF;
        long two = Math.abs(cellZ) & 0x8FFFFFFF;
        long seed = ((one + two) * (one + two + 1)) / 2 + two;

        return new Random(seed);
    }

    @Test
    public void testRandomness(){

        Random random = getRandom(-7, -5);

        System.out.println(random.nextInt(50)+" "+random.nextInt(50));

    }

}
