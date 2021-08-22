package me.cube.engine.game.particle;

import me.cube.engine.file.Assets;
import me.cube.engine.shader.Material;

public abstract class Particle {

    protected float life;

    protected Material material;

    public Particle(float life){
        this.life = life;
        material = Assets.defaultMaterial();
    }

    public boolean isRemoved(){
        return life <= 0;
    }

    public void update(float delta){
        life -= delta;
    }

    public abstract void render();

}
