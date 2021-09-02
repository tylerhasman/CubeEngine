package me.cube.game.particle;

import me.cube.engine.Transform;
import me.cube.engine.Voxel;
import me.cube.engine.file.Assets;
import me.cube.engine.model.SimpleVoxelMesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CubeParticle extends Particle {

    private static SimpleVoxelMesh cube = null;

    private Voxel voxel;

    public Vector3f velocity;

    public CubeParticle(float life, Vector3f position, Vector3f scale, Quaternionf rotation, Vector3f color) {
        super(life);
        if(cube == null){
            generateMesh();
        }
        material.setUniform3f("u_Hue", color);
        voxel = new Voxel("Particle", cube, material);
        voxel.getTransform().set(position, rotation, scale);
        velocity = new Vector3f(0, 0, 0);
    }

    public Transform getTransform(){
        return voxel.getTransform();
    }

    @Override
    public void render() {
        voxel.render();
    }

    private void generateMesh(){

        int[][][] cubes = new int[1][1][1];
        cubes[0][0][0] = 0xFFFFFFFF;

        cube = new SimpleVoxelMesh(cubes, 1, 1, 1);

        Assets.registerModel(cube);

    }

    @Override
    public void update(float delta) {
        super.update(delta);
        velocity.add(0, -20 * delta, 0);

        Vector3f position = voxel.getTransform().getLocalPosition();

        position.add(velocity.mul(delta, new Vector3f()));

        voxel.getTransform().setLocalPosition(position.x, position.y, position.z);

        voxel.getTransform().rotateAxis(delta, 1, 1, 1);
    }
}
