package me.cube.engine.game;

import me.cube.engine.Voxel;
import me.cube.engine.VoxelModel;
import me.cube.engine.file.Assets;
import me.cube.engine.game.animation.AnimationController;
import me.cube.engine.game.animation.Avatar;
import me.cube.engine.game.animation.IdleAnimation;
import me.cube.engine.game.animation.WalkingAnimation;
import org.joml.Math;
import org.joml.Vector3f;

public class LivingEntity extends Entity {

    private static final int ANIMATION_LAYER_BASE = 0;

    private AnimationController animationController;

    private float moveSpeed;
    private float yaw;

    public LivingEntity(World world) {
        super(world);
        moveSpeed = 60f;
        initAppearance();
        initAnimations();
        yaw = 0f;
    }

    public void walk(float dirX, float dirZ){
        velocity.x = dirX * moveSpeed;
        velocity.z = dirZ * moveSpeed;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if(isOnGround() && (Math.abs(velocity.x) > 0 || Math.abs(velocity.z) > 0)){
            animationController.setActiveAnimation(ANIMATION_LAYER_BASE, "walking");
        }else{
            animationController.setActiveAnimation(ANIMATION_LAYER_BASE, "idle");
        }

        if(!velocity.equals(0, 0, 0)){
            float targetYaw = (float) (Math.atan2(velocity.x, velocity.z) + Math.PI);

            yaw = targetYaw;
        }


        rotation.identity().rotateAxis(yaw, 0, 1, 0);
        animationController.update(delta);
    }

    private void initAnimations(){
        Avatar avatar = new Avatar.AvatarBuilder()
                .withHead(root.getChild("head"), 1)
                .withTorso(root.getChild("torso"), 1)
                .withLeftHand(root.getChild("left-hand"), 1)
                .withRightHand(root.getChild("right-hand"), 1)
                .withLeftLeg(root.getChild("left-foot"), 1)
                .withRightLeg(root.getChild("right-foot"), 1)
                .build();

        animationController = new AnimationController(avatar);

        animationController.addAnimation(ANIMATION_LAYER_BASE, "idle", new IdleAnimation());
        animationController.addAnimation(ANIMATION_LAYER_BASE, "walking", new WalkingAnimation());

    }

    private void initAppearance(){
        VoxelModel torsoModel = Assets.loadModel("torso.vox");
        VoxelModel handModel = Assets.loadModel("hand.vox");
        VoxelModel footModel = Assets.loadModel("foot.vox");
        VoxelModel headModel = Assets.loadModel("head.vox");

        Voxel torso = new Voxel("torso", torsoModel);

        Voxel head = new Voxel("head", headModel);
        head.position.y = 10;

        Voxel leftHand = new Voxel("left-hand", handModel);
        leftHand.position.x = -8;

        Voxel rightHand = new Voxel("right-hand", handModel);
        rightHand.position.x = 8;

        Voxel leftFoot = new Voxel("left-foot", footModel);
        leftFoot.position.y = -6;
        leftFoot.position.x = -4;
        leftFoot.position.z = 1;

        Voxel rightFoot = new Voxel("right-foot", footModel);
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
    
}
