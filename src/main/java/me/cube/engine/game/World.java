package me.cube.engine.game;

import me.cube.engine.Terrain;
import me.cube.engine.util.MathUtil;
import org.joml.AABBf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class World {

    private List<Entity> entities;

    private Player player;

    private Terrain terrain;

    public World(){
        entities = new ArrayList<>();

        player = new Player(this);

        player.position.set(100, 100, 100);

        entities.add(player);

        terrain = new Terrain(40, 10, 40);

        Random random = new Random();

        for(int i = 0; i < 6;i++){
            Flora flora = new Flora(this);
            flora.rotation.rotateAxis(random.nextFloat() * MathUtil.PI2, 0, 1, 0);
            flora.position.add(random.nextFloat() * 40 * 10, 15, random.nextFloat() * 40 * 10);
            entities.add(flora);
        }

    }

    public Terrain getTerrain() {
        return terrain;
    }

    public Player getPlayer() {
        return player;
    }

    public void update(float delta){
        for(Entity entity : entities){
            entity.updatePhysics(delta);
        }
        for(Entity entity : entities){
            entity.update(delta);
        }
    }

    public void render(){
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);

        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);

        glLightfv(GL_LIGHT0, GL_AMBIENT, new float[] {0.3f, 0.3f, 0.3f, 1f});
        glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[] {0, 0, 0, 0});
        glLightfv(GL_LIGHT0, GL_POSITION, new float[] {0, 100, 0, 1});

        glBegin(GL_QUADS);

        for(Entity entity : entities){
            entity.root.render();
        }

        glEnd();

        glEnable(GL_CULL_FACE);

        glCullFace(GL_FRONT);

        glBegin(GL_QUADS);

        terrain.render();

        glEnd();

        glDisable(GL_CULL_FACE);

        glDisable(GL_LIGHT0);
        glDisable(GL_LIGHTING);
        glDisable(GL_COLOR_MATERIAL);

    }

    private void renderHitboxes(){
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
