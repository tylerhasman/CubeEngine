package me.cube.engine.game;

import me.cube.engine.Voxel;
import me.cube.engine.VoxelModel;
import me.cube.engine.file.Assets;

public class Player extends Entity {

    public Player(World world) {
        super(world);

        VoxelModel torsoModel = Assets.loadModel("torso.vox");
        VoxelModel handModel = Assets.loadModel("hand.vox");
        VoxelModel footModel = Assets.loadModel("foot.vox");
        VoxelModel headModel = Assets.loadModel("head.vox");

        Voxel torso = new Voxel(torsoModel);

        Voxel head = new Voxel(headModel);
        head.position.y = 10;

        Voxel leftHand = new Voxel(handModel);
        leftHand.position.x = -8;

        Voxel rightHand = new Voxel(handModel);
        rightHand.position.x = 8;

        Voxel leftFoot = new Voxel(footModel);
        leftFoot.position.y = -6;
        leftFoot.position.x = -4;
        leftFoot.position.z = -2;

        Voxel rightFoot = new Voxel(footModel);
        rightFoot.position.y = -6;
        rightFoot.position.x = 4;
        rightFoot.position.z = -2;

        torso.position.y += 6;

        root.addChild(torso);

        torso.addChild(head);
        torso.addChild(leftHand);
        torso.addChild(rightHand);
        torso.addChild(leftFoot);
        torso.addChild(rightFoot);

    }

    @Override
    public void update(float delta) {
        super.update(delta);

    }
}
