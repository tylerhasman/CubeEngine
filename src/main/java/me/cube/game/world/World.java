package me.cube.game.world;

import me.cube.engine.Renderer;
import me.cube.game.entity.*;
import me.cube.game.particle.ParticleEngine;
import me.cube.engine.util.MathUtil;
import org.joml.Math;
import org.joml.Vector3f;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class World {

    private static final float ONE_DAY = 24 * 60;//Seconds

    public static final float WORLD_SCALE = 1f;

    private List<Entity> entities;

    private Terrain terrain;

    private ParticleEngine particleEngine;

    private float worldTime;

    private List<DiffuseLight> diffuseLights;

    public World(){
        entities = new ArrayList<>();
        particleEngine = new ParticleEngine(2000);

        terrain = new Terrain(8, "test");

        worldTime = ONE_DAY / 2f;

        diffuseLights = new ArrayList<>();
    }

    public DiffuseLight createLight(){
        DiffuseLight diffuseLight = new DiffuseLight();

        diffuseLights.add(diffuseLight);

        return diffuseLight;
    }

    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public ParticleEngine getParticleEngine() {
        return particleEngine;
    }

    public void addEntity(Entity entity){
        entities.add(entity);
    }

    public void update(float delta, Vector3f fromPosition){
        for(Entity entity : entities){
            entity.updatePhysics(delta);
        }
        for(Entity entity : entities){
            entity.update(delta);
        }

        diffuseLights.removeIf(DiffuseLight::isRemoved);

        terrain.updateTerrain(new Vector3f(fromPosition));
        particleEngine.update(delta);
    }

    public String getWorldTimeFormatted(){
        float normalizedWorldTime = worldTime / (24f * 60f);

        int hour = (int) (normalizedWorldTime * 24);
        int minute = (int) (normalizedWorldTime * 60 * 24) - (hour * 60);

        String ampm = "AM";

        if(hour >= 12){
            hour -= 12;
            ampm = "PM";
        }

        return String.format("%02d", hour)+":"+String.format("%02d", minute)+" "+ampm;
    }

    public void render(Renderer renderer){

        for(Entity entity : entities){
            entity.render(renderer);
        }


        terrain.render(renderer);

        terrain.renderTransparent(renderer);

    }

}
