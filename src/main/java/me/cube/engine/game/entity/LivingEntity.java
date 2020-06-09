package me.cube.engine.game.entity;

import me.cube.engine.Voxel;
import me.cube.engine.game.World;
import me.cube.engine.game.animation.*;
import me.cube.engine.util.MathUtil;
import org.joml.Math;

public abstract class LivingEntity extends Entity {

    protected AnimationController animationController;

    private float moveSpeed;
    private float yaw, roll;

    //private float attackTime;

    //private boolean weaponOut;
    //private float weaponPutAwayTime;
    protected float rollTime;

    //private boolean bufferAttack;

    //private boolean blocking;


    public LivingEntity(World world) {
        super(world);
        moveSpeed = 90f;
        yaw = 0f;
        roll = 0;
    }

    public void roll(){
        rollTime = 0.4f;
    }

    public void walk(float dirX, float dirZ, float acceleration){
        velocity.x = MathUtil.moveValueTo(velocity.x, dirX * moveSpeed, acceleration);
        velocity.z = MathUtil.moveValueTo(velocity.z, dirZ * moveSpeed, acceleration);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        rotation.identity();

        if(velocity.x != 0 || velocity.z != 0){
            float targetYaw = (float) (Math.atan2(velocity.x, velocity.z) + Math.PI);
            yaw = MathUtil.moveAngleTowards(yaw, targetYaw, delta * 20);
        }

        rotation.rotateAxis(yaw, 0, 1, 0).rotateAxis(roll, 0, 0, 1);

        animationController.update(delta);

        if(rollTime > 0){
            rollTime -= delta;
            Voxel torso = root.getChild("torso");
            torso.rotation.rotateAxis(MathUtil.PI2 * rollTime * (1f / 0.4f), 1, 0, -0.2f);
        }else{
            rollTime = 0;
        }

    }

    public void attack(){

    }

/*    public void putAwayWeapon(){
        if(weaponOut){
            Voxel weapon = root.getChild("weapon");
            Voxel torso = root.getChild("torso");
            Voxel shield = root.getChild("shield");
            if(weapon != null){
                root.removeChild("weapon");
                torso.addChild(weapon);

            }
            if(shield != null){
                root.removeChild("shield");
                torso.addChild(shield);

            }
            weaponPutAwayTime = 0f;
            weaponOut = false;
        }
    }*/

/*    public void takeOutWeapon(){
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
    }*/



}
