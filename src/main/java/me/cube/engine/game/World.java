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

        entities.add(new Player(this));

        terrain = new Terrain(20, 10, 20);
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

        //glLightfv(GL_LIGHT0, GL_POSITION, new float[] {player.position.x, player.position.y, player.position.z, 1});

        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glLightfv(GL_LIGHT0, GL_AMBIENT, new float[] {0.3f, 0.3f, 0.3f, 1f});


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