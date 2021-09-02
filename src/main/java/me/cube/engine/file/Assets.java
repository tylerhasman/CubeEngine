package me.cube.engine.file;

import me.cube.engine.SSAmbientOcclusion;
import me.cube.game.entity.CreatureAppearance;
import me.cube.game.entity.CreatureAppearance.BodyPart;
import me.cube.game.entity.CreatureAppearance.PartType;
import me.cube.engine.model.Mesh;
import me.cube.engine.model.SimpleVoxelMesh;
import me.cube.engine.shader.Material;
import me.cube.engine.util.FileUtil;
import org.joml.Vector3f;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class Assets {

    private static int dynamicMeshCount = 0;

    private static Map<String, SimpleVoxelMesh> models = new HashMap<>();
    private static Map<String, Material> materials = new HashMap<>();
    private static Map<String, CubeFont> fonts = new HashMap<>();
    private static Map<String, CreatureAppearance.BodyPart> bodyParts = new HashMap<>();

    private static SSAmbientOcclusion ambientOcclusion = new SSAmbientOcclusion();

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

        ambientOcclusion.dispose();
    }

    public static SSAmbientOcclusion getAmbientOcclusion() {
        return ambientOcclusion;
    }

    public static Material defaultMaterial() {
        return loadMaterial("default.json");
    }

    public static Material loadMaterial(String path){
        if(materials.containsKey(path)){
            return new Material(materials.get(path));
        }
        Material material;
        try {
            material = Material.loadMaterialFromFile("assets/materials/"+path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        materials.put(path, material);
        return new Material(material);
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

    public static BodyPart loadBodyPart(String path){
        if(bodyParts.containsKey(path)){
            return bodyParts.get(path);
        }

        try {
            String file = FileUtil.readFileAsString(new File("assets/creatures/parts/"+path));

            JSONObject jsonObject = new JSONObject(file);

            String model = jsonObject.getString("model");
            PartType partType = jsonObject.getEnum(PartType.class, "part");

            BodyPart bodyPart = new BodyPart(model, partType);

            JSONArray array = jsonObject.getJSONArray("connectors");

            for(int i = 0; i < array.length();i++){
                JSONObject connector = array.getJSONObject(i);

                PartType connectorType = connector.getEnum(PartType.class, "part");

                StringTokenizer tokenizer = new StringTokenizer(connector.getString("position"), " ");

                Vector3f position = new Vector3f(
                        Float.parseFloat(tokenizer.nextToken()),
                        Float.parseFloat(tokenizer.nextToken()),
                        Float.parseFloat(tokenizer.nextToken())
                );

                bodyPart.addConnector(connectorType, position);

            }

            bodyParts.put(path, bodyPart);

            return bodyPart;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static void registerModel(SimpleVoxelMesh mesh){
        models.put("DynamicMesh"+(dynamicMeshCount++), mesh);
    }

}
