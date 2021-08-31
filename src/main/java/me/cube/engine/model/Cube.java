package me.cube.engine.model;

import me.cube.engine.util.FloatArray;
import org.joml.Vector3f;

public class Cube {

    public static final int FLAG_NO_COLOR_BLEED = 1;

    public float scale = 1f;

    public float red, green, blue, alpha;
    public int flags;

    public boolean top, bottom, south, north, west, east;

    public float x, y, z;

    public BlockGetter adjacentBlockGetFunction = (x, y, z) -> 0;

    //public int[][][] neighbors = new int[3][3][3];

    public boolean isVisible(){
        return top || bottom || south || north || west || east;
    }

    private int getNeighbor(int x, int y, int z){

        return adjacentBlockGetFunction.colorOf((int) this.x + x, (int) this.y + y, (int) this.z + z);
    }

    private int getNeighborOLD(int x, int y, int z, int nX, int nY, int nZ){
        int aX = x + nX - 1;
        int aY = y + nY - 1;
        int aZ = z + nZ - 1;

        return adjacentBlockGetFunction.colorOf(aX, aY, aZ);
    }

    //TODO: This method is really fucked
    //It is supposed to calculate the color each vertex of the cube should be but it gets fucked when there are underground blocks
    //And for horizontal sides of the cubes it can get really fucky
    private Vector3f calculateColor(int minX, int maxX, int minY, int maxY, int minZ, int maxZ){

        if(alpha < 1 || (flags & FLAG_NO_COLOR_BLEED) != 0){
            return new Vector3f(red, green, blue);
        }

        float legit = (maxX - minX) + (maxY - minY) + (maxZ - minZ);
        Vector3f outputColor = new Vector3f(red, green, blue).mul(legit);

        int iX = (int) x;
        int iY = (int) y;
        int iZ = (int) z;

        for(int i = minX;i <= maxX;i++){
            for(int k = minY; k <= maxY;k++){
                for(int j = minZ;j <= maxZ;j++){
                    int color = getNeighborOLD(iX, iY, iZ, i, k, j);

                    if(i == 1 && j == 1 && k == 1)
                        continue;

                    if(color != 0 && (((color >> 24)) & 0xFF) == 0xFF){
                        legit++;
                        if(k == 1){
                            if((flags & FLAG_NO_COLOR_BLEED) == 0){
                                outputColor.add(rgbToVector(color));
                            }else{
                                outputColor.add(new Vector3f(0.1f));
                            }
                        }else{
                            outputColor.add(new Vector3f(0.1f));
                        }
                    }

                }
            }
        }

        return outputColor.mul(1f / legit);
    }

    private static Vector3f rgbToVector(int rgb){
        Vector3f color = new Vector3f();
        color.x = ((rgb >> 16) & 255) / 255F;
        color.y = ((rgb >> 8) & 255) / 255F;
        color.z = (rgb & 255) / 255F;
        return color;
    }

