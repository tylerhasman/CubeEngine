package me.cube.engine.game.world;

import me.cube.engine.Camera;
import me.cube.engine.Voxel;
import me.cube.engine.file.Assets;
import me.cube.engine.game.entity.*;
import me.cube.engine.game.particle.ParticleEngine;
import me.cube.engine.model.Mesh;
import me.cube.engine.model.VoxelMesh;
import me.cube.engine.shader.Material;
import me.cube.engine.util.MathUtil;
import org.joml.AABBf;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.CallbackI;

import java.util.*;

import static me.cube.engine.game.world.Chunk.CHUNK_HEIGHT;
import static org.lwjgl.opengl.GL11.*;

public class World {

    private static final float ONE_DAY = 24 * 60;//Seconds

    public static final float WORLD_SCALE = 10f;

    private List<Entity> entities;

    private Terrain terrain;

    private ParticleEngine particleEngine;

    private float worldTime;

    public World(){
        entities = new ArrayList<>();
        particleEngine = new ParticleEngine(2000);

        terrain = new Terrain(10, "test");

        worldTime = ONE_DAY / 2f;
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

        worldTime += delta;
        if(worldTime >= ONE_DAY){
            worldTime -= ONE_DAY;
        }

        //worldTime = 60 * 12;

        terrain.updateTerrain(delta, new Vector3f(fromPosition));
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

    private Vector3f applyAmbientLighting(Vector3f ambientLight){

        final Vector3f moonLight = new Vector3f(46f,68f,130f).mul(1f / 255f);

        float normalizedWorldTime = worldTime / (24f * 60f);
        float sunElevation = Math.sin(normalizedWorldTime * MathUtil.PI2 - MathUtil.PI / 2f);
        float moonElevation = Math.sin(normalizedWorldTime * MathUtil.PI2 + MathUtil.PI / 2.4f);

        if(sunElevation < 0.1f) {
            sunElevation = 0.1f;
        }

        if(moonElevation < 0){
            moonElevation = 0f;
        }

        ambientLight.mul(sunElevation);

        ambientLight.lerp(moonLight, moonElevation);

        return ambientLight;
    }

    private Vector3f getSunMoonPosition(){
        float normalizedWorldTime = worldTime / (24f * 60f);

        float sunX = Math.cos(normalizedWorldTime * MathUtil.PI2 - MathUtil.PI / 2f);
        float sunY = Math.sin(normalizedWorldTime * MathUtil.PI2 - MathUtil.PI / 2f);

        //float moonElevation = Math.sin(normalizedWorldTime * MathUtil.PI2 + MathUtil.PI / 2.4f);

        return new Vector3f(sunX, sunY, 0);
    }

    public void render(){

        Vector3f ambientColor = applyAmbientLighting(new Vector3f(1f, 1f, 1f));
        Vector3f skyColor = applyAmbientLighting(new Vector3f(135 / 255f,206 / 255f,235 / 255f));
        Vector3f sunPosition = getSunMoonPosition();

        Vector3f sunDirection = sunPosition.normalize(new Vector3f());

        glClearColor(skyColor.x, skyColor.y, skyColor.z, 1f);
        glClear(GL_COLOR_BUFFER_BIT);

        for(Entity entity : entities){

            entity.root.getTransform().set(entity.position, entity.rotation, entity.scale);
            if(!entity.root.getTransform().hasParent()){
                entity.root.material.setUniform3f("u_AmbientLight", ambientColor);
                entity.root.material.setUniform3f("u_LightDirection", sunDirection);
                entity.root.material.setUniform3f("u_LightColor", ambientColor);
                entity.root.render();
            }

            //renderBoundingBox(entity);

        }

        glDisable(GL_BLEND);

        glEnable(GL_CULL_FACE);

        glCullFace(GL_FRONT);

        terrain.render();

        glDisable(GL_CULL_FACE);

//        particleEngine.render();

    }
/*

    private void renderBoundingBox(Entity entity){
        AABBf bb = entity.boundingBox;

        Voxel point = new Voxel("BB", Assets.loadModel("red_fruit.vxm"));

        point.scale.set(0.5f);

        float width = bb.maxX - bb.minX;
        float height = bb.maxY - bb.minY;
        float length = bb.maxZ - bb.minZ;

        point.position.set(bb.minX, bb.minY, bb.minZ);
        point.render();

        point.position.set(bb.minX, bb.minY + height, bb.minZ);
        point.render();

        point.position.set(bb.minX + width, bb.minY, bb.minZ);
        point.render();

        point.position.set(bb.minX + width, bb.minY + height, bb.minZ);
        point.render();

        point.position.set(bb.minX + width, bb.minY, bb.minZ + length);
        point.render();

        point.position.set(bb.minX + width, bb.minY + height, bb.minZ + length);
        point.render();

        point.position.set(bb.maxX, bb.maxY, bb.maxZ);
        point.render();

        point.position.set(bb.minX, bb.minY + height, bb.minZ + length);
        point.render();

        point.position.set(bb.minX, bb.minY, bb.minZ + length);
        point.render();

    }
*/

}
