package me.cube.engine.game.entity;

import me.cube.engine.Voxel;
import me.cube.engine.file.Assets;
import me.cube.engine.game.world.World;

public class Flora extends Entity {
    public Flora(World world) {
        super(world);
        gravity = 0f;
        root.getTransform().addChild(new Voxel("flower", Assets.loadModel("flower.vxm")).getTransform());
    }
}
