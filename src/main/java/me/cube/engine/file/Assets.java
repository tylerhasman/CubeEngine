package me.cube.engine.file;

import me.cube.engine.model.Mesh;
import me.cube.engine.model.SimpleVoxelMesh;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Assets {

    private static Map<String, SimpleVoxelMesh> models = new HashMap<>();

    public static void disposeAll(){
        for(String key : models.keySet()){
            Mesh model = models.get(key);
            model.dispose();
            System.out.println("[ASSET] Unloaded "+key);
        }
    }

    public static SimpleVoxelMesh loadModel(String path){
        path = "assets/models/"+path;
        if(models.containsKey(path)){
            return models.get(path);
        }

        SimpleVoxelMesh model;
        try {
            if(path.endsWith("vox")){
                VoxFile voxFile = new VoxFile(path);

                model = new SimpleVoxelMesh(voxFile.toVoxelColorArray(), voxFile.width(), voxFile.height(), voxFile.length());
            }else if(path.endsWith("vxm")){
                VxmFile voxFile = new VxmFile(path);

                model = new SimpleVoxelMesh(voxFile.toVoxelColorArray(), voxFile.width(), voxFile.height(), voxFile.length());
                model.pivot.set(voxFile.getPivot());
            }else{
                model = new SimpleVoxelMesh(new int[0][0][0], 0, 0, 0);
                System.err.println("Unknown file format "+path);
            }

            System.out.println("[ASSET] Loaded "+path);
        } catch (IOException e) {
            e.printStackTrace();
            model = new SimpleVoxelMesh(new int[0][0][0], 0, 0, 0);
        }

        models.put(path, model);

        return model;
    }

}
