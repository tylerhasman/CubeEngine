package me.cube.engine.game.world;

import me.cube.engine.Camera;
import me.cube.engine.game.entity.*;
import me.cube.engine.game.particle.ParticleEngine;
import me.cube.engine.util.MathUtil;
import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static me.cube.engine.game.world.Chunk.CHUNK_HEIGHT;
import static org.lwjgl.opengl.GL11.*;

public class World {

    public static final float WORLD_SCALE = 10f;

    private List<Entity> entities;

    private Terrain terrain;

    private ParticleEngine particleEngine;

    public World(){
        entities = new ArrayList<>();
        particleEngine = new ParticleEngine(2000);

        NPC npc = new NPC(this);
        npc.position.set(200, 1000, 0);

        entities.add(npc);

        terrain = new Terrain(10);

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


        terrain.updateTerrain(new Vector3f(fromPosition));
        particleEngine.update(delta);
    }

    public void render(){

        for(Entity entity : entities){
            entity.root.render();
        }

        glDisable(GL_BLEND);

        glEnable(GL_CULL_FACE);

        glCullFace(GL_FRONT);

        terrain.render();

        glDisable(GL_CULL_FACE);

        particleEngine.render();


    }

    public void renderHitboxes(){
        glBegin(GL_LINES);

        glColor3f(1f, 0f, 0f);
        for(Entity entity : entities){
            AABBf boundingBox = entity.boundingBox;

            glVertex3f(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            glVertex3f(boundingBox.minX + (boundingBox.maxX - boundingBox.minX), boundingBox.minY, boundingBox.minZ);

            glVertex3f(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            glVertex3f(boundingBox.minX, boundingBox.minY + (boundingBox.maxY - boundingBox.minY), boundingBox.minZ);

            glVertex3f(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            glVertex3f(boundingBox.minX, boundingBox.minY, boundingBox.minZ + (boundingBox.maxZ - boundingBox.minZ));

            glVertex3f(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            glVertex3f(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);

            glVertex3f(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            glVertex3f(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);

            glVertex3f(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            glVertex3f(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);

            glVertex3f(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            glVertex3f(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);

            glVertex3f(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            glVertex3f(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);

            glVertex3f(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            glVertex3f(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);

            glVertex3f(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            glVertex3f(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);

            glVertex3f(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            glVertex3f(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);

            glVertex3f(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            glVertex3f(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);

            glVertex3f(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            glVertex3f(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);

        }

        glEnd();
    }

}
