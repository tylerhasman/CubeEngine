package me.cube.engine;

import me.cube.engine.game.world.Chunk;
import me.cube.engine.game.world.Terrain;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL30C.*;

/**
 * This code is fucked.
 */
public class VoxelModel {

    private static final int GRADIENT_SIDES = 1, DARKER_NORTH = 2, DARKER_SOUTH = 4, DARKER_WEST = 8, DARKER_EAST = 16;

    public final int width, height, length;

    public final Vector3f pivot = new Vector3f();

    private final int vertexHandle, colorHandle, normalHandle;

    private final int indices;

    public VoxelModel(int[][][] cubes, int width, int height, int length){
        this(cubes, width, height, length, true);
    }

    public VoxelModel(Terrain terrain, Chunk chunk){
        width = Chunk.CHUNK_WIDTH;
        height = Chunk.CHUNK_HEIGHT;
        length = Chunk.CHUNK_WIDTH;

        pivot.set(0, 0, 0);

        List<Float> vertices = new ArrayList<>();
        List<Float> colors = new ArrayList<>();
        List<Float> normals = new ArrayList<>();

        indices = generateVertices(terrain, chunk, vertices, colors, normals);

        float[] vertexBufferData = toArray(vertices);
        float[] colorBufferData = toArray(colors);
        float[] normalBufferData = toArray(normals);

        vertices.clear();
        colors.clear();
        normals.clear();

        vertexHandle = glGenBuffers();
        colorHandle = glGenBuffers();
        normalHandle = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexBufferData, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, colorHandle);
        glBufferData(GL_ARRAY_BUFFER, colorBufferData, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, normalHandle);
        glBufferData(GL_ARRAY_BUFFER, normalBufferData, GL_STATIC_DRAW);
    }

    public VoxelModel(int[][][] cubes, int width, int height, int length, boolean center){
        this.width = width;
        this.height = height;
        this.length = length;
        if(!center){
            pivot.set(width / 2f, height / 2f, length / 2f);
        }

        List<Float> vertices = new ArrayList<>();
        List<Float> colors = new ArrayList<>();
        List<Float> normals = new ArrayList<>();

        indices = generateVertices(cubes, center, vertices, colors, normals);

        float[] vertexBufferData = toArray(vertices);
        float[] colorBufferData = toArray(colors);
        float[] normalBufferData = toArray(normals);

        vertices.clear();
        colors.clear();
        normals.clear();

        vertexHandle = glGenBuffers();
        colorHandle = glGenBuffers();
        normalHandle = glGenBuffers();


        glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexBufferData, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, colorHandle);
        glBufferData(GL_ARRAY_BUFFER, colorBufferData, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, normalHandle);
        glBufferData(GL_ARRAY_BUFFER, normalBufferData, GL_STATIC_DRAW);

    }

    public void render(){

        //VERTEX
        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);


        //COLOR
        glEnableClientState(GL_COLOR_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, colorHandle);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);

        //NORMAL
        glEnableClientState(GL_NORMAL_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, normalHandle);

        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        //DRAW CALL
        GL11.glDrawArrays(GL11.GL_QUADS, 0, indices);

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);
        glDisableClientState(GL_NORMAL_ARRAY);
    }

    public void dispose(){
        glDeleteBuffers(new int[] {vertexHandle, colorHandle, normalHandle});
    }

    private int generateVertices(Terrain terrain, Chunk chunk, List<Float> vertices, List<Float> colors, List<Float> normals){
        int indices = 0;

        for(int i = 0; i < width;i++){
            for(int k = 0; k < length;k++){
                boolean usedGradient = false;
                for(int j = 0; j < height;j++){

                    int color = chunk.blocks[i][j][k];

                    if(color != 0){

                        float red = ((color >> 16) & 255) / 255F;
                        float green = ((color >> 8) & 255) / 255F;
                        float blue = (color & 255) / 255F;

                        boolean top = !isSolid(terrain, chunk, i, j + 1, k);
                        boolean bottom = !isSolid(terrain, chunk, i, j - 1, k);
                        boolean north = !isSolid(terrain, chunk, i, j, k-1);
                        boolean south = !isSolid(terrain, chunk, i, j, k+1);
                        boolean east = !isSolid(terrain, chunk, i-1, j, k);
                        boolean west = !isSolid(terrain, chunk, i+1, j, k);

                        boolean colorGradient = false;

                        if(!usedGradient && (north || south || east || west)){
                            colorGradient = true;
                            usedGradient = true;
                        }

                        int colorFlags = 0;

                        colorFlags |= colorGradient ? GRADIENT_SIDES : 0;
/*

                        for(int x = -1; x <= 1; x++){
                            for(int y = -1; y <= 1;y++){
                                colorFlags |= isSolid(terrain, chunk, i + 1 + x, j + 1, k + y) ? DARKER_NORTH : 0;
                                colorFlags |= isSolid(terrain, chunk, i - 1 + x, j + 1, k + y) ? DARKER_SOUTH : 0;
                                colorFlags |= isSolid(terrain, chunk, i + x, j + 1, k + y + 1) ? DARKER_EAST : 0;
                                colorFlags |= isSolid(terrain, chunk, i + x, j + 1, k + y - 1) ? DARKER_WEST : 0;
                            }
                        }
*/

                        int verts = generateCube(vertices, normals, colors, red, green, blue, i, j, k, colorFlags, top, bottom, north, south, east, west);

                        indices += verts;

                    }else{
                        usedGradient = false;
                    }

                }
            }
        }

        return indices;
    }

    private int generateVertices(int[][][] cubes, boolean center, List<Float> vertices, List<Float> colors, List<Float> normals){

        int indices = 0;

        for(int i = 0; i < width;i++){
            for(int k = 0; k < length;k++){
                boolean usedGradient = false;
                for(int j = 0; j < height;j++){
                    int color = cubes[i][j][k];

                    if(color != 0){

                        float red = ((color >> 16) & 255) / 255F;
                        float green = ((color >> 8) & 255) / 255F;
                        float blue = (color & 255) / 255F;

                        float x = i;
                        float y = j;
                        float z = k;

                        if(center){
                            x -= width / 2f;
                            y -= height / 2f;
                            z -= length / 2f;
                        }

                        boolean top = getOrZero(cubes, width, height, length, i, j + 1, k) == 0;
                        boolean bottom = getOrZero(cubes, width, height, length, i, j - 1, k) == 0;
                        boolean north = getOrZero(cubes, width, height, length, i, j, k-1) == 0;
                        boolean south = getOrZero(cubes, width, height, length, i, j, k+1) == 0;
                        boolean east = getOrZero(cubes, width, height, length, i-1, j, k) == 0;
                        boolean west = getOrZero(cubes, width, height, length, i+1, j, k) == 0;

                        boolean colorGradient = false;

                        if(!usedGradient){
                            colorGradient = true;
                            usedGradient = true;
                        }


                        int verts = generateCube(vertices, normals, colors, red, green, blue, x, y, z, colorGradient ? GRADIENT_SIDES : 0, top, bottom, north, south, east, west);

                        indices += verts;

                    }else{
                        usedGradient = false;
                    }
                }
            }
        }

        return indices;
    }

    private static boolean isSolid(Terrain terrain, Chunk chunk, int i, int worldY, int k){
        if(worldY < 0){//Under the world
            return true;
        }
        if(worldY >= Chunk.CHUNK_HEIGHT){
            return false;
        }
        int worldX = chunk.getChunkX() * Chunk.CHUNK_WIDTH + i;
        int worldZ = chunk.getChunkZ() * Chunk.CHUNK_WIDTH + k;

        if(i < 0 || k < 0 || i >= Chunk.CHUNK_WIDTH || k >= Chunk.CHUNK_WIDTH){//Outside this chunk
            return terrain.isSolid(worldX, worldY, worldZ);
        }

        return chunk.blocks[i][worldY][k] != 0;
    }

    private static int getOrZero(int[][][] cube, int w, int h, int l, int x, int y, int z){
        if(x < 0 || y < 0 || z < 0 || x >= w || y >= h || z >= l){
            return 0;
        }
        return cube[x][y][z];
    }

    private static int generateCube(List<Float> vertOut, List<Float> norOut, List<Float> colorOut, float red, float green, float blue, float x, float y, float z, int colorGradient, boolean top, boolean bottom, boolean north, boolean south, boolean east, boolean west){
        int start = vertOut.size();

        if(north){
            vertOut.add(x);
            vertOut.add(y);
            vertOut.add(z);

            vertOut.add(x + 1f);
            vertOut.add(y);
            vertOut.add(z);

            vertOut.add(x + 1f);
            vertOut.add(y + 1f);
            vertOut.add(z);

            vertOut.add(x);
            vertOut.add(y + 1f);
            vertOut.add(z);

            for(int i = 0; i < 4;i++){
                norOut.add(0f);
                norOut.add(0f);
                norOut.add(-1f);
            }

            if((colorGradient & GRADIENT_SIDES) != 0){
                colorGradient(colorOut, red, green, blue, 0, 1);
            }else{
                for(int i = 0; i < 4;i++){
                    colorOut.add(red);
                    colorOut.add(green);
                    colorOut.add(blue);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }
            }
        }

        if(south){
            vertOut.add(x);
            vertOut.add(y);
            vertOut.add(z + 1f);

            vertOut.add(x);
            vertOut.add(y + 1f);
            vertOut.add(z + 1f);

            vertOut.add(x + 1f);
            vertOut.add(y + 1f);
            vertOut.add(z + 1f);

            vertOut.add(x + 1f);
            vertOut.add(y);
            vertOut.add(z + 1f);

            for(int i = 0; i < 4;i++){
                norOut.add(0f);
                norOut.add(0f);
                norOut.add(1f);
            }

            if((colorGradient & GRADIENT_SIDES) != 0){
                colorGradient(colorOut, red, green, blue, 0, 3);
            }else{
                for(int i = 0; i < 4;i++){
                    colorOut.add(red);
                    colorOut.add(green);
                    colorOut.add(blue);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }
            }
        }

        if(top){
            vertOut.add(x);
            vertOut.add(y + 1f);
            vertOut.add(z);

            vertOut.add(x + 1f);
            vertOut.add(y + 1f);
            vertOut.add(z);

            vertOut.add(x + 1f);
            vertOut.add(y + 1f);
            vertOut.add(z + 1f);

            vertOut.add(x);
            vertOut.add(y + 1f);
            vertOut.add(z + 1f);

            for(int i = 0; i < 4;i++) {
                norOut.add(0f);
                norOut.add(1f);
                norOut.add(0f);
            }

            boolean darkerTop = (colorGradient & (DARKER_NORTH | DARKER_SOUTH | DARKER_EAST | DARKER_WEST)) != 0;

            if(darkerTop){

                if((colorGradient & (DARKER_SOUTH | DARKER_WEST)) != 0){
                    colorOut.add(red * 0.8f);
                    colorOut.add(green * 0.8f);
                    colorOut.add(blue * 0.8f);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }else{
                    colorOut.add(red);
                    colorOut.add(green);
                    colorOut.add(blue);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }

                if((colorGradient & (DARKER_NORTH | DARKER_WEST)) != 0){
                    colorOut.add(red * 0.8f);
                    colorOut.add(green * 0.8f);
                    colorOut.add(blue * 0.8f);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }else{
                    colorOut.add(red);
                    colorOut.add(green);
                    colorOut.add(blue);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }


                if((colorGradient & (DARKER_NORTH | DARKER_EAST)) != 0){
                    colorOut.add(red * 0.8f);
                    colorOut.add(green * 0.8f);
                    colorOut.add(blue * 0.8f);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }else{
                    colorOut.add(red);
                    colorOut.add(green);
                    colorOut.add(blue);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }

                if((colorGradient & (DARKER_SOUTH | DARKER_EAST)) != 0){
                    colorOut.add(red * 0.8f);
                    colorOut.add(green * 0.8f);
                    colorOut.add(blue * 0.8f);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }else{
                    colorOut.add(red);
                    colorOut.add(green);
                    colorOut.add(blue);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }


            }else{
                for(int i = 0; i < 4;i++){
                    colorOut.add(red);
                    colorOut.add(green);
                    colorOut.add(blue);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }
            }
        }

        if(bottom){

            vertOut.add(x);
            vertOut.add(y);
            vertOut.add(z);

            vertOut.add(x);
            vertOut.add(y);
            vertOut.add(z + 1f);

            vertOut.add(x + 1f);
            vertOut.add(y);
            vertOut.add(z + 1f);

            vertOut.add(x + 1f);
            vertOut.add(y);
            vertOut.add(z);

            for(int i = 0; i < 4;i++) {
                norOut.add(0f);
                norOut.add(-1f);
                norOut.add(0f);
            }

            if((colorGradient & GRADIENT_SIDES) != 0){
                for(int i = 0; i < 4;i++){
                    colorOut.add(red);
                    colorOut.add(green);
                    colorOut.add(blue);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }
            }else{
                for(int i = 0; i < 4;i++){
                    colorOut.add(red);
                    colorOut.add(green);
                    colorOut.add(blue);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }
            }
        }

        if(east){
            vertOut.add(x);
            vertOut.add(y);
            vertOut.add(z);

            vertOut.add(x);
            vertOut.add(y + 1f);
            vertOut.add(z);

            vertOut.add(x);
            vertOut.add(y + 1f);
            vertOut.add(z + 1f);

            vertOut.add(x);
            vertOut.add(y);
            vertOut.add(z + 1f);

            for(int i = 0; i < 4;i++){
                norOut.add(-1f);
                norOut.add(0f);
                norOut.add(0f);
            }


            if((colorGradient & GRADIENT_SIDES) != 0){
                colorGradient(colorOut, red, green, blue, 0, 3);
            }else{
                for(int i = 0; i < 4;i++){
                    colorOut.add(red);
                    colorOut.add(green);
                    colorOut.add(blue);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }
            }
        }

        if(west){
            vertOut.add(x + 1f);
            vertOut.add(y);
            vertOut.add(z);

            vertOut.add(x + 1f);
            vertOut.add(y);
            vertOut.add(z + 1f);

            vertOut.add(x + 1f);
            vertOut.add(y + 1f);
            vertOut.add(z + 1f);

            vertOut.add(x + 1f);
            vertOut.add(y + 1f);
            vertOut.add(z);

            for(int i = 0; i < 4;i++){
                norOut.add(-1f);
                norOut.add(0f);
                norOut.add(0f);
            }

            if((colorGradient & GRADIENT_SIDES) != 0){
                colorGradient(colorOut, red, green, blue, 0, 1);
            }else{
                for(int i = 0; i < 4;i++){
                    colorOut.add(red);
                    colorOut.add(green);
                    colorOut.add(blue);//For each quad add the color in
                    colorOut.add(1.0f);//For each quad add the color in
                }
            }

        }

        return vertOut.size() - start;
    }

    private static void colorGradient(List<Float> colorOut, float red, float green, float blue, int botVertOne, int botVertTwo){
        for(int i = 0; i < 4;i++){
            if(i == botVertOne || i == botVertTwo){
                colorOut.add(red * 0.8f);
                colorOut.add(green * 0.8f);
                colorOut.add(blue * 0.8f);//For each quad add the color in
                colorOut.add(1.0f);//For each quad add the color in
            }else{
                colorOut.add(red);
                colorOut.add(green);
                colorOut.add(blue);//For each quad add the color in
                colorOut.add(1.0f);//For each quad add the color in
            }
        }
    }

    private static float[] toArray(List<Float> vertices){
        float[] out = new float[vertices.size()];
        for(int i = 0; i < out.length;i++){
            out[i] = vertices.get(i);
        }
        return out;
    }

}
