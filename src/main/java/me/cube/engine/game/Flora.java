package me.cube.engine.game;

import me.cube.engine.Voxel;
import me.cube.engine.file.Assets;

public class Flora extends Entity{
    public Flora(World world) {
        super(world);
        gravity = 0f;
        root.addChild(new Voxel("flower", Assets.loadModel("flower.vxm")));
    }
}
