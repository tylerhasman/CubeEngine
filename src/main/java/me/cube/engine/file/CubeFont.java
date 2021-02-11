package me.cube.engine.file;

import me.cube.engine.Voxel;
import me.cube.engine.model.SimpleVoxelMesh;
import me.cube.engine.model.VoxelMesh;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Font that uses 3d glyphs! Magic :D
 */
public class CubeFont {

    private Map<Character, VoxelMesh> glyphMap;
    private Map<Character, GlyphData> glyphData;

    public CubeFont(String fontFile, String glyphs) throws IOException, FontFormatException {
        glyphMap = new HashMap<>();
        glyphData = new HashMap<>();
        load(fontFile, glyphs);
    }

    public Voxel generate(String text){
        Voxel parent = new Voxel("generatedText");

        float width = 0;

        Voxel[] glyphs = new Voxel[text.length()];

        for(int i = 0; i < text.length();i++){
            if(!glyphMap.containsKey(text.charAt(i))){
                continue;
            }
            Voxel glyph = new Voxel("glyph-"+i, glyphMap.get(text.charAt(i)));

            glyph.position.x = width;

            if(i < text.length()-1)
                width += glyphData.get(text.charAt(i)).width;

            parent.addChild(glyph);

            glyphs[i] = glyph;
        }

        for(int i = 0; i < glyphs.length;i++){
            if(glyphs[i] != null){
                glyphs[i].position.x -= width / 2f;
            }
        }

        return parent;
    }

    private void load(String fontFile, String glyphs) throws IOException, FontFormatException {

        File file = new File(fontFile);

        System.out.println("Loading font "+file.getAbsolutePath());

        Font font = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(16f);



        FontRenderContext frc = new FontRenderContext(new AffineTransform(), false, false);

        for(int i = 0; i < glyphs.length();i++){

            char glyph = glyphs.charAt(i);

            Rectangle2D bounds = font.getStringBounds(new char[] {glyph}, 0, 1, frc);

            BufferedImage image = new BufferedImage((int) bounds.getWidth(), (int) bounds.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();

            g.setFont(font);

            g.setColor(new Color(1f, 1f, 1f, 1f));
            g.fillRect(0, 0, image.getWidth(), image.getHeight());

            g.setColor(new Color(0f, 0f, 0f, 1f));
            g.drawString(new String(new char[] {glyph}), 0, image.getHeight());

            LineMetrics lineMetrics = font.getLineMetrics(new char[] {glyph}, 0, 1, frc);


            glyphData.put(glyph, new GlyphData(g.getFontMetrics().charWidth(glyph), lineMetrics.getHeight()));

            glyphMap.put(glyph, extractGlyph(image, image.getWidth(), image.getHeight()));
        }

    }

    private VoxelMesh extractGlyph(BufferedImage image, int width, int height){
        int[][][] cubes = new int[width][height][1];

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){

                int color = image.getRGB(i, height - j - 1);

                if(color == Color.WHITE.getRGB()){
                    continue;
                }

                cubes[i][j][0] = color;

            }
        }



        return new SimpleVoxelMesh(cubes, width, height, 1, true);
    }

    public void dispose() {
        for(VoxelMesh mesh : glyphMap.values()){
            mesh.dispose();
        }
    }

    static class GlyphData {
        final float width, height;

        GlyphData(float width, float height) {
            this.width = width;
            this.height = height;
        }
    }
}
