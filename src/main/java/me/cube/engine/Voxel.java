package me.cube.engine;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Voxel {

    public final String name;
    public final Vector3f position, scale;
    public final Quaternionf rotation;
    public final Vector3f origin;
    public VoxelModel model;
    private final List<Voxel> children;

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
        children = new ArrayList<>();
        origin = new Vector3f();
        if(model != null){
           origin.set(model.pivot);
        }
    }

    /**
     * Adds a child to this voxel, it will be rendered by this one's render
     */
    public void addChild(Voxel voxel){
        children.add(voxel);
    }

    /**
     * Recursively finds a child of this voxel. If two child voxels share the same name the behaviour is undefined.
     */
    public Voxel getChild(String name){
        for(Voxel child : children){
            if(child.name.equals(name)){
                return child;
            }
        }
        for(Voxel child : children){
            Voxel found = child.getChild(name);
            if(found != null){
                return found;
            }
        }
        System.err.println("[ERROR] Couldnt find child "+name);
        return null;
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

        for(Voxel child : children){
            child.calculateTransforms(transform);
        }

    }

    /**
     * Actually renders this voxel and children ones.
     * Does not update transformations
     */
    private void render0(){
        if(model != null){

            float[] verts = model.vertices;
            float[] colors = model.colors;
            float[] normals = model.normals;

            int colorIndex = 0;
            int normalIndex = 0;

            Vector3f position = new Vector3f();

            for(int i = 0; i < verts.length;i += 12){

                float red = colors[colorIndex++];
                float green = colors[colorIndex++];
                float blue = colors[colorIndex++];

                float norX = normals[normalIndex++];
                float norY = normals[normalIndex++];
                float norZ = normals[normalIndex++];

                glColor3f(red, green, blue);
                glNormal3f(norX, norY, norZ);

                for(int j = 0; j < 12;j += 3){
                    transform.transformPosition(verts[i + j], verts[i + j + 1], verts[i + j + 2], position);

                    glVertex3f(position.x, position.y, position.z);
                }

            }
        }

        for(Voxel child : children){
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
