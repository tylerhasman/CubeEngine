package me.cube.engine.game.particle;

public abstract class Particle {

    protected float life;

    public Particle(float life){
        this.life = life;
    }

    public boolean isRemoved(){
        return life <= 0;
    }

    public void update(float delta){
        life -= delta;
    }

    public abstract void render();

}
