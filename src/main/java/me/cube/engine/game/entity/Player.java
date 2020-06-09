package me.cube.engine.game.entity;

import me.cube.engine.game.CubeGame;
import me.cube.engine.game.Input;
import me.cube.engine.game.World;
import me.cube.engine.game.item.Items;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static me.cube.engine.game.Input.*;

public class Player extends Humanoid {

    //After this timer hits zero the player will put away their weapon
    private float weaponPutAwayTimer;

    //If true the player will attack as soon as their action timer hits zero
    private boolean attackBuffer;

    public Player(World world) {
        super(world, "head.vox", "torso.vox", "hand.vox", "foot.vox");
        equipWeapon(MAIN_HAND, Items.WOOD_SWORD);
    }

    @Override
    public boolean isInCombat() {
        return weaponPutAwayTimer > 0f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        weaponPutAwayTimer -= delta;

        Vector3f forward = CubeGame.game.getCameraForward().mul(1, 0, 1).normalize();//TODO: When you aim the camera more downwards you move slower, fix this

        Vector3f right = new Vector3f();

        forward.rotateAxis(Math.toRadians(90f), 0, 1, 0, right);

        Vector2f desiredDirection = new Vector2f();

        if(Input.isActionActive(ACTION_FORWARD)){
            desiredDirection.add(-forward.x, -forward.z);
        }

        if(Input.isActionActive(ACTION_BACK)){
            desiredDirection.add(forward.x, forward.z);
        }

        if(Input.isActionActive(ACTION_RIGHT)){
            desiredDirection.add(right.x, right.z);
        }

        if(Input.isActionActive(ACTION_LEFT)){
            desiredDirection.add(-right.x, -right.z);
        }

        if(Input.isActionActive(ACTION_JUMP)){
            if(isOnGround()){
                velocity.y = 100;
            }
        }

        if(Input.isActionActive(ACTION_ROLL) && !isRolling() && !isAttacking()){
            if(velocity.x != 0 || velocity.z != 0){
                roll();
                weaponPutAwayTimer = 0f;
            }
        }

        if(Input.isActionActive(ACTION_ATTACK_PRIMARY)){
            weaponPutAwayTimer = 10f;
            if(!isAttacking()){
                attack();
            }
        }
/*
        if(Input.isActionActive(ACTION_ATTACK_SECONDAY)){
            takeOutWeapon();
            setBlocking(true);
        }else{
            setBlocking(false);
        }*/

        if(desiredDirection.x == 0 && desiredDirection.y == 0){
            walk(0, 0, 300 * delta);
        }else{
            walk(desiredDirection.x, desiredDirection.y, 400 * delta);
        }


    }
}
