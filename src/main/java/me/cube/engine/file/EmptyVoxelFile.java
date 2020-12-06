package me.cube.engine.file;

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
}
