package me.cube.engine.file;

import me.cube.game.world.Chunk;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ChunkSave {

    private static final int VERSION = 2;

    private final int[][][] modified;

    private boolean loadedSuccessfully;

    private final File file;

    private boolean containsChanges;

    public ChunkSave(File file){
        this.file = file;
        loadedSuccessfully = false;
        modified = new int[Chunk.CHUNK_WIDTH][Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];
        for (int j = 0; j < Chunk.CHUNK_HEIGHT; j++) {

            for (int i = 0; i < Chunk.CHUNK_WIDTH; i++) {
                for (int k = 0; k < Chunk.CHUNK_WIDTH; k++) {
                    modified[i][j][k] = -1;
                }
            }
        }

        containsChanges = false;

        long time = System.currentTimeMillis();

        load(file);

        time = System.currentTimeMillis() - time;
    }

    public void modify(int x, int y, int z, int color){
        modified[x][y][z] = color & 0xFFFFFF;
        containsChanges = true;
    }

    public void save() throws IOException {
        if(!containsChanges){
            return;
        }
        containsChanges = false;
        if(!file.exists()){
            file.createNewFile();
        }
        try(DataOutputStream dataOutputStream = new DataOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file))))){
            dataOutputStream.writeInt(VERSION);

            for (int j = 0; j < Chunk.CHUNK_HEIGHT; j++) {
                boolean hasChanges = hasChange(modified, j);

                dataOutputStream.writeBoolean(hasChanges);

                if(hasChanges){
                    for (int i = 0; i < Chunk.CHUNK_WIDTH; i++) {
                        for (int k = 0; k < Chunk.CHUNK_WIDTH; k++) {
                            dataOutputStream.writeInt(modified[i][j][k]);
                        }
                    }
                }

            }

        } catch (FileNotFoundException e) {
            System.err.println("Failed to save chunk "+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save chunk "+e.getMessage());
        }
    }

    public boolean hasChanges(){
        return loadedSuccessfully;
    }

    public void applyTo(Chunk chunk){
        for(int i = 0; i < Chunk.CHUNK_WIDTH;i++){
            for(int j = 0; j < Chunk.CHUNK_HEIGHT;j++){
                for(int k = 0; k < Chunk.CHUNK_WIDTH;k++){

                    int cube = modified[i][j][k];

                    if(cube >= 0){
                        chunk.blocks[i][j][k] = cube;
                    }

                }
            }
        }
    }

    private static boolean hasChange(int[][][] colors, int y){
        for(int i = 0; i < Chunk.CHUNK_WIDTH;i++){
            for(int k = 0; k < Chunk.CHUNK_WIDTH;k++){
                if(colors[i][y][k] >= 0){
                    return true;
                }
            }
        }
        return false;
    }

    private void load(File file){
        try(DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream((file)))))) {

            int version = dataInputStream.readInt();

            if (version == 1 || version == 2) {

                for (int j = 0; j < Chunk.CHUNK_HEIGHT; j++) {
                    boolean hasChanges = dataInputStream.readBoolean();

                    if(hasChanges){
                        for (int i = 0; i < Chunk.CHUNK_WIDTH; i++) {
                            for (int k = 0; k < Chunk.CHUNK_WIDTH; k++) {

                                int rgb = dataInputStream.readInt();

                                modified[i][j][k] = rgb;

                            }
                        }
                    }

                }

                loadedSuccessfully = true;

            } else {
                System.err.println("Unknown chunk file version " + version);
            }
        } catch (FileNotFoundException ignored){
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
