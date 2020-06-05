package me.cube.engine.game;

import me.cube.engine.Voxel;
import me.cube.engine.VoxelModel;
import me.cube.engine.file.Assets;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static me.cube.engine.game.Input.*;

public class Player extends LivingEntity {

    public Player(World world) {
        super(world);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

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

        if(Input.isActionActive(ACTION_ATTACK_PRIMARY)){
            takeOutWeapon();
        }

        if(desiredDirection.x == 0 && desiredDirection.y == 0){
            walk(0, 0, 300 * delta);
        }else{
            walk(desiredDirection.x, desiredDirection.y, 400 * delta);
        }


    }
}
