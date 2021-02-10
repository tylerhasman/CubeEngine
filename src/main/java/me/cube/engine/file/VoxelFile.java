package me.cube.engine.file;

import org.joml.Vector3f;

public interface VoxelFile {

    int[][][] toVoxelColorArray();

    int width();

    int height();

    int length();

    Vector3f pivot();

}
