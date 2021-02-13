package me.cube.engine.game.entity;

import me.cube.engine.Transform;
import me.cube.engine.Voxel;
import me.cube.engine.file.Assets;
import me.cube.engine.game.world.World;
import me.cube.engine.game.animation.*;
import me.cube.engine.game.particle.WeaponSwooshParticle;
import me.cube.engine.model.SimpleVoxelMesh;
import me.cube.engine.shader.Material;
import me.cube.engine.util.MathUtil;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class LivingEntity extends Entity {

    private static final int ANIMATION_LAYER_BASE = 0;
    private static final int ANIMATION_LAYER_HAND = 1;

    private AnimationController animationController;

    private float maxMoveSpeed;
    private float yaw, roll;

    private float attackTime;

    private float attackSpeed;

    private boolean weaponOut;
    private float weaponPutAwayTime;
    private float rollTime;

    private boolean blocking;

    private WeaponSwooshParticle lastSwooshParticle;

    private float movingSpeed;
    private final Vector2f desiredDirection = new Vector2f();

    public LivingEntity(World world) {
        super(world);
        maxMoveSpeed = 90f;
        attackSpeed = 2f;
        initAppearance();
        initAnimations();
        yaw = 0f;
        weaponOut = true;
        putAwayWeapon();
        attackTime = 0f;
        roll = 0;
        physics = true;
        life = 20;
    }

    public boolean isDead(){
        return life <= 0;
    }

    public void setMaxMoveSpeed(float maxMoveSpeed) {
        this.maxMoveSpeed = maxMoveSpeed;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public void roll(){
        if(attackTime <= 0f && rollTime <= 0f){
            rollTime = 0.4f;
            putAwayWeapon();
        }
    }

    public void walk(float dirX, float dirZ, float acceleration){

        float actualMoveSpeed = maxMoveSpeed;

        if(attackTime > 0f){
            actualMoveSpeed *= 0.85f;
        }

        if(dirX == 0 && dirZ == 0){
            movingSpeed = MathUtil.moveValueTo(movingSpeed, 0, acceleration);
        }else{
            desiredDirection.x = MathUtil.moveValueTo(desiredDirection.x, dirX, actualMoveSpeed);
            desiredDirection.y = MathUtil.moveValueTo(desiredDirection.y, dirZ, actualMoveSpeed);
            movingSpeed = MathUtil.moveValueTo(movingSpeed, actualMoveSpeed, acceleration);
        }

        velocity.x = desiredDirection.x * movingSpeed;
        velocity.z = desiredDirection.y * movingSpeed;

    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if(position.y < -500){
            position.set(100, 100, 100);
        }

        if(rollTime > 0f){
            animationController.setActiveAnimation(ANIMATION_LAYER_BASE, "rolling");
        }else if(isOnGround()){
            if((Math.abs(velocity.x) > 0 || Math.abs(velocity.z) > 0)){
                animationController.transitionAnimation(ANIMATION_LAYER_BASE, "walking");
            }else{
                animationController.transitionAnimation(ANIMATION_LAYER_BASE, "idle");
            }
        }else{
            animationController.setActiveAnimation(ANIMATION_LAYER_BASE, "falling");
        }

        rotation.identity();

        if(velocity.x != 0 || velocity.z != 0){
            float targetYaw = (float) (Math.atan2(velocity.x, velocity.z) + Math.PI);
            yaw = MathUtil.moveAngleTowards(yaw, targetYaw, delta * 20);
/*

            float angleDif = MathUtil.angleDifferenceRad(targetYaw, yaw);
            if(Math.abs(angleDif) > 0.05f){//Epsilon
                if(angleDif > 0){
                    roll = MathUtil.moveValueTo(roll, Math.toRadians(-10), delta * 5f);
                }else if(targetYaw % MathUtil.PI > yaw % MathUtil.PI){
                    roll = MathUtil.moveValueTo(roll, Math.toRadians(10), delta * 5f);
                }
            }else{
                roll = MathUtil.moveValueTo(roll, 0f, delta * 5f);
            }*/

        }else{
            //roll = MathUtil.moveValueTo(roll, 0f, delta * 5f);
        }

        rotation.rotateAxis(yaw, 0, 1, 0).rotateAxis(roll, 0, 0, 1);

        animationController.update(delta);

        if(weaponOut){
            weaponPutAwayTime -= delta;
            if(weaponPutAwayTime <= 0){
                putAwayWeapon();
            }
            animationController.setLayerWeight(ANIMATION_LAYER_BASE, Avatar.BodyPart.LeftHand, 0.1f);
            animationController.setLayerWeight(ANIMATION_LAYER_BASE, Avatar.BodyPart.RightHand, 0.1f);
        }else{
            animationController.setLayerWeight(ANIMATION_LAYER_BASE, Avatar.BodyPart.LeftHand, 1);
            animationController.setLayerWeight(ANIMATION_LAYER_BASE, Avatar.BodyPart.RightHand, 1);
            animationController.transitionAnimation(ANIMATION_LAYER_HAND, "idle");
        }

        if(rollTime > 0){
            rollTime -= delta;
            Voxel torso = root.getChild("torso");
            torso.getTransform().rotateAxis(MathUtil.PI2 * rollTime * (1f / 0.4f), 1, 0, -0.2f);
        }else{
            rollTime = 0;
        }

        if(animationController.getCurrentAnimation(ANIMATION_LAYER_HAND).equals("swing")){
            if(attackTime > 0.1f && attackTime < 0.9f){

                Voxel weapon = root.getChild("weapon");

                Vector3f top = new Vector3f(0, weapon.model.height / 2f, 0);
                Vector3f bottom = new Vector3f(0, 0, 0);

                Transform transform = weapon.getTransform();

                transform.transformPosition(top);
                transform.transformPosition(bottom);

                WeaponSwooshParticle particle = new WeaponSwooshParticle(top, bottom, lastSwooshParticle);

                getWorld().getParticleEngine().addParticle(particle);

                lastSwooshParticle = particle;
            }
        }else{
            lastSwooshParticle = null;
        }

        attackTime -= delta * attackSpeed;

        if(attackTime <= 0){
            animationController.setActiveAnimation(ANIMATION_LAYER_HAND, "prone");
        }

        if(!animationController.getCurrentAnimation(ANIMATION_LAYER_HAND).equals("swing")){
            if(blocking){
                animationController.transitionAnimation(ANIMATION_LAYER_HAND, "block");
            }else if(weaponOut){
                animationController.transitionAnimation(ANIMATION_LAYER_HAND, "prone");
            }
        }

    }

    public void attack(){
        if(attackTime <= 0f && rollTime <= 0f){
            animationController.setActiveAnimation(ANIMATION_LAYER_HAND, "swing");

            weaponPutAwayTime = 10f;
            attackTime = 1f;
        }
    }

    public void putAwayWeapon(){
        if(weaponOut){
            Voxel weapon = root.getChild("weapon");
            Voxel torso = root.getChild("torso");
            Voxel shield = root.getChild("shield");
            if(weapon != null){

                weapon.getTransform().setParent(torso.getTransform());
                weapon.getTransform().setLocalPosition(6, 10, 6);
                //weapon.getTransform().rot

/*                weapon.position.z = 6;
                weapon.position.x = 6;
                weapon.position.y = 10;
                weapon.rotation.identity();
                weapon.rotation.rotateAxis(Math.toRadians(90f), 0, 1, 0);
                weapon.rotation.rotateAxis(Math.toRadians(180f + 45f), 1, 0, 0);*/
            }
            if(shield != null){

                shield.getTransform().setParent(torso.getTransform());
                shield.getTransform().setLocalPosition(0, 5, 6);
                shield.getTransform().rotateAxis(Math.toRadians(90f), 0, 1, 0);
                shield.getTransform().rotateAxis(Math.toRadians(180f + 45f), 1, 0, 0);
            }
            weaponPutAwayTime = 0f;
            weaponOut = false;
        }
    }

    public void takeOutWeapon(){
        if(!weaponOut){
            Voxel weapon = root.getChild("weapon");
            Voxel shield = root.getChild("shield");
            Voxel rightHand = root.getChild("right-hand");
            Voxel leftHand = root.getChild("left-hand");
            if(weapon != null){

                weapon.getTransform().setParent(rightHand.getTransform());
                weapon.getTransform().setLocalPosition(0, 0, 0);
            }

            if(shield != null){
                shield.getTransform().setParent(leftHand.getTransform());
                shield.getTransform().setLocalPosition(0, 0, 0);
            }

            weaponPutAwayTime = 10f;
            weaponOut = true;
        }
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

        animationController.addAnimation(ANIMATION_LAYER_BASE, "idle", new IdleAnimation().setLooping(true));
        animationController.addAnimation(ANIMATION_LAYER_BASE, "walking", new WalkingAnimation().setLooping(true));
        animationController.addAnimation(ANIMATION_LAYER_BASE, "falling", new FallingAnimation().setLooping(false));
        animationController.addAnimation(ANIMATION_LAYER_BASE, "rolling", new RollingAnimation().setTransitionSpeedBack(5f).setLooping(false));

        animationController.addAnimation(ANIMATION_LAYER_HAND, "prone", new WeaponProneAnimation().setLooping(true));
        animationController.addAnimation(ANIMATION_LAYER_HAND, "swing", new SwordSlashAnimation().setSpeed(attackSpeed).setTransitionSpeedBack(1f / 2f).setFadeOnFinish("prone").setLooping(true));
        animationController.addAnimation(ANIMATION_LAYER_HAND, "block", new ShieldBlockAnimation().setLooping(false));

    }

    private void initAppearance(){
/*        VoxelModel torsoModel = Assets.loadModel("player_body_template.vxm");
        VoxelModel handModel = Assets.loadModel("player_hand_template.vxm");
        VoxelModel footModel = Assets.loadModel("player_leg_template.vxm");
        VoxelModel headModel = Assets.loadModel("player_head_template.vxm");*/
        SimpleVoxelMesh torsoModel = Assets.loadModel("torso.vox");
        SimpleVoxelMesh handModel = Assets.loadModel("hand.vox");
        SimpleVoxelMesh footModel = Assets.loadModel("foot.vox");
        SimpleVoxelMesh headModel = Assets.loadModel("head.vox");
        SimpleVoxelMesh swordModel = Assets.loadModel("sword.vxm");

        Voxel torso = new Voxel("torso", torsoModel);

        Voxel head = new Voxel("head", headModel);
        head.getTransform().translate(0, 10, 0);

        Voxel leftHand = new Voxel("left-hand", handModel);
        leftHand.getTransform().translate(-8, 0, 0);
        //leftHand.addChild(shield);

        Voxel rightHand = new Voxel("right-hand", handModel);
        rightHand.getTransform().translate(8, 0, 0);

        Voxel weapon = new Voxel("weapon", swordModel);

        weapon.getTransform().setParent(rightHand.getTransform());

        Voxel leftFoot = new Voxel("left-foot", footModel);

        leftFoot.getTransform().translate(-4, -6, 1);

        Voxel rightFoot = new Voxel("right-foot", footModel);
        rightFoot.getTransform().translate(4, -6, 1);

        torso.getTransform().translate(0, 9.5f, 0);

        root.getTransform().addChild(torso.getTransform());

        torso.getTransform().addChild(head.getTransform());
        torso.getTransform().addChild(leftHand.getTransform());
        torso.getTransform().addChild(rightHand.getTransform());
        torso.getTransform().addChild(leftFoot.getTransform());
        torso.getTransform().addChild(rightFoot.getTransform());
    }
    
}
