package me.cube.engine.game;

import me.cube.engine.Voxel;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Entity {

    private final World world;

    public final Vector3f position, scale;
    public final Quaternionf rotation;

    public final Voxel root;

    public float life;

    public Entity(World world){
        this.world = world;
/*        this.position = new Vector3f();
        this.scale = new Vector3f(1, 1, 1);
        this.rotation = new Quaternionf();*/
        root = new Voxel(null);
        position = root.position;
        scale = root.scale;
        rotation = root.rotation;
    }

    public World getWorld() {
        return world;
    }

    public void update(float delta){
        life += delta;
    }

}
