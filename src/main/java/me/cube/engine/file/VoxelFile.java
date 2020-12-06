package me.cube.engine.file;

public interface VoxelFile {

    int[][][] toVoxelColorArray();

    int width();

    int height();

    int length();

}
