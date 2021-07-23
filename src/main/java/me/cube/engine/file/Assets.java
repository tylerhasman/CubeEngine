package me.cube.engine.file;

import me.cube.engine.model.Mesh;
import me.cube.engine.model.SimpleVoxelMesh;
import me.cube.engine.shader.Material;
import me.cube.engine.shader.ShaderProgram;
import me.cube.engine.util.FileUtil;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Assets {

    private static Map<String, SimpleVoxelMesh> models = new HashMap<>();
    private static Map<String, Material> materials = new HashMap<>();
    private static Map<String, CubeFont> fonts = new HashMap<>();

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

        for(String key : fonts.keySet()){
            CubeFont font = fonts.get(key);
            font.dispose();
            System.out.println("[ASSET] Unloaded font "+key);
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

    //TODO: This is really shitty because loadVoxelData assumes the file is in the path assets/models/XXXX
    //      We need to make loadVoxelData accept absolute paths as well!
    //      A super far in the future to-do would be to load everything from a proper asset cache file or something.
    public static List<VoxelFile> loadVoxelDataFolder(String pathToFolder){
        File folder = new File(pathToFolder);

        if(!folder.exists() || !folder.isDirectory()){
            return Collections.emptyList();
        }

        List<VoxelFile> files = new ArrayList<>();

        for(File child : folder.listFiles()){
            if(child.isFile()){
                files.add(loadVoxelData(child.getAbsolutePath(), true));
            }
        }

        return files;
    }

    //TODO: Handle null
    public static CubeFont loadFont(String path) {
        if(fonts.containsKey(path)){
            return fonts.get(path);
        }

        CubeFont font = null;
        try {
            font = new CubeFont(path, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        fonts.put(path, font);

        return font;
    }

    public static VoxelFile loadVoxelData(String path, boolean absolutePath){
        if(!absolutePath){
            path = "assets/models/"+path;
        }

        try {
            if(path.endsWith("vox")){
                return new VoxFile(path);
            }else if(path.endsWith("vxm")){
                return new VxmFile(path);
            }else{
                System.err.println("Unknown file format "+path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new EmptyVoxelFile();
    }

    public static SimpleVoxelMesh loadModel(String path){

        if(models.containsKey(path)){
            return models.get(path);
        }

        VoxelFile voxelFile = loadVoxelData(path, false);

        SimpleVoxelMesh model = new SimpleVoxelMesh(voxelFile.toVoxelColorArray(), voxelFile.width(), voxelFile.height(), voxelFile.length(), voxelFile.pivot());

        models.put(path, model);

        return model;
    }

}
