package me.cube.engine.game.entity;

import me.cube.engine.Voxel;
import me.cube.engine.file.Assets;
import me.cube.engine.file.CubeFont;
import me.cube.engine.game.world.World;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class Nameplate extends Entity {

    private Voxel nameObject;

    public Nameplate(World world, Player player) {
        super(world);

        String name = "Player 1";

        root.getTransform().setParent(player.root.getTransform());

        CubeFont cubeFont = Assets.loadFont("assets/fonts/Minecraft.ttf");

        nameObject = cubeFont.generate(name);

        nameObject.getTransform().setParent(root.getTransform());

    }

    @Override
    public void update(float delta) {
        super.update(delta);

        Matrix4f transformation = root.getTransform().getParent().getTransformation();

        Quaternionf rotation = transformation.getNormalizedRotation(new Quaternionf());

        root.getTransform().setRotation(rotation);

        position.y = 30;

    }
}
