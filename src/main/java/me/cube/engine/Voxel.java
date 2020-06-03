package me.cube.engine;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Voxel {

    public final Vector3f position, scale;
    public final Quaternionf rotation;
    public VoxelModel model;
    private final List<Voxel> children;

    private final Matrix4f transform;

    public Voxel(){
        this(null);
    }

    public Voxel(VoxelModel model){
        transform = new Matrix4f();
        position = new Vector3f();
        scale = new Vector3f(1f, 1f, 1f);
        rotation = new Quaternionf();
        this.model = model;
        children = new ArrayList<>();
    }

    public void addChild(Voxel voxel){
        children.add(voxel);
    }

    private void calculateTransforms(Matrix4f parent){

        transform.identity();

        if(parent != null){
            transform.mul(parent);
        }
        transform.scale(scale).translate(position).rotate(rotation);


        for(Voxel child : children){
            child.calculateTransforms(transform);
        }

        /*

        if(children.size() == 0){
            System.out.println(position);
        }else{
            System.out.println(children.size());
        }*/

    }

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

    public void render(){
        calculateTransforms(null);

        render0();
    }
}
