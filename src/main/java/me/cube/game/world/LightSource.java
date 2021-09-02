package me.cube.game.world;

import org.joml.Vector3f;

public class LightSource {

    public Vector3f position;
    public float intensity;

    public LightSource(){
        position.set(0);
        intensity = 10;
    }

}
