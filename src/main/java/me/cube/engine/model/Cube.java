package me.cube.engine.model;

import me.cube.engine.util.FloatArray;
import org.joml.Vector3f;

public class Cube {

    public float red, green, blue;
    public int flags;

    public boolean top, bottom, south, north, west, east;

    public float x, y, z;

    public int[][][] neighbors = new int[3][3][3];

    public boolean isVisible(){
        return top || bottom || south || north || west || east;
    }

    private Vector3f calculateColor(int minX, int maxX, int minY, int maxY, int minZ, int maxZ){

        Vector3f outputColor = new Vector3f(red, green, blue);
        float legit = 1f;

        for(int i = minX;i <= maxX;i++){
            for(int k = minY; k <= maxY;k++){
                for(int j = minZ;j <= maxZ;j++){
                    int color = neighbors[i][k][j];

                    if(color != 0){
                        legit++;
                        if(k != 1){
                            outputColor.add(rgbToVector(color).mul(0.5f));
                        }else{
                            outputColor.add(rgbToVector(color));

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

        if(north){
            vertOut.add(x, y, z);
            vertOut.add(x + 1, y, z);
            vertOut.add(x + 1, y + 1, z);
            vertOut.add(x, y + 1, z);

            norOut.addRepeat(new float[] {0, 0, -1}, 4);

            //colorOut.addRepeat(new float[] {color.x, color.y, color.z, 1f}, 4);

            Vector3f color1 = calculateColor(0, 1, 0, 1, 0, 1);
            Vector3f color2 = calculateColor(1, 2, 0, 1, 0, 1);

            Vector3f color3 = calculateColor(0, 1, 1, 2, 0, 1);
            Vector3f color4 = calculateColor(1, 2, 1, 2, 0, 1);

            colorOut.add(color1);
            colorOut.add(1f);
            colorOut.add(color2);
            colorOut.add(1f);
            colorOut.add(color3);
            colorOut.add(1f);
            colorOut.add(color4);
            colorOut.add(1f);

        }

        if(south){
            vertOut.add(x, y, z + 1);
            vertOut.add(x, y + 1, z + 1);
            vertOut.add(x + 1, y + 1, z + 1);
            vertOut.add(x + 1, y, z + 1);

            norOut.addRepeat(new float[] {0, 0, 1}, 4);

            Vector3f color1 = calculateColor(0, 1, 0, 1, 1, 2);
            Vector3f color2 = calculateColor(1, 2, 1, 2, 1, 2);

            Vector3f color3 = calculateColor(0, 1, 1, 2, 1, 2);
            Vector3f color4 = calculateColor(1, 2, 0, 1, 1, 2);

            colorOut.add(color1);
            colorOut.add(1f);
            colorOut.add(color2);
            colorOut.add(1f);
            colorOut.add(color3);
            colorOut.add(1f);
            colorOut.add(color4);
            colorOut.add(1f);

        }

        if(top){
            vertOut.add(x, y + 1, z);
            vertOut.add(x + 1, y + 1, z);
            vertOut.add(x + 1, y + 1, z + 1);
            vertOut.add(x, y + 1, z + 1);

            norOut.addRepeat(new float[] {0, 1, 0}, 4);

            Vector3f color1 = calculateColor(0, 1, 1, 2, 0, 1);
            Vector3f color2 = calculateColor(1, 2, 1, 2, 0, 1);
            Vector3f color3 = calculateColor(1, 2,1, 2, 1, 2);
            Vector3f color4 = calculateColor(0, 1, 1, 2, 1, 2);


            colorOut.add(color1);
            colorOut.add(1f);
            colorOut.add(color2);
            colorOut.add(1f);
            colorOut.add(color3);
            colorOut.add(1f);
            colorOut.add(color4);
            colorOut.add(1f);

        }

        if(bottom){

            vertOut.add(x, y, z);
            vertOut.add(x, y, z + 1);
            vertOut.add(x + 1, y, z + 1);
            vertOut.add(x + 1, y, z);

            norOut.addRepeat(new float[] {0, -1, 0}, 4);
            colorOut.addRepeat(new float[] {color.x, color.y, color.z, 1f}, 4);

        }

        if(east){
            vertOut.add(x, y, z);
            vertOut.add(x, y + 1, z);
            vertOut.add(x, y + 1, z + 1);
            vertOut.add(x, y, z + 1);

            norOut.addRepeat(new float[] {1, 0, 0}, 4);

            Vector3f color1 = calculateColor(0, 1, 0, 1, 0, 1);
            Vector3f color2 = calculateColor(0, 1, 1, 2, 1, 2);

            Vector3f color3 = calculateColor(0, 1, 1, 2, 1, 2);
            Vector3f color4 = calculateColor(0, 1, 0, 1, 0, 1);

            colorOut.add(color1);
            colorOut.add(1f);
            colorOut.add(color2);
            colorOut.add(1f);
            colorOut.add(color3);
            colorOut.add(1f);
            colorOut.add(color4);
            colorOut.add(1f);

        }

        if(west){
            vertOut.add(x + 1, y, z);
            vertOut.add(x + 1, y, z + 1);
            vertOut.add(x + 1, y + 1, z + 1);
            vertOut.add(x + 1, y + 1, z);

            norOut.addRepeat(new float[] {-1, 0, 0}, 4);


            Vector3f color1 = calculateColor(1, 2, 0, 1, 0, 1);
            Vector3f color2 = calculateColor(1, 2, 0, 1, 1, 2);

            Vector3f color3 = calculateColor(1, 2, 1, 2, 1, 2);
            Vector3f color4 = calculateColor(1, 2, 1, 2, 0, 1);

            colorOut.add(color1);
            colorOut.add(1f);
            colorOut.add(color2);
            colorOut.add(1f);
            colorOut.add(color3);
            colorOut.add(1f);
            colorOut.add(color4);
            colorOut.add(1f);

        }
    }


}
