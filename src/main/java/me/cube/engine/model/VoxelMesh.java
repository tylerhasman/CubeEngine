package me.cube.engine.model;

import me.cube.engine.util.FloatArray;
import org.joml.Vector3f;

public class VoxelMesh extends Mesh {

    public final Vector3f pivot = new Vector3f();
    public final int width, height, length;

    protected VoxelMesh(int width, int height, int length) {
        this.width = width;
        this.height = height;
        this.length = length;
    }

    protected VoxelMesh(FloatArray vertices, FloatArray colors, FloatArray normals, int width, int height, int length) {
        super(vertices, colors, normals);
        this.width = width;
        this.height = height;
        this.length = length;
    }

    protected VoxelMesh(float[] vertexBufferData, float[] colorBufferData, float[] normalBufferData, int width, int height, int length) {
        super(vertexBufferData, colorBufferData, normalBufferData);
        this.width = width;
        this.height = height;
        this.length = length;
    }

}
