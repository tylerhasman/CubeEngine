package me.cube.engine.shader;

import me.cube.engine.shader.ShaderProgram;
import me.cube.engine.util.FileUtil;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11C.glFlush;

public class Material {

    private ShaderProgram shader;

    private Map<String, Float> fUniforms = new HashMap<>();
    private Map<String, Vector3f> f3Uniforms = new HashMap<>();
    private Map<String, Matrix4f> fMUniforms = new HashMap<>();
    private Map<String, Matrix3f> fM3Uniforms = new HashMap<>();

    public Material(ShaderProgram shader){
        if(shader == null) {
            throw new IllegalArgumentException("shader cannot be null");
        }
        this.shader = shader;
    }

    //TODO: Move this to Assets.java
    public static Material loadMaterialFromFile(String pathToFile) throws IOException {
        File file = new File(pathToFile);
        if(!file.exists()){
            throw new FileNotFoundException(file.getAbsolutePath()+" not found");
        }
        String data = FileUtil.readFileAsString(file);

        return loadMaterial(data);
    }

    private static Material loadMaterial(String matData) throws IOException {
        JSONObject object = new JSONObject(matData);

        File shaderFolder = new File(object.getString("shader"));
        if(!shaderFolder.exists()){
            throw new FileNotFoundException("Couldn't find shader file "+shaderFolder.getAbsolutePath());
        }

        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(FileUtil.readFileAsString(new File(shaderFolder, "vertex.glsl")));
        shaderProgram.createFragmentShader(FileUtil.readFileAsString(new File(shaderFolder, "fragment.glsl")));

        shaderProgram.link();

        String name = object.getString("name");

        Material material = new Material(shaderProgram);

        JSONObject uniforms = object.getJSONObject("uniforms");

        for(String key : uniforms.keySet()){
            String value = uniforms.getString(key);

            String type = value.substring(0, value.indexOf(':'));
            String[] values = value.substring(value.indexOf(':')+1).split(",");

            if(type.equalsIgnoreCase("v3")){
                if(values.length != 3){
                    throw new IllegalArgumentException("Expected v3 for uniform "+key);
                }
                Vector3f vector3f = new Vector3f(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
                material.setUniform3f(key, vector3f);
            }else if(type.equalsIgnoreCase("f")){
                if(values.length != 1){
                    throw new IllegalArgumentException("Expected float for uniform "+key);
                }
                float f = Float.parseFloat(values[0]);

                material.setUniformf(key, f);
            }


        }

        System.out.println("Loaded material "+name);

        return material;
    }

    public void setUniformf(String name, float val){
        fUniforms.put(name, val);
        //shader.setUniformf(name, val);
    }

    public void setUniform3f(String name, Vector3f vector3f){
        f3Uniforms.put(name, new Vector3f(vector3f));
        //shader.setUniformf(name, vector3f);
    }

    public void setUniformMat4f(String name, Matrix4f matrix4f){
        fMUniforms.put(name, new Matrix4f(matrix4f));
        //shader.setUniformMatrix4(name, matrix4f);
    }

    public void setUniformMat3f(String name, Matrix3f matrix3f){
        fM3Uniforms.put(name, new Matrix3f(matrix3f));
        //shader.setUniformMatrix4(name, matrix4f);
    }

    public void dispose(){
        shader.cleanup();
    }

    public void bind(){
        shader.bind();

        for(String name : fUniforms.keySet()){
            shader.setUniformf(name, fUniforms.get(name));
        }

        for(String name : f3Uniforms.keySet()){
            shader.setUniformf(name, f3Uniforms.get(name));
        }

        for(String name : fMUniforms.keySet()){
            shader.setUniformMatrix4(name, fMUniforms.get(name));
        }

        for(String name : fM3Uniforms.keySet()){
            shader.setUniformMatrix3(name, fM3Uniforms.get(name));
        }

    }

    public void unbind(){
        shader.unbind();
    }

}
