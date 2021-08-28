import me.cube.engine.game.world.BiomeMap;
import me.cube.engine.game.world.Chunk;
import me.cube.engine.game.world.generator.Biome;
import org.joml.Vector2f;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import java.awt.image.BufferedImage;

public class BiomeMapTest {

    @Test
    public void biomeMapTest() throws IOException {

        BiomeMap map = new BiomeMap(0x43783);

        BufferedImage bufferedImage = new BufferedImage(2048, 2048, BufferedImage.TYPE_INT_RGB);

        Map<Biome, Color> colors = new HashMap<>();

        colors.put(Biome.PLAINS, Color.GREEN);
        colors.put(Biome.ULTRA_PLAINS, Color.yellow);
        colors.put(Biome.MOUNTAINS, Color.gray);
        colors.put(Biome.RIVER, Color.blue);
        colors.put(Biome.LAKE, Color.cyan);

        Graphics graphics = bufferedImage.getGraphics();

        for(int i = 0; i < bufferedImage.getWidth();i++){
            for(int j = 0; j < bufferedImage.getHeight();j++){

               // Map<Float, Biome> distances = map.calculateBiomeDistances(i, j);
                Map<Biome, Float> weights = map.calculateBiomeWeights(i, j);

                float r = 0, g = 0, b = 0;
                float sumOfWeights = 0f;

                for(Biome biome : weights.keySet()){
                    float weight = weights.get(biome);
                    r += colors.get(biome).getRed() * weight;
                    g += colors.get(biome).getGreen() * weight;
                    b += colors.get(biome).getBlue() * weight;
                    sumOfWeights += weight;
                }

                r /= sumOfWeights;
                g /= sumOfWeights;
                b /= sumOfWeights;

                bufferedImage.setRGB(i, j, new Color((int)r, (int)g, (int)b).getRGB());



/*
                List<Float> keys = new ArrayList<>(distances.keySet());
                keys.sort(Float::compare);

                bufferedImage.setRGB(i, j, colors.get(distances.get(keys.get(0))).getRGB());
*/

            }
        }

        for(int i = 0; i < bufferedImage.getWidth();i += BiomeMap.BIOME_CELL_SIZE * Chunk.CHUNK_WIDTH){
            for(int j = 0; j < bufferedImage.getHeight();j += BiomeMap.BIOME_CELL_SIZE * Chunk.CHUNK_WIDTH){
                Vector2f biomeCenter = map.findBiomeCenter(i, j);

                graphics.setColor(Color.BLACK);
                graphics.fillOval((int) biomeCenter.x - 8, (int) biomeCenter.y - 8, 16, 16);
            }
        }

        ImageIO.write(bufferedImage, "PNG", new File("biomeTest.png"));

    }

}
