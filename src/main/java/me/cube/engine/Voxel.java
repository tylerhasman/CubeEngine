package me.cube.engine;

import org.joml.Matrix4f;

public class Voxel {

    public final Matrix4f transform;
    public VoxelModel model;

    public Voxel(VoxelModel model){
        this.model = model;
        transform = new Matrix4f();
    }

}
