package me.cube.engine.game.world;

import org.joml.Vector3f;

public class DiffuseLight {

    public Vector3f position, color;
    public float intensity;
    public boolean removed;

    public DiffuseLight(){
        position = new Vector3f();
        color = new Vector3f(1f, 1f, 1f);
        intensity = 1f;
        removed = false;
    }

    public boolean isRemoved(){
        return removed;
    }

}
