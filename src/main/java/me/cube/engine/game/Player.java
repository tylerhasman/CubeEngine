package me.cube.engine.game;

import me.cube.engine.Voxel;
import me.cube.engine.VoxelModel;
import me.cube.engine.file.Assets;
import org.joml.Math;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import static me.cube.engine.game.Input.*;

public class Player extends Entity {

    private Voxel leftFoot, rightFoot, sword, rightHand, leftHand;

    public Player(World world) {
        super(world);

        VoxelModel torsoModel = Assets.loadModel("torso.vox");
        VoxelModel handModel = Assets.loadModel("hand.vox");
        VoxelModel footModel = Assets.loadModel("foot.vox");
        VoxelModel headModel = Assets.loadModel("head.vox");
        VoxelModel swordModel = Assets.loadModel("BowTest.vxm");

        Voxel torso = new Voxel(torsoModel);

        Voxel head = new Voxel(headModel);
        head.position.y = 10;

        leftHand = new Voxel(handModel);
        leftHand.position.x = -8;

        rightHand = new Voxel(handModel);
        rightHand.position.x = 8;

        sword = new Voxel(swordModel);
        sword.position.y = 10;
        sword.position.z = -2.5f;
        sword.origin.y = 10;
        sword.scale.set(0.4f);

        rightHand.addChild(sword);

        leftFoot = new Voxel(footModel);
        leftFoot.position.y = -6;
        leftFoot.position.x = -4;
        leftFoot.position.z = 1;

        rightFoot = new Voxel(footModel);
        rightFoot.position.y = -6;
        rightFoot.position.x = 4;
        rightFoot.position.z = 1;

        torso.position.y += 9.5f;

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

        Vector3f forward = CubeGame.game.getCameraForward().normalize();//TODO: When you aim the camera more downwards you move slower, fix this
        forward.mul(-1f).mul(100f);//runspeed

        Vector3f right = new Vector3f();

        forward.rotateAxis(Math.toRadians(-90f), 0, 1, 0, right);

        velocity.x = 0;
        velocity.z = 0;

        float targetYaw = 0;
        boolean walking = false;

        if(Input.isActionActive(ACTION_FORWARD)){
            velocity.x = forward.x;
            velocity.z = forward.z;
            rotation.identity().rotateAxis(Math.toRadians(CubeGame.game.getYaw()), 0, 1, 0);
            walking = true;
        }

        if(Input.isActionActive(ACTION_BACK)){
            velocity.x = -forward.x;
            velocity.z = -forward.z;
            rotation.identity().rotateAxis(Math.toRadians(CubeGame.game.getYaw() + 180), 0, 1, 0);
            walking = true;
        }

        if(Input.isActionActive(ACTION_LEFT)){
            velocity.x += -right.x;
            velocity.z += -right.z;
            rotation.identity().rotateAxis(Math.toRadians(CubeGame.game.getYaw() + 90), 0, 1, 0);
            walking = true;
        }

        if(Input.isActionActive(ACTION_RIGHT)){
            velocity.x += right.x;
            velocity.z += right.z;
            rotation.identity().rotateAxis(Math.toRadians(CubeGame.game.getYaw() - 90), 0, 1, 0);
            walking = true;
        }

        if(walking){
            leftFoot.rotation.identity().rotateAxis(Math.sin(life * 10) * (float) Math.PI * 0.5f, 1, 0, 0);
            rightFoot.rotation.identity().rotateAxis(Math.sin(-life * 10) * (float) Math.PI * 0.5f, 1, 0, 0);
            sword.rotation.identity().rotateAxis(Math.toRadians(180), 0, 1, 0).rotateAxis(Math.sin(-life * 10) * (float) Math.PI * 0.1f, 1, 0, 0);
        }
        sword.origin.y = 10;

    }
}
