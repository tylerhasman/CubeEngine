package me.cube.engine.game.entity;

import me.cube.engine.Voxel;
import me.cube.engine.VoxelModel;
import me.cube.engine.file.Assets;
import me.cube.engine.game.World;
import me.cube.engine.game.animation.*;
import me.cube.engine.game.particle.WeaponSwooshParticle;
import me.cube.engine.util.MathUtil;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class LivingEntity extends Entity {

    private static final int ANIMATION_LAYER_BASE = 0;
    private static final int ANIMATION_LAYER_HAND = 1;

    private AnimationController animationController;

    private float moveSpeed;
    private float yaw, roll;

    private float attackTime;

    private boolean weaponOut;
    private float weaponPutAwayTime;
    private boolean bufferAttack;//If true the player has 'buffered' an attack and another attack will happen as soon as possible
    private float rollTime;

    private boolean blocking;

    private WeaponSwooshParticle lastSwooshParticle;

    public LivingEntity(World world) {
        super(world);
        moveSpeed = 90f;
        initAppearance();
        initAnimations();
        yaw = 0f;
        weaponOut = true;
        putAwayWeapon();
        attackTime = 0f;
        roll = 0;
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
        velocity.x = MathUtil.moveValueTo(velocity.x, dirX * moveSpeed, acceleration);
        velocity.z = MathUtil.moveValueTo(velocity.z, dirZ * moveSpeed, acceleration);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

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
            animationController.setLayerWeight(ANIMATION_LAYER_BASE, Avatar.BodyPart.LeftHand, -0.25f);
            animationController.setLayerWeight(ANIMATION_LAYER_BASE, Avatar.BodyPart.RightHand, 0.25f);
        }else{
            animationController.setLayerWeight(ANIMATION_LAYER_BASE, Avatar.BodyPart.LeftHand, 1);
            animationController.setLayerWeight(ANIMATION_LAYER_BASE, Avatar.BodyPart.RightHand, 1);
            animationController.transitionAnimation(ANIMATION_LAYER_HAND, "idle");
        }

        if(rollTime > 0){
            rollTime -= delta;
            Voxel torso = root.getChild("torso");
            torso.rotation.rotateAxis(MathUtil.PI2 * rollTime * (1f / 0.4f), 1, 0, -0.2f);
        }else{
            rollTime = 0;
        }

        if(animationController.getCurrentAnimation(ANIMATION_LAYER_HAND).equals("swing")){
            if(attackTime > 0.2f && attackTime < 0.6f){

                Voxel weapon = root.getChild("weapon");

                Vector3f top = new Vector3f(0, weapon.model.height / 2f, 0);
                Vector3f bottom = new Vector3f(0, 0, 0);

                Matrix4f transform = weapon.getTransform();

                transform.transformPosition(top);
                transform.transformPosition(bottom);

                WeaponSwooshParticle particle = new WeaponSwooshParticle(top, bottom, lastSwooshParticle);

                getWorld().getParticleEngine().addParticle(particle);

                lastSwooshParticle = particle;
            }
        }else{
            lastSwooshParticle = null;
        }

        if(attackTime <= 0.2f && bufferAttack){
            attack();
        }

        attackTime -= delta;

        if(!animationController.getCurrentAnimation(ANIMATION_LAYER_HAND).equals("swing")){
            if(blocking){
                animationController.transitionAnimation(ANIMATION_LAYER_HAND, "block");
            }else if(weaponOut){
                animationController.transitionAnimation(ANIMATION_LAYER_HAND, "prone");
            }
        }

    }

    public void attack(){
        if(attackTime <= 0.2f && rollTime <= 0f){
            animationController.setActiveAnimation(ANIMATION_LAYER_HAND, "swing");

            weaponPutAwayTime = 10f;
            attackTime = 0.8f;
            bufferAttack = false;
        }else if(attackTime <= 0.4f){
            bufferAttack = true;
        }
    }

    public void putAwayWeapon(){
        if(weaponOut){
            Voxel weapon = root.getChild("weapon");
            Voxel torso = root.getChild("torso");
            Voxel shield = root.getChild("shield");
            if(weapon != null){
                root.removeChild("weapon");
                torso.addChild(weapon);
                weapon.position.z = 6;
                weapon.position.x = 6;
                weapon.position.y = 10;
                weapon.rotation.identity();
                weapon.rotation.rotateAxis(Math.toRadians(90f), 0, 1, 0);
                weapon.rotation.rotateAxis(Math.toRadians(180f + 45f), 1, 0, 0);
            }
            if(shield != null){
                root.removeChild("shield");
                torso.addChild(shield);
                shield.position.z = 6;
                shield.position.x = 0;
                shield.position.y = 5;
                shield.rotation.identity();
                shield.rotation.rotateAxis(Math.toRadians(90f), 0, 1, 0);
                shield.rotation.rotateAxis(Math.toRadians(180f + 45f), 1, 0, 0);
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
                root.removeChild("weapon");
                rightHand.addChild(weapon);
                weapon.position.z = 0;
                weapon.position.x = 0;
                weapon.position.y = 0;
                weapon.rotation.identity();
            }

            if(shield != null){
                root.removeChild("shield");
                leftHand.addChild(shield);
                shield.position.x = 0;
                shield.position.y = 0;
                shield.position.z = 0;
                shield.rotation.identity();
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

        animationController.addAnimation(ANIMATION_LAYER_BASE, "idle", new IdleAnimation());
        animationController.addAnimation(ANIMATION_LAYER_BASE, "walking", new WalkingAnimation());
        animationController.addAnimation(ANIMATION_LAYER_BASE, "falling", new FallingAnimation());
        animationController.addAnimation(ANIMATION_LAYER_BASE, "rolling", new RollingAnimation().setTransitionSpeedBack(5f));

        animationController.addAnimation(ANIMATION_LAYER_HAND, "prone", new WeaponProneAnimation());
        animationController.addAnimation(ANIMATION_LAYER_HAND, "swing", new SwordSlashAnimation().setSpeed(9f).setFadeOnFinish("prone"));
        animationController.addAnimation(ANIMATION_LAYER_HAND, "block", new ShieldBlockAnimation());

    }

    private void initAppearance(){
/*        VoxelModel torsoModel = Assets.loadModel("player_body_template.vxm");
        VoxelModel handModel = Assets.loadModel("player_hand_template.vxm");
        VoxelModel footModel = Assets.loadModel("player_leg_template.vxm");
        VoxelModel headModel = Assets.loadModel("player_head_template.vxm");*/
        VoxelModel torsoModel = Assets.loadModel("torso.vox");
        VoxelModel handModel = Assets.loadModel("hand.vox");
        VoxelModel footModel = Assets.loadModel("foot.vox");
        VoxelModel headModel = Assets.loadModel("head.vox");
        VoxelModel swordModel = Assets.loadModel("sword.vxm");
        VoxelModel shieldModel = Assets.loadModel("WoodenShield.vxm");

        Voxel torso = new Voxel("torso", torsoModel);

        Voxel head = new Voxel("head", headModel);
        head.position.y = 10;

        Voxel leftHand = new Voxel("left-hand", handModel);
        leftHand.position.x = -8;

        Voxel shield = new Voxel("shield", shieldModel);
        shield.scale.set(1f);
        leftHand.addChild(shield);

        Voxel rightHand = new Voxel("right-hand", handModel);
        rightHand.position.x = 8;

        Voxel weapon = new Voxel("weapon", swordModel);
        weapon.scale.set(0.7f);

        rightHand.addChild(weapon);

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
