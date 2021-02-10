package me.cube.engine.file;

import org.joml.Vector3f;

public class EmptyVoxelFile implements VoxelFile {
    @Override
    public int[][][] toVoxelColorArray() {
        return new int[0][0][0];
    }

    @Override
    public int width() {
        return 0;
    }

    @Override
    public int height() {
        return 0;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public Vector3f pivot() {
        return new Vector3f();
    }
}
