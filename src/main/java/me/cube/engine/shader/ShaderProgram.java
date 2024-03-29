package me.cube.engine.shader;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private final int programId;

    private int vertexShaderId;

    private int fragmentShaderId;

    private final Map<String, Integer> locationCache;

    protected ShaderProgram() {
        programId = glCreateProgram();
        locationCache = new HashMap<>();
        if (programId == 0) {
            throw new RuntimeException("Could not create Shader");
        }
    }

    public void createVertexShader(String shaderCode) {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new RuntimeException("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    private int getUniformLocation(String name){
        if(locationCache.containsKey(name)){
            return locationCache.get(name);
        }
        int location = glGetUniformLocation(programId, name);
        locationCache.put(name, location);
        return location;
    }

    public void setUniformf(String name, float val){
        int location = getUniformLocation(name);
        if(location != -1){
            glUniform1f(location, val);
        }else{
            //System.err.println("Shader has no uniform named "+name);
        }
    }

    public void setUniformf(String name, Vector3f val){
        int location = getUniformLocation(name);
        if(location != -1){
            glUniform3f(location, val.x, val.y, val.z);
        }else{
            //System.err.println("Shader has no uniform named "+name);
        }
    }

    public void setUniformMatrix4(String name, Matrix4f mat){
        int location = getUniformLocation(name);
        if(location != -1){
            float[] arr = new float[4 * 4];
            mat.get(arr);
            glUniformMatrix4fv(location, false, arr);
        }else{
            //System.err.println("Shader has no uniform named "+name);
        }
    }

    public void setUniformMatrix3(String name, Matrix3f mat) {
        int location = getUniformLocation(name);
        if(location != -1){
            float[] arr = new float[3 * 3];
            mat.get(arr);
            glUniformMatrix3fv(location, false, arr);
        }else{
            //System.err.println("Shader has no uniform named "+name);
        }
    }

    public void setUniformi(String name, int val) {
        int location = getUniformLocation(name);
        if(location != -1){
            glUniform1i(location, val);
        }else{
            //System.err.println("Shader has no uniform named "+name);
        }
    }

/*    public int getAttributeLocation(String name){
        return glGetAttribLocation(programId, name);
    }*/

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

}