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

    public float gravity;

    public Entity(World world){
        this.world = world;
        root = new Voxel(null);
        position = root.position;
        scale = root.scale;
        rotation = root.rotation;
        gravity = -0.2f;
    }

    public World getWorld() {
        return world;
    }

    public void update(float delta){
        life += delta;

        if(!getWorld().getTerrain().isSolid(position)){
           position.y += gravity;
        }

    }

}
