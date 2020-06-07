package me.cube.engine;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class Voxel {

    public final String name;
    public final Vector3f position, scale;
    public final Quaternionf rotation;
    public final Vector3f origin;
    public VoxelModel model;
    private final Map<String, Voxel> children;

    private final Matrix4f transform;

    public Voxel(){
        this("unnamed", null);
    }

    public Voxel(String name, VoxelModel model){
        this.name = name;
        transform = new Matrix4f();
        position = new Vector3f();
        scale = new Vector3f(1f, 1f, 1f);
        rotation = new Quaternionf();
        this.model = model;
        children = new HashMap<>();
        origin = new Vector3f();
        if(model != null){
           origin.set(model.pivot);
        }
    }

    /**
     * Get a copy of this matrixes transformation
     * @return
     */
    public Matrix4f getTransform(){
        return new Matrix4f(transform);
    }

    /**
     * Adds a child to this voxel, it will be rendered by this one's render
     */
    public void addChild(Voxel voxel){
        children.put(voxel.name, voxel);
    }

    /**
     * Recursively finds a child of this voxel. If two child voxels share the same name the behaviour is undefined.
     */
    public Voxel getChild(String name){
        if(children.containsKey(name)){
            return children.get(name);
        }
        for(Voxel child : children.values()){
            Voxel found = child.getChild(name);
            if(found != null){
                return found;
            }
        }
        return null;
    }

    /**
     * Recursively removes all children with this name
     * @param name
     */
    public void removeChild(String name){
        children.remove(name);
        for(Voxel child : children.values()){
           child.removeChild(name);
        }
    }

    /**
     * Calculates this voxel and all its childrens transforms relative to some parent voxel
     */
    private void calculateTransforms(Matrix4f parent){

        transform.identity();

        if(parent != null){
            transform.mul(parent);
        }

        transform.scale(scale).translate(position).rotate(rotation).translate(origin.mul(-1f, new Vector3f()));

        for(Voxel child : children.values()){
            child.calculateTransforms(transform);
        }

    }

    /**
     * Actually renders this voxel and children ones.
     * Does not update transformations
     */
    private void render0(){
        if(model != null){
            glPushMatrix();

            glMultMatrixf(transform.get(new float[16]));

            model.render();

            glPopMatrix();
        }

        for(Voxel child : children.values()){
            child.render0();
        }
    }

    /**
     * Updates this voxels transformations and renders it.
     */
    public void render(){
        calculateTransforms(null);

        render0();
    }
}
