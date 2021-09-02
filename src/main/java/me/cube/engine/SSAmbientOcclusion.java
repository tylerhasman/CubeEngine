package me.cube.engine;

import me.cube.engine.shader.Material;
import org.joml.Random;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL30C.GL_RGBA16F;

/**
 * https://learnopengl.com/Advanced-Lighting/SSAO
 */
public class SSAmbientOcclusion {

    private static final int KERNAL_SIZE = 64;
    private static final int NOISE_SIZE = 4;

    private Vector3f[] kernal;

    private int noiseTexture;

    public SSAmbientOcclusion(){
        initKernal();
        initNoise();
    }

    public void dispose(){
        glDeleteTextures(noiseTexture);
    }

    public void sendKernalToShader(Material material){
        for(int i = 0; i < kernal.length;i++){
            material.setUniform3f("samples["+i+"]", kernal[i]);
        }
    }

    private void initNoise(){
        Random random = new Random();
        float[] noise = new float[NOISE_SIZE * NOISE_SIZE * 3];

        for(int i = 0; i < noise.length;i += 3){
            noise[i] = random.nextFloat() * 2.0f - 1.0f;
            noise[i+1] = random.nextFloat() * 2.0f - 1.0f;
            noise[i+2] = 0.0f;
        }

        int[] texId = new int[1];

        glGenTextures(texId);

        noiseTexture = texId[0];

        glBindTexture(GL_TEXTURE_2D, noiseTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, NOISE_SIZE, NOISE_SIZE, 0, GL_RGB, GL_FLOAT, noise);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);


    }

    public void bindNoiseTexture(int unit){
        glActiveTexture(unit);
        glBindTexture(GL_TEXTURE_2D, noiseTexture);
    }

    private void initKernal(){
        Random random = new Random();
        kernal = new Vector3f[KERNAL_SIZE];
        for(int i = 0; i < KERNAL_SIZE;i++){
            Vector3f vec = new Vector3f(random.nextFloat() * 2.0f - 1.0f, random.nextFloat() * 2.0f - 1.0f, random.nextFloat()).normalize();

            float scale = ((float)i) / KERNAL_SIZE;

            scale = lerp(0.1f, 1.0f, scale * scale);

            vec.mul(scale);

            kernal[i] = vec;

        }
    }


    private static float lerp(float a, float b, float alpha){
        return a + alpha * (b - a);
    }
}
