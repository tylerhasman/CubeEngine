package me.cube.engine.game.entity;

import me.cube.engine.Camera;
import me.cube.engine.Voxel;
import me.cube.engine.file.Assets;
import me.cube.engine.file.CubeFont;
import me.cube.engine.game.CubeGame;
import me.cube.engine.Input;
import me.cube.engine.game.world.World;
import org.joml.*;
import org.joml.Math;

import static me.cube.engine.Input.*;

public class Player extends LivingEntity {

    public Player(World world) {
        super(world);
        setMaxMoveSpeed(1000);

    }

    @Override
    public void update(float delta) {
        super.update(delta);

        Vector3f forward = CubeGame.game.getCameraForward().mul(1, 0, 1).normalize();

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

        if(Input.isActionActive(ACTION_ROLL)){
            if(velocity.x != 0 || velocity.z != 0){
                roll();
            }
        }

        if(Input.isActionActive(ACTION_ATTACK_PRIMARY)){
            takeOutWeapon();
            attack();
        }

        if(Input.isActionActive(ACTION_ATTACK_SECONDAY)){
            takeOutWeapon();
            setBlocking(true);
        }else{
            setBlocking(false);
        }


        if(desiredDirection.x == 0 && desiredDirection.y == 0){
            walk(0, 0, 300 * delta);
        }else{
            desiredDirection.normalize();
            walk(desiredDirection.x, desiredDirection.y, 800 * delta);
        }


    }
}
