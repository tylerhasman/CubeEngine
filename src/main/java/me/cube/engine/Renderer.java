package me.cube.engine;

import me.cube.engine.file.Assets;
import me.cube.engine.shader.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.GL_RGBA16F;

public class Renderer {

    private final List<Voxel> opaqueVoxels, transparentVoxels;

    //G-Buffer
    private final FrameBuffer gBuffer;

    private final Material gBufferMaterial, lightingMaterial;

    private final Vector3f ambientLight;

    private final int width, height;

    private int quadVAO, quadVBO, quadEBO;

    public Renderer(int width, int height){
        opaqueVoxels = new ArrayList<>();
        transparentVoxels = new ArrayList<>();

        gBuffer = new FrameBuffer();
        gBuffer.createTexture(width, height, GL_RGBA16F, GL_RGBA, GL_COLOR_ATTACHMENT0_EXT);//Position
        gBuffer.createTexture(width, height, GL_RGBA16F, GL_RGBA, GL_COLOR_ATTACHMENT1_EXT);//Normal
        gBuffer.createTexture(width, height, GL_RGBA16F, GL_RGBA, GL_COLOR_ATTACHMENT2_EXT);//Albedo

        gBufferMaterial = Assets.loadMaterial("gbuffer.json");
        lightingMaterial = Assets.loadMaterial("lightingPass.json");

        ambientLight = new Vector3f(1, 1, 1);

        this.width = width;
        this.height = height;
        initQuad();
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

        gBuffer.bind();

        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glEnable(GL_DEPTH_TEST);

        glEnable(GL_CULL_FACE);

        glCullFace(GL_FRONT);

        for(Voxel voxel : voxels){
            voxel.render(gBufferMaterial, true);
        }

        glDisable(GL_CULL_FACE);

        glDisable(GL_DEPTH_TEST);

        gBuffer.unbind();

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

    protected void renderScene(){

        Vector3f cameraPosition = Camera.getCameraPosition();

        transparentVoxels.sort((c1, c2) -> Float.compare(c2.position.distanceSquared(cameraPosition), c1.position.distanceSquared(cameraPosition)));

        glEnable(GL11C.GL_DEPTH_TEST);
        glEnable(GL_MULTISAMPLE);

        render(opaqueVoxels);

        //render(transparentVoxels);

        Matrix4f frame = new Matrix4f().identity().scale(2f);

        lightingMaterial.setUniformi("gPosition", 0);
        lightingMaterial.setUniformi("gNormal", 1);
        lightingMaterial.setUniformi("gAlbedoSpec", 2);
        lightingMaterial.setUniform3f("viewPos", Camera.getCameraPosition());

        lightingMaterial.setUniformMat4f("Frame", frame);

        gBuffer.bindTexture(0, GL_TEXTURE0);
        gBuffer.bindTexture(1, GL_TEXTURE1);
        gBuffer.bindTexture(2, GL_TEXTURE2);

        lightingMaterial.bind();

        renderQuad();

        lightingMaterial.unbind();

        glDisable(GL_MULTISAMPLE);
        glDisable(GL11.GL_DEPTH_TEST);

        transparentVoxels.clear();
        opaqueVoxels.clear();
    }

    public void dispose(){
        gBuffer.dispose();
        glDeleteBuffers(new int[] {quadVBO, quadVAO, quadEBO});
    }

}
