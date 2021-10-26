package me.cube.engine;

import me.cube.engine.file.Assets;
import me.cube.engine.model.VoxelMesh;
import me.cube.engine.shader.Material;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

public class Voxel {

    public final String name;

    public VoxelMesh model;

    private Material material;

    public final Vector3f position, scale;
    public final Quaternionf rotation;

    private Voxel parent;
    private List<Voxel> children;

    public boolean transparent;

    public Voxel(){
        this("unnamed");
    }

    public Voxel(String name){
        this(name, null);
    }

    public Voxel(String name, VoxelMesh model){
        this(name, model, null);
    }

    public Voxel(String name, VoxelMesh model, Material material){
        this.name = name;
        this.model = model;

        this.material = material;

        transparent = false;

        position = new Vector3f();
        scale = new Vector3f(1, 1, 1);
        rotation = new Quaternionf().identity();

        children = new ArrayList<>();
    }

    public void setParent(Voxel parent) {
        if(this.parent != null){
            this.parent.children.remove(this);
        }
        this.parent = parent;
        parent.children.add(this);
    }

    public void addChild(Voxel child){
        if(child.parent != null){
            child.parent.children.remove(child);
        }
        children.add(child);
        child.parent = this;
    }

    private Matrix4f getLocalFrame(){
        return new Matrix4f().translate(position).scale(scale).rotate(rotation);
    }

    private Matrix4f getWorldFrame(){
        Matrix4f frame = new Matrix4f();

        if(parent != null){
            frame.mul(parent.getWorldFrame());
        }

        frame.mul(getLocalFrame());

        return frame;
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
        return null;
    }

    /**
     * Renders this voxel
     * @param material the material to render it with
     * @param overrideChildrenMaterial if true children will be forced to use the provided material as well
     */
    public void render(Material material, boolean overrideChildrenMaterial){
        if(model != null){

            Matrix4f frame = getWorldFrame();
            Matrix3f normalMatrix = new Matrix3f(frame.transpose(new Matrix4f()).invert());

            material.setUniformMat4f("ProjectionMatrix", Camera.projectionMatrix);
            material.setUniformMat4f("ViewMatrix", Camera.cameraMatrix);

            material.setUniformMat4f("ModelMatrix", frame);

            material.setUniformMat3f("NormalMatrix", normalMatrix);

            material.setUniform3f("u_AmbientLight", new Vector3f(1));

            material.bind();

            model.render();

        }

        for(Voxel child : children){
            if(overrideChildrenMaterial){
                child.render(material, true);
            }else{
                child.render();
            }
        }
    }

    /**
     * Updates this voxels transformations and renders it.
     */
    public void render(){
        render(getMaterial(), false);
    }

    public Material getMaterial() {
        if(material == null){
            if(parent != null){
                return parent.getMaterial();
            }else{
                material = Assets.defaultMaterial();
            }
        }
        return material;
    }
}
