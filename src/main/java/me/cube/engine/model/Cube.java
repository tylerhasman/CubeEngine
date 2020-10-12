package me.cube.engine.model;

import me.cube.engine.util.FloatArray;

public class Cube {

    public static final int SHADE_SIDES = 1;

    public float red, green, blue;
    public int flags;

    public boolean top, bottom, south, north, west, east;

    public float x, y, z;

    public boolean isVisible(){
        return top || bottom || south || north || west || east;
    }

    public void generate(FloatArray vertOut, FloatArray norOut, FloatArray colorOut){

        if(north){
            vertOut.add(x, y, z);
            vertOut.add(x + 1, y, z);
            vertOut.add(x + 1, y + 1, z);
            vertOut.add(x, y + 1, z);

            norOut.addRepeat(new float[] {0, 0, -1}, 4);

            if((flags & SHADE_SIDES) == SHADE_SIDES){
                colorOut.addRepeat(new float[] {red * 0.8f, green * 0.8f, blue * 0.8f, 1f}, 2);
                colorOut.addRepeat(new float[] {red, green, blue, 1f}, 2);
            }else{
                colorOut.addRepeat(new float[] {red, green, blue, 1f}, 4);
            }

        }

        if(south){
            vertOut.add(x, y, z + 1);
            vertOut.add(x, y + 1, z + 1);
            vertOut.add(x + 1, y + 1, z + 1);
            vertOut.add(x + 1, y, z + 1);

            norOut.addRepeat(new float[] {0, 0, 1}, 4);

            if((flags & SHADE_SIDES) == SHADE_SIDES){
                colorOut.addRepeat(new float[] {red * 0.8f, green * 0.8f, blue * 0.8f, 1f}, 1);
                colorOut.addRepeat(new float[] {red, green, blue, 1f}, 2);
                colorOut.addRepeat(new float[] {red * 0.8f, green * 0.8f, blue * 0.8f, 1f}, 1);
            }else{
                colorOut.addRepeat(new float[] {red, green, blue, 1f}, 4);
            }

        }

        if(top){
            vertOut.add(x, y + 1, z);
            vertOut.add(x + 1, y + 1, z);
            vertOut.add(x + 1, y + 1, z + 1);
            vertOut.add(x, y + 1, z + 1);

            norOut.addRepeat(new float[] {0, 1, 0}, 4);

            colorOut.addRepeat(new float[] {red, green, blue, 1f}, 4);

        }

        if(bottom){

            vertOut.add(x, y, z);
            vertOut.add(x, y, z + 1);
            vertOut.add(x + 1, y, z + 1);
            vertOut.add(x + 1, y, z);

            norOut.addRepeat(new float[] {0, -1, 0}, 4);
            colorOut.addRepeat(new float[] {red, green, blue, 1f}, 4);

        }

        if(east){
            vertOut.add(x, y, z);
            vertOut.add(x, y + 1, z);
            vertOut.add(x, y + 1, z + 1);
            vertOut.add(x, y, z + 1);

            norOut.addRepeat(new float[] {1, 0, 0}, 4);


            if((flags & SHADE_SIDES) == SHADE_SIDES){
                colorOut.addRepeat(new float[] {red * 0.8f, green * 0.8f, blue * 0.8f, 1f}, 1);
                colorOut.addRepeat(new float[] {red, green, blue, 1f}, 2);
                colorOut.addRepeat(new float[] {red * 0.8f, green * 0.8f, blue * 0.8f, 1f}, 1);
            }else{
                colorOut.addRepeat(new float[] {red, green, blue, 1f}, 4);
            }
        }

        if(west){
            vertOut.add(x + 1, y, z);
            vertOut.add(x + 1, y, z + 1);
            vertOut.add(x + 1, y + 1, z + 1);
            vertOut.add(x + 1, y + 1, z);

            norOut.addRepeat(new float[] {-1, 0, 0}, 4);

            if((flags & SHADE_SIDES) == SHADE_SIDES){
                colorOut.addRepeat(new float[] {red * 0.8f, green * 0.8f, blue * 0.8f, 1f}, 2);
                colorOut.addRepeat(new float[] {red, green, blue, 1f}, 2);
            }else{
                colorOut.addRepeat(new float[] {red, green, blue, 1f}, 4);
            }
        }
    }

}
