package me.cube.engine.model;

import me.cube.engine.util.FloatArray;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class VoxelMesh extends Mesh {

    public final Vector3f pivot = new Vector3f();

    protected VoxelMesh() {
        super(GL11.GL_QUADS);
    }

}
