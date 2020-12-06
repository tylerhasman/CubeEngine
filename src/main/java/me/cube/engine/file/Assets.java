package me.cube.engine.file;

import me.cube.engine.model.Mesh;
import me.cube.engine.model.SimpleVoxelMesh;
import me.cube.engine.shader.Material;
import me.cube.engine.shader.ShaderProgram;
import me.cube.engine.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Assets {

    private static Map<String, SimpleVoxelMesh> models = new HashMap<>();
    private static Map<String, Material> materials = new HashMap<>();

    public static void disposeAll(){
        for(String key : models.keySet()){
            Mesh model = models.get(key);
            model.dispose();
            System.out.println("[ASSET] Unloaded model "+key);
        }

        for(String key : materials.keySet()){
            Material material = materials.get(key);
            material.dispose();
            System.out.println("[ASSET] Unloaded material "+key);
        }
    }

    public static Material defaultMaterial() {
        return loadMaterial("default.json");
    }

    public static Material loadMaterial(String path){
        if(materials.containsKey(path)){
            return materials.get(path);
        }
        Material material = null;
        try {
            material = Material.loadMaterialFromFile("assets/materials/"+path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        materials.put(path, material);
        return material;
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
