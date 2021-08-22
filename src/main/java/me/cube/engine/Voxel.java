package me.cube.engine;

import me.cube.engine.file.Assets;
import me.cube.engine.model.VoxelMesh;
import me.cube.engine.shader.Material;
import org.joml.Matrix3f;

public class Voxel {

    public final String name;

    public VoxelMesh model;

    private Transform transform;

    private Material material;

    private VoxelMesh coordinateModel;

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
        this.coordinateModel = Assets.loadModel("coordinates.vxm");
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
     * Updates this voxels transformations and renders it.
     */
    public void render(){
        if(model != null){

            getMaterial().setUniformMat4f("ProjectionMatrix", Camera.projectionMatrix);
            getMaterial().setUniformMat4f("ViewMatrix", Camera.cameraMatrix);

            getMaterial().setUniformMat4f("ModelMatrix", transform.getTransformation());

            Matrix3f normalMatrix = new Matrix3f(transform.getTransformation().transpose().invert());

            getMaterial().setUniformMat3f("NormalMatrix", normalMatrix);

            getMaterial().bind();

            model.render();

            getMaterial().unbind();
        }

        for(Transform child : transform.getChildren()){
            child.voxel.render();
        }
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
