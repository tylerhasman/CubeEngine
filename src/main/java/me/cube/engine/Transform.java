package me.cube.engine;

import org.joml.*;

import java.util.ArrayList;
import java.util.List;

public class Transform {

    private Transform parent;
    private List<Transform> children;

    private final Matrix4f frame;

    public final Voxel voxel;

    public Transform(Voxel voxel){
        if(voxel == null){
            throw new IllegalArgumentException("voxel cannot be null.");
        }
        this.voxel = voxel;
        frame = new Matrix4f().identity();
        children = new ArrayList<>();
    }

    public void set(Vector3f localPosition, Quaternionf localRotation, Vector3f localScale){
        frame.identity().translate(localPosition).scale(localScale).rotate(localRotation);
    }

    public Matrix4f getTransformation(){
        return toWorldFrame();
    }

    private Matrix4f toWorldFrame(){
        Matrix4f worldFrame = new Matrix4f().identity();

        if(parent != null){
            worldFrame.mul(parent.toWorldFrame());
        }

        worldFrame.mul(frame);

        return worldFrame;
    }

    public void setParent(Transform parent){
        if(this.parent != null){
            this.parent.removeChild(this);
        }

        parent.addChild(this);
    }

    public void addChild(Transform transform){
        if(transform == this){
            throw new IllegalArgumentException("transform cannot be a child of itself");
        }
        if(transform.parent != null){
            throw new IllegalArgumentException("transform already has a parent");
        }

        transform.parent = this;
        children.add(transform);
    }

    public void removeChild(Transform transform){
        if(transform.parent == this){
            children.remove(transform);
            transform.parent = null;
        }
    }

    public Vector3f transformPosition(Vector3f point){
        return toWorldFrame().transformPosition(point);
    }

    public void rotateAxis(float angle, float x, float y, float z) {
        frame.rotate(angle, x, y, z);
    }

    public Transform translate(float x, float y, float z){
        frame.translate(x, y, z);
        return this;
    }

    public void setLocalPosition(float x, float y, float z){
        frame.setTranslation(x, y, z);
    }

    public void lookAt(float worldX, float worldY, float worldZ){



    }

    public void scale(float xyz){
        frame.scale(xyz);
    }

    public void scale(float x, float y, float z){
        frame.scale(x, y, z);
    }

    public Transform getChild(int index){
        return children.get(index);
    }

    public int countChildren(){
        return children.size();
    }

    public Transform[] getChildren(){
        return children.toArray(new Transform[0]);
    }

    public Transform getParent() {
        return parent;
    }

    public boolean hasParent(){
        return parent != null;
    }

    public Vector3f getPosition(){
        return truncate(toWorldFrame().transform(new Vector4f(0, 0, 0, 1)));
    }

    public Vector3f getLocalPosition(){
        return frame.getTranslation(new Vector3f());
    }

    public Quaternionf getRotation(){
        return toWorldFrame().getNormalizedRotation(new Quaternionf());
    }

    public void setRotation(Quaternionf rotation){
        frame.rotate(getLocalRotation().invert()).rotate(rotation);
    }

    public Quaternionf getLocalRotation(){
        return frame.getNormalizedRotation(new Quaternionf());
    }

    public Vector3f getScale(){
        return toWorldFrame().getScale(new Vector3f());
    }

    public Vector3f getLocalScale(){
        return frame.getScale(new Vector3f());
    }

    private static Vector3f truncate(Vector4f vector4f){
        return new Vector3f(vector4f.x, vector4f.y, vector4f.z);
    }

}
