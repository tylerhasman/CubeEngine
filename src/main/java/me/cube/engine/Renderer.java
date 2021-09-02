package me.cube.engine;

import me.cube.engine.file.Assets;
import me.cube.engine.shader.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30C.GL_RGBA16F;

public class Renderer {

    private static final int SSAO_KERNAL_SIZE = 64;

    private final List<Voxel> opaqueVoxels, transparentVoxels;

    //G-Buffer
    private final FrameBuffer gBuffer;

    private final Material gBufferMaterial, lightingMaterial, ssaoMaterial;

    private final Vector3f ambientLight;

    private final int width, height;

    private int quadVAO, quadVBO, quadEBO;


    //SSAO
    private Vector3f[] ssaoKernel;
    private int ssaoNoiseTex;
    private FrameBuffer ssaoBuffer;

    public Renderer(int width, int height){
        opaqueVoxels = new ArrayList<>();
        transparentVoxels = new ArrayList<>();

        gBuffer = new FrameBuffer();
        gBuffer.createTexture(width, height, GL_RGBA16F, GL_RGBA, GL_COLOR_ATTACHMENT0_EXT);//Position
        gBuffer.createTexture(width, height, GL_RGBA16F, GL_RGBA, GL_COLOR_ATTACHMENT1_EXT);//Normal
        gBuffer.createTexture(width, height, GL_RGBA16F, GL_RGBA, GL_COLOR_ATTACHMENT2_EXT);//Albedo

        ssaoBuffer = new FrameBuffer();
        ssaoBuffer.createTexture(width, height, GL_RED, GL_RED, GL_COLOR_ATTACHMENT0);

        gBufferMaterial = Assets.loadMaterial("gbuffer.json");
        lightingMaterial = Assets.loadMaterial("lightingPass.json");
        ssaoMaterial = Assets.loadMaterial("ssao.json");

        ambientLight = new Vector3f(1, 1, 1);

        this.width = width;
        this.height = height;
        initQuad();
        initSSAO();
    }

    private void initSSAO(){
        ssaoKernel = new Vector3f[SSAO_KERNAL_SIZE];
        Random random = new Random();

        for(int i = 0; i < ssaoKernel.length; i++){

            float x = random.nextFloat() * 2 - 1;
            float y = random.nextFloat() * 2 - 1;
            float z = random.nextFloat();

            Vector3f sample = new Vector3f(x, y, z).normalize();
            float scale = (float) i / SSAO_KERNAL_SIZE;
            scale = 0.1f + (1f - 0.1f) * (scale * scale);
            sample.mul(scale);

            ssaoKernel[i] = sample;

        }

        float[] noise = new float[4 * 4 * 3];

        for(int i = 0; i < noise.length;i += 3){
            noise[i] = random.nextFloat() * 2 - 1;
            noise[i+1] = random.nextFloat() * 2 - 1;
            noise[i+2] = 0;
        }

        ssaoNoiseTex = glGenTextures();
        glBindTexture(GL11C.GL_TEXTURE_2D, ssaoNoiseTex);

        glTexImage2D(GL11C.GL_TEXTURE_2D, 0, GL_RGBA16F, 4, 4, 0, GL_RGB, GL_FLOAT, noise);
        glTexParameteri(GL11C.GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL11C.GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL11C.GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL11C.GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

    }

    private void initQuad(){
        quadVAO = glGenBuffers();
        quadVBO = glGenBuffers();
        quadEBO = glGenBuffers();

        float[] vertices = new float[] {
                0.5f, 0.5f, 0,
                0.5f, -0.5f, 0,
                -0.5f, -0.5f, 0,
                -0.5f, 0.5f, 0,
        };

        int[] indices = new int[] {
                0, 1, 3,
                1, 2, 3
        };

        glBindVertexArray(quadVAO);

        glBindBuffer(GL_ARRAY_BUFFER, quadVBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, quadEBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public void setAmbientLight(float r, float g, float b) {
        this.ambientLight.set(r, g, b);
    }

    public void render(Voxel voxel){
        if(voxel.transparent){
            transparentVoxels.add(voxel);
        }else{
            opaqueVoxels.add(voxel);
        }
    }

    private void render(List<Voxel> voxels){

        for(Voxel voxel : voxels){
            voxel.render(gBufferMaterial, true);
        }

    }

    private void renderQuad(){

        glBindVertexArray(quadVAO);

        glBindBuffer(GL_ARRAY_BUFFER, quadVBO);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, quadEBO);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 12, 0);
        glEnableVertexAttribArray(0);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }


    private void renderGBuffer(){
        glEnable(GL11C.GL_DEPTH_TEST);
        glEnable(GL_MULTISAMPLE);

        glEnable(GL_CULL_FACE);

        glCullFace(GL_FRONT);

        gBuffer.bind();

        glClearColor(135f / 255f, 206f / 255f, 235f / 255f, 0.0f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        render(opaqueVoxels);

        gBuffer.unbind();

        glDisable(GL_CULL_FACE);

        glDisable(GL_MULTISAMPLE);
        glDisable(GL11.GL_DEPTH_TEST);
    }

    private void renderSSAO(){
        Matrix4f frame = new Matrix4f().identity().scale(2f);

        ssaoMaterial.setUniformi("gPosition", 0);
        ssaoMaterial.setUniformi("gNormal", 1);
        ssaoMaterial.setUniformi("texNoise", 2);
        ssaoMaterial.setUniformMat4f("projection", Camera.projectionMatrix);

        ssaoMaterial.setUniformMat4f("Frame", frame);

        for(int i = 0; i < ssaoKernel.length; i++){
            ssaoMaterial.setUniform3f("samples["+i+"]", ssaoKernel[i]);
        }

        gBuffer.bindTexture(0, GL_TEXTURE0);
        gBuffer.bindTexture(1, GL_TEXTURE1);

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL11.GL_TEXTURE_2D, ssaoNoiseTex);

        ssaoBuffer.bind();

        ssaoMaterial.bind();

        renderQuad();

        ssaoMaterial.unbind();

        ssaoBuffer.unbind();
    }

    protected void renderScene(){

        Vector3f cameraPosition = Camera.getCameraPosition();

        transparentVoxels.sort((c1, c2) -> Float.compare(c2.position.distanceSquared(cameraPosition), c1.position.distanceSquared(cameraPosition)));

        renderGBuffer();

        renderSSAO();

        Matrix4f frame = new Matrix4f().identity().scale(2f);

        lightingMaterial.setUniformi("gPosition", 0);
        lightingMaterial.setUniformi("gNormal", 1);
        lightingMaterial.setUniformi("gAlbedoSpec", 2);
        lightingMaterial.setUniformi("gSSAO", 3);
        lightingMaterial.setUniform3f("viewPos", Camera.getCameraPosition());
        lightingMaterial.setUniform3f("u_AmbientLight", ambientLight);

        lightingMaterial.setUniformMat4f("Frame", frame);

        gBuffer.bindTexture(0, GL_TEXTURE0);
        gBuffer.bindTexture(1, GL_TEXTURE1);
        gBuffer.bindTexture(2, GL_TEXTURE2);
        ssaoBuffer.bindTexture(0, GL_TEXTURE3);

        lightingMaterial.bind();

        renderQuad();

        lightingMaterial.unbind();

        transparentVoxels.clear();
        opaqueVoxels.clear();
    }

    public void dispose(){
        gBuffer.dispose();
        ssaoBuffer.dispose();
        glDeleteTextures(ssaoNoiseTex);
        glDeleteBuffers(new int[] {quadVBO, quadVAO, quadEBO});
    }

}
