package me.cube.engine;

import me.cube.engine.file.Assets;
import me.cube.engine.model.VoxelMesh;
import me.cube.engine.shader.Material;
import org.joml.Matrix3f;

import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

public class Voxel {

    public final String name;

    public VoxelMesh model;

    private final Transform transform;

    private Material material;

    public Voxel(){
        this("unnamed");
    }

    public Voxel(String name){
        this(name, null);
    }

    public Voxel(String name, VoxelMesh model){
        this(name, model, null);
    }

    public Voxel(String name, VoxelMesh model, Material material){
        this.name = name;
        this.model = model;

        this.material = material;
        this.transform = new Transform(this);
    }

    public Transform getTransform() {
        return transform;
    }

    /**
     * Recursively finds a child of this voxel. If two child voxels share the same name the behaviour is undefined.
     */
    public Voxel getChild(String name){

        for(Transform child : transform.getChildren()){
            if(child.voxel.name.equals(name)){
                return child.voxel;
            }
        }

        for(Transform child : transform.getChildren()){
            Voxel found = child.voxel.getChild(name);
            if(found != null){
                return found;
            }
        }
        return null;
    }

    /**
     * Renders this voxel
     * @param material the material to render it with
     * @param overrideChildrenMaterial if true children will be forced to use the provided material as well
     */
    public void render(Material material, boolean overrideChildrenMaterial){
        if(model != null){

            material.setUniformMat4f("ProjectionMatrix", Camera.projectionMatrix);
            material.setUniformMat4f("ViewMatrix", Camera.cameraMatrix);

            material.setUniformMat4f("ModelMatrix", transform.getTransformation());

            Matrix3f normalMatrix = new Matrix3f(transform.getTransformation().transpose().invert());

            material.setUniformMat3f("NormalMatrix", normalMatrix);

            Assets.getAmbientOcclusion().sendKernalToShader(material);

            material.setUniformi("u_NoiseTex", 0);
            Assets.getAmbientOcclusion().bindNoiseTexture(GL_TEXTURE0);

            material.bind();

            model.render();

            material.unbind();
        }

        for(Transform child : transform.getChildren()){
            if(overrideChildrenMaterial){
                child.voxel.render(material, true);
            }else{
                child.voxel.render();
            }
        }
    }

    /**
     * Updates this voxels transformations and renders it.
     */
    public void render(){
        render(getMaterial(), false);
    }

    public Material getMaterial() {
        if(material == null){
            if(transform.hasParent()){
                return transform.getParent().voxel.getMaterial();
            }else{
                material = Assets.defaultMaterial();
            }
        }
        return material;
    }
}
