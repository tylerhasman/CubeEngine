package me.cube.engine.game.world;

import me.cube.engine.game.entity.*;
import me.cube.engine.game.particle.ParticleEngine;
import me.cube.engine.util.MathUtil;
import org.joml.AABBf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.cube.engine.game.world.Chunk.CHUNK_HEIGHT;
import static org.lwjgl.opengl.GL11.*;

public class World {

    public static final float WORLD_SCALE = 10f;

    private List<Entity> entities;

    private Player player;

    private Terrain terrain;

    private ParticleEngine particleEngine;

    public World(){
        entities = new ArrayList<>();
        particleEngine = new ParticleEngine(2000);

        player = new Player(this);

        player.position.set(0, 150, 0);

        entities.add(player);

        terrain = new Terrain(this,10);

    }

    public Terrain getTerrain() {
        return terrain;
    }

    public Player getPlayer() {
        return player;
    }

    public ParticleEngine getParticleEngine() {
        return particleEngine;
    }

    public void addEntity(Entity entity){
        entities.add(entity);
    }

    public void update(float delta){
        for(Entity entity : entities){
            entity.updatePhysics(delta);
        }
        for(Entity entity : entities){
            entity.update(delta);
        }
        terrain.updateTerrain(new Vector3f(player.position));
        particleEngine.update(delta);
    }

    public void render(){

/*        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);

        glLightfv(GL_LIGHT0, GL_AMBIENT, new float[] {0.3f, 0.3f, 0.3f, 1f});
        glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[] {0.3f, 0.3f, 0.3f, 0});
        glLightfv(GL_LIGHT0, GL_POSITION, new float[] {player.position.x, player.position.y + 40, player.position.z, 1});*/

        for(Entity entity : entities){
            entity.root.render();
        }

        glDisable(GL_BLEND);

        glEnable(GL_CULL_FACE);

        glCullFace(GL_FRONT);

        terrain.render();

        glDisable(GL_CULL_FACE);

/*        glDisable(GL_LIGHT0);
        glDisable(GL_LIGHTING);*/

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
