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
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.*;

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

        terrain = new Terrain(10, "test");

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

            //renderBoundingBox(entity);

        }

        glDisable(GL_BLEND);

        glEnable(GL_CULL_FACE);

        glCullFace(GL_FRONT);

        terrain.render();

        glDisable(GL_CULL_FACE);

//        particleEngine.render();

    }

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

}
