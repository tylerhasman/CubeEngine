package me.cube.engine;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Voxel {

    public final Vector3f position, scale;
    public final Quaternionf rotation;
    public final Matrix4f transform;
    public VoxelModel model;

    public Voxel(){
        transform = new Matrix4f();
        position = new Vector3f();
        scale = new Vector3f(1f, 1f, 1f);
        rotation = new Quaternionf();
    }

    public Voxel(VoxelModel model){
        this();
        this.model = model;
    }

    public void update(float delta){

    }

}