    public void generate(FloatArray vertOut, FloatArray norOut, FloatArray colorOut){

        Vector3f color = new Vector3f(red, green, blue);

        float size = 1f * scale;

        float x = this.x * scale;
        float y = this.y * scale;
        float z = this.z * scale;

        if(north){
            vertOut.add(x, y, z);
            vertOut.add(x + size, y, z);
            vertOut.add(x + size, y + size, z);
            vertOut.add(x, y + size, z);

            norOut.addRepeat(new float[] {0, 0, -1}, 4);

            //colorOut.addRepeat(new float[] {color.x, color.y, color.z, 1f}, 4);

            Vector3f color1 = calculateColor(0, 1, 0, 1, 0, 1);
            Vector3f color2 = calculateColor(1, 2, 0, 1, 0, 1);

            Vector3f color3 = calculateColor(0, 1, 1, 2, 0, 1);
            Vector3f color4 = calculateColor(1, 2, 1, 2, 0, 1);

            if((flags & FLAG_NO_COLOR_BLEED) != 0){

                Vector3f topColor3 = calculateColor(0, 1, 1, 2, 0, 1);
                Vector3f topColor4 = calculateColor(1, 2, 1, 2, 0, 1);

                color1.set(red, green, blue);
                color2.set(red, green, blue);

                color3.set(red, green, blue).lerp(topColor3, 1);
                color4.set(red, green, blue).lerp(topColor4, 1);
            }

            colorOut.add(color1.mul(0.8f));
            colorOut.add(alpha);
            colorOut.add(color2.mul(0.8f));
            colorOut.add(alpha);
            colorOut.add(color3.mul(0.8f));
            colorOut.add(alpha);
            colorOut.add(color4.mul(0.8f));
            colorOut.add(alpha);

        }

        if(south){
            vertOut.add(x, y, z + size);
            vertOut.add(x, y + size, z + size);
            vertOut.add(x + size, y + size, z + size);
            vertOut.add(x + size, y, z + size);

            norOut.addRepeat(new float[] {0, 0, 1}, 4);

            Vector3f color1 = calculateColor(0, 1, 0, 1, 1, 2);
            Vector3f color2 = calculateColor(1, 2, 1, 2, 1, 2);

            Vector3f color3 = calculateColor(0, 1, 1, 2, 1, 2);
            Vector3f color4 = calculateColor(1, 2, 0, 1, 1, 2);

            colorOut.add(color1.mul(0.8f));
            colorOut.add(alpha);
            colorOut.add(color2.mul(0.8f));
            colorOut.add(alpha);
            colorOut.add(color3.mul(0.8f));
            colorOut.add(alpha);
            colorOut.add(color4.mul(0.8f));
            colorOut.add(alpha);

        }

        if(top){
            vertOut.add(x, y + size, z);
            vertOut.add(x + size, y + size, z);
            vertOut.add(x + size, y + size, z + size);
            vertOut.add(x, y + size, z + size);

            norOut.addRepeat(new float[] {0, 1, 0}, 4);

            Vector3f color1 = calculateColor(0, 1, 1, 2, 0, 1);
            Vector3f color2 = calculateColor(1, 2, 1, 2, 0, 1);
            Vector3f color3 = calculateColor(1, 2,1, 2, 1, 2);
            Vector3f color4 = calculateColor(0, 1, 1, 2, 1, 2);

            colorOut.add(color1);
            colorOut.add(alpha);
            colorOut.add(color2);
            colorOut.add(alpha);
            colorOut.add(color3);
            colorOut.add(alpha);
            colorOut.add(color4);
            colorOut.add(alpha);

        }

        if(bottom){

            vertOut.add(x, y, z);
            vertOut.add(x, y, z + size);
            vertOut.add(x + size, y, z + size);
            vertOut.add(x + size, y, z);

            norOut.addRepeat(new float[] {0, -1, 0}, 4);
            colorOut.addRepeat(new float[] {color.x * 0.8f, color.y * 0.8f, color.z * 0.8f, alpha}, 4);

        }

        if(east){
            vertOut.add(x, y, z);
            vertOut.add(x, y + size, z);
            vertOut.add(x, y + size, z + size);
            vertOut.add(x, y, z + size);

            norOut.addRepeat(new float[] {-1, 0, 0}, 4);

            Vector3f color1 = calculateColor(0, 1, 0, 1, 0, 1);
            Vector3f color2 = calculateColor(0, 1, 1, 2, 1, 2);

            Vector3f color3 = calculateColor(0, 1, 1, 2, 1, 2);
            Vector3f color4 = calculateColor(0, 1, 0, 1, 0, 1);

            colorOut.add(color1.mul(0.8f));
            colorOut.add(alpha);
            colorOut.add(color2.mul(0.8f));
            colorOut.add(alpha);
            colorOut.add(color3.mul(0.8f));
            colorOut.add(alpha);
            colorOut.add(color4.mul(0.8f));
            colorOut.add(alpha);

        }

        if(west){
            vertOut.add(x + size, y, z);
            vertOut.add(x + size, y, z + size);
            vertOut.add(x + size, y + size, z + size);
            vertOut.add(x + size, y + size, z);

            norOut.addRepeat(new float[] {1, 0, 0}, 4);


            Vector3f color1 = calculateColor(1, 2, 0, 1, 0, 1);
            Vector3f color2 = calculateColor(1, 2, 0, 1, 1, 2);

            Vector3f color3 = calculateColor(1, 2, 1, 2, 1, 2);
            Vector3f color4 = calculateColor(1, 2, 1, 2, 0, 1);

            colorOut.add(color1.mul(0.8f));
            colorOut.add(alpha);
            colorOut.add(color2.mul(0.8f));
            colorOut.add(alpha);
            colorOut.add(color3.mul(0.8f));
            colorOut.add(alpha);
            colorOut.add(color4.mul(0.8f));
            colorOut.add(alpha);

        }
    }

    public static interface BlockGetter {
        int colorOf(int x, int y, int z);
    }

}
