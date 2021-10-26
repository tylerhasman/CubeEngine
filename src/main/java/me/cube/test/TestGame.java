package me.cube.test;

import me.cube.engine.Camera;
import me.cube.engine.Game;
import me.cube.engine.Renderer;
import me.cube.engine.Voxel;
import me.cube.engine.file.Assets;
import me.cube.engine.model.SimpleVoxelMesh;
import me.cube.game.world.Terrain;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TestGame extends Game {

    private Voxel testVoxel;

    @Override
    public void init() {

        int[][][] cubes = new int[1][1][1];
        cubes[0][0][0] = 0xFF_FFFF00;

        testVoxel = new Voxel("Test", new SimpleVoxelMesh(cubes, 1, 1, 1, new Vector3f(0.5f, 0.5f, 0.5f)));
        testVoxel.position.set(0, 0, 0);


        Camera.projectionMatrix = new Matrix4f()
                .perspective(Math.toRadians(90f), 1280f / 720f, 0.5f, 5000);

    }

    @Override
    public void update(float delta) {

        testVoxel.position.set(1, 0, 1);
        testVoxel.rotation.rotateAxis(0.01f, 0, 1, 0);

        Camera.cameraMatrix.identity().lookAt(4, 4, 4,
                0, 0, 0,
                0, 1, 0);

    }

    @Override
    public void render(Renderer renderer) {
        renderer.render(testVoxel);
    }

    @Override
    public void onKeyPress(int key, int action) {

    }

    @Override
    public void onCursorMove(double dx, double dy) {

    }

    @Override
    public void onMouseScroll(double delta) {

    }

    @Override
    public void onMousePress(int button, int action) {

    }

    @Override
    public void destroy() {
        Assets.disposeAll();
    }

    @Override
    public String getTitle() {
        return "Test";
    }
}
