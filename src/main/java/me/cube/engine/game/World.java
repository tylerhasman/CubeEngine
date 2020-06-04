package me.cube.engine.game;

import me.cube.engine.Terrain;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class World {

    private List<Entity> entities;

    private Player player;

    private Terrain terrain;

    public World(){
        entities = new ArrayList<>();

        player = new Player(this);

        player.position.set(100, 50, 100);

        entities.add(player);

        terrain = new Terrain(20, 10, 20);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public Player getPlayer() {
        return player;
    }

    public void update(float delta){
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

}
