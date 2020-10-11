package me.cube.engine.file;

import org.joml.Vector3f;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VxmFile {

    private int version;

    private int width, height, length;

    private float pivX, pivY, pivZ;

    private int[][][] voxels;
    private int[] colorPallete;

    public VxmFile(String path) throws IOException {
        File file = new File(path);
        try(FileInputStream fis = new FileInputStream(path)){

            long size = file.length();

            byte[] data = new byte[(int) size];

            int read = fis.read(data);

            load(ByteBuffer.wrap(data, 0, read).order(ByteOrder.LITTLE_ENDIAN));
        }//Auto closes the fis
    }

    public int[][][] toVoxelColorArray(){
        int[][][] v = new int[width][height][length];

        for(int i = 0; i < width;i++){
            for(int j = 0; j < height;j++){
                for(int k = 0; k < length;k++){
                    if(voxels[i][j][k] == 255){
                        continue;
                    }
                    v[i][j][k] = colorPallete[voxels[i][j][k]];
                }
            }
        }

        return v;
    }

    public Vector3f getPivot(){
        return new Vector3f(pivX, pivY, pivZ).mul(width, height, length).sub(width / 2f, height / 2f, length / 2f);
    }

    public int width(){
        return width;
    }

    public int height(){
        return height;
    }

    public int length(){
        return length;
    }

    private void load(ByteBuffer wrap) {
        char v = (char) wrap.get();
        char x = (char) wrap.get();
        char m = (char) wrap.get();
        version = wrap.get();
        if(v != 'V' || x != 'X' || m != 'M'){
            throw new IllegalArgumentException("Invalid magic header "+v+""+x+""+m);
        }
        if(version != 57 && version != 65){
            throw new IllegalArgumentException("Invalid version "+version);
        }

        width = wrap.getInt();
        height = wrap.getInt();
        length = wrap.getInt();

        voxels = new int[width][height][length];

        for(int i = 0; i < width;i++){
            for(int j = 0; j < height;j++){
                for(int k = 0; k < length;k++){
                    voxels[i][j][k] = 255;
                }
            }
        }

        pivX = wrap.getFloat();
        pivY = wrap.getFloat();
        pivZ = wrap.getFloat();

        if(wrap.get() != 0){
            throw new IllegalArgumentException("Screen data!");
        }

        wrap.getFloat();
        wrap.getFloat();
        wrap.getFloat();
        wrap.getFloat();

        int lodLevels = wrap.getInt();

        for(int i = 0; i < lodLevels;i++){
            wrap.getInt();
            wrap.getInt();

            int texAmount = wrap.getInt();

            for(int j = 0; j < texAmount;j++){


                while(wrap.get() != 0);

                int size = wrap.getInt();

                wrap.get(new byte[size]);
            }

            for(int j = 0; j < 6;j++){
                int quadAmount = wrap.getInt();
                for(int q = 0; q < quadAmount;q++){
                    wrap.getFloat();
                    wrap.getFloat();
                    wrap.getFloat();
                    wrap.getInt();
                    wrap.getInt();
                    wrap.getFloat();
                    wrap.getFloat();
                    wrap.getFloat();
                    wrap.getInt();
                    wrap.getInt();
                    wrap.getFloat();
                    wrap.getFloat();
                    wrap.getFloat();
                    wrap.getInt();
                    wrap.getInt();
                    wrap.getFloat();
                    wrap.getFloat();
                    wrap.getFloat();
                    wrap.getInt();
                    wrap.getInt();
                }
            }

        }

        int materialAmount = wrap.get() & 0xFF;

        colorPallete = new int[materialAmount];

        for(int i = 0; i < materialAmount;i++){
            int col = wrap.getInt();
            boolean emissive = wrap.get() == 1;
            colorPallete[i] = col;
        }

        if(colorPallete.length == 0){
            colorPallete = new int[1];
            colorPallete[0] = 16777215;
        }

        int idx = 0;
        for(;;){
            int runLength = wrap.get() & 0xFF;
            if(runLength == 0){
                break;
            }
            int runMaterialIdx = wrap.get() & 0xFF;
            if(runMaterialIdx == 255){
                idx += runLength;
            }else{
                byte runMaterial = (byte) runMaterialIdx;
                for(int i = idx; i < idx + runLength;i++){
                    int xx = i / (height * length);
                    int yy = i / length % height;
                    int zz = i % length;

                    voxels[xx][yy][zz] = runMaterial;
                }
                idx += runLength;
            }
        }

    }

}
