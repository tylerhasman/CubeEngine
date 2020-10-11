package me.cube.engine;

import java.util.List;

public class VoxelData {

    private List<Float> vertices, colors, normals;
    private int width, height, length;

    public VoxelData(List<Float> vertices, List<Float> colors, List<Float> normals, int width, int height, int length) {
        this.vertices = vertices;
        this.colors = colors;
        this.normals = normals;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public VoxelModel toModel(){
        return new VoxelModel(vertices, colors, normals, width, height, length);
    }

}
