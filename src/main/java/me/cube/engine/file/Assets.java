package me.cube.engine.file;

import me.cube.engine.VoxelModel;
import me.cube.engine.file.VoxFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Assets {

    private static Map<String, VoxelModel> models = new HashMap<>();

    public static VoxelModel loadModel(String path){
        if(models.containsKey(path)){
            return models.get(path);
        }

        VoxelModel model;
        try {
            VoxFile voxFile = new VoxFile(path);

            model = new VoxelModel(voxFile.toVoxelColorArray(), voxFile.width(), voxFile.height(), voxFile.length());

            System.out.println("[ASSET] Loaded "+path);
        } catch (IOException e) {
            e.printStackTrace();
            model = new VoxelModel(new int[0][0][0], 0, 0, 0);
        }

        models.put(path, model);

        return model;
    }

}
