package me.cube.engine.game.particle;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class ParticleEngine {

    private List<Particle> particles;
    private final int maxParticles;

    public ParticleEngine(int maxParticles){
        this.maxParticles = maxParticles;
        particles = new ArrayList<>(maxParticles);
    }

    public void addParticle(Particle particle){
        if(particles.size() + 1 > maxParticles){
            particles.remove(0);
        }
        particles.add(particle);
    }

    public void update(float delta){
        for(int i = particles.size()-1;i >= 0;i--){
            Particle p = particles.get(i);
            p.update(delta);
            if(p.isRemoved()){
                particles.remove(i);
            }
        }
    }

    public void render(){

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBegin(GL_QUADS);
        {
            for(int i = 0; i < particles.size();i++){
                particles.get(i).render();
            }
        }
        glEnd();

    }

}
