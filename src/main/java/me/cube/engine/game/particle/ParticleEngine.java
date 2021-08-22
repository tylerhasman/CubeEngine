package me.cube.engine.game.particle;

import me.cube.engine.game.CubeGame;
import me.cube.engine.game.world.DiffuseLight;
import org.joml.Vector3f;

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

    public void render(Vector3f ambientLight, List<DiffuseLight> lights){

        for(int i = 0; i < particles.size();i++){

            Particle particle = particles.get(i);

            particle.material.setUniform3f("u_AmbientLight", ambientLight);

            for(int j = 0; j < lights.size();j++){
                DiffuseLight light = lights.get(j);
                particle.material.setUniform3f("DiffuseLight"+i+"_Position", light.position);
                particle.material.setUniform3f("DiffuseLight"+i+"_Color", light.color);
                particle.material.setUniformf("DiffuseLight"+i+"_Intensity", 0);
            }

            particles.get(i).render();
        }

    }

}
