package me.cube.engine;

import me.cube.engine.file.Assets;
import me.cube.engine.game.CubeGame;
import me.cube.engine.model.VoxelMesh;
import me.cube.engine.shader.Material;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class Voxel {

    //private Voxel parent;

    public final String name;
/*    public final Vector3f position, scale;
    public final Quaternionf rotation;
    public final Vector3f origin;*/
    public VoxelMesh model;
    //private final Map<String, Voxel> children;

    private Transform transform;

    //TODO: Allow materials to be changed dynamically...
    public final Material material;

    public Voxel(){
        this("unnamed");
    }

    public Voxel(String name){
        this(name, null);
    }

    public Voxel(String name, VoxelMesh model){
        this(name, model, Assets.defaultMaterial());
    }

    public Voxel(String name, VoxelMesh model, Material material){
        if(material == null){
            throw new IllegalArgumentException("material cannot be null");
        }
        this.name = name;
        this.model = model;
/*        if(model != null){
           origin.set(model.pivot);
        }*/
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
     * Updates this voxels transformations and renders it.
     */
    public void render(){
        if(model != null){

            material.setUniformMat4f("ProjectionMatrix", Camera.projectionMatrix);
            material.setUniformMat4f("ViewMatrix", Camera.cameraMatrix);

            material.setUniformMat4f("ModelMatrix", transform.getTransformation());

            Matrix3f normalMatrix = new Matrix3f(transform.getTransformation().transpose().invert());

            material.setUniformMat3f("NormalMatrix", normalMatrix);

            material.bind();

            model.render();

            material.unbind();
        }

        for(Transform child : transform.getChildren()){
            child.voxel.render();
        }
    }
}
