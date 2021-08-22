package me.cube.engine.game.entity;

import me.cube.engine.Transform;
import me.cube.engine.Voxel;
import me.cube.engine.file.Assets;
import me.cube.engine.game.particle.CubeParticle;
import me.cube.engine.game.world.World;
import me.cube.engine.game.animation.*;
import me.cube.engine.game.particle.WeaponSwooshParticle;
import me.cube.engine.model.SimpleVoxelMesh;
import me.cube.engine.shader.Material;
import me.cube.engine.util.MathUtil;
import org.joml.*;
import org.joml.Math;

import java.awt.*;

public abstract class LivingEntity extends Entity {

    private static final int ANIMATION_LAYER_BASE = 0;
    private static final int ANIMATION_LAYER_HAND = 1;

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

    private float particleKickupTimer;

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
        particleKickupTimer = 0;
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

        if(rollTime > 0){
            rollTime -= delta;
            Voxel torso = root.getChild("torso");
            Vector3f axis = new Vector3f(1, 0, -0.2f).normalize();
            torso.getTransform().setRotation(new Quaternionf().identity()).rotateAxis(MathUtil.PI2 * rollTime * (1f / 0.4f), axis.x, axis.y, axis.z);
        }else{
            rollTime = 0;
        }

        attackTime -= delta * attackSpeed;

        if(isOnGround() && velocity.length() > 0){
            particleKickupTimer -= delta;
            if(particleKickupTimer < 0){
                particleKickupTimer = 0.125f;

                Vector3f hit = getWorld().getTerrain().rayTrace(position, new Vector3f(0, -1, 0), 1.5f);

                int color = getWorld().getTerrain().getCube((int) Math.floor(hit.x), (int) hit.y-1, (int) Math.floor(hit.z));

                if(color != 0){
                    Color c = new Color(color);

                    Random random = new Random();

                    float scale = 2f + random.nextFloat() * 1.5f;
                    float velocityModX = random.nextFloat() * 1.5f + 1.5f;
                    float velocityModZ = random.nextFloat() * 1.5f + 1.5f;
                    float velY = random.nextFloat() * 3 + 2f;

                    CubeParticle particle = new CubeParticle(1, position, new Vector3f(scale), new Quaternionf(), new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f));

                    Vector3f direction = velocity.normalize(new Vector3f());

                    direction.rotateAxis(random.nextFloat() * 90 - 45, 0, 1, 0);

                    particle.velocity.set(-direction.x * velocityModX, velY, -direction.z * velocityModZ);

                    getWorld().getParticleEngine().addParticle(particle);
                }

            }
        }

    }

    public void attack(){
        if(attackTime <= 0f && rollTime <= 0f){
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
        head.getTransform().translate(0, 1, 0);

        Voxel leftHand = new Voxel("left-hand", handModel);
        leftHand.getTransform().translate(-0.8f, 0, 0);
        //leftHand.addChild(shield);

        Voxel rightHand = new Voxel("right-hand", handModel);
        rightHand.getTransform().translate(0.8f, 0, 0);

        Voxel weapon = new Voxel("weapon", swordModel);

        weapon.getTransform().setParent(rightHand.getTransform());

        Voxel leftFoot = new Voxel("left-foot", footModel);

        leftFoot.getTransform().translate(-0.4f, -0.6f, 0.1f);

        Voxel rightFoot = new Voxel("right-foot", footModel);
        rightFoot.getTransform().translate(0.4f, -0.6f, 0.1f);

        torso.getTransform().translate(0, 0.95f, 0);

        root.getTransform().addChild(torso.getTransform());

        torso.getTransform().addChild(head.getTransform());
        torso.getTransform().addChild(leftHand.getTransform());
        torso.getTransform().addChild(rightHand.getTransform());
        torso.getTransform().addChild(leftFoot.getTransform());
        torso.getTransform().addChild(rightFoot.getTransform());
    }
    
}
