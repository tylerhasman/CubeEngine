package me.cube.game.entity;

import me.cube.engine.Camera;
import me.cube.engine.Input;
import me.cube.engine.file.Assets;
import me.cube.engine.util.MathUtil;
import me.cube.game.CubeGame;
import me.cube.game.world.World;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Player extends Creature{
    public Player(World world) {
        super(world);

        CreatureAppearance appearance = new CreatureAppearance();
        appearance.addBodyPart(Assets.loadBodyPart("test_torso.json"));


        appearance.addBodyPart(new CreatureAppearance.BodyPart("head.vxm", CreatureAppearance.PartType.Head));
        appearance.addBodyPart(new CreatureAppearance.BodyPart("hand.vxm", CreatureAppearance.PartType.LeftHand));
        appearance.addBodyPart(new CreatureAppearance.BodyPart("hand.vxm", CreatureAppearance.PartType.RightHand));
        appearance.addBodyPart(new CreatureAppearance.BodyPart("hand.vxm", CreatureAppearance.PartType.LeftLeg));
        appearance.addBodyPart(new CreatureAppearance.BodyPart("hand.vxm", CreatureAppearance.PartType.RightLeg));

        changeAppearance(appearance);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if(isOnGround()){
            velocity.x = 0;
            velocity.z = 0;
        }

        boolean strafe = false;

        if(Input.isActionActive(Input.ACTION_STRAFE)){
            this.yaw = CubeGame.game.getYaw() / 360f * MathUtil.PI2 + MathUtil.PI;
            strafe = true;
        }

        Vector3f forward = getForward();
        Vector3f left = forward.rotateAxis(MathUtil.PI / 2f, 0, 1, 0, new Vector3f());

        Vector3f direction = new Vector3f();

        if(Input.isActionActive(Input.ACTION_FORWARD)){
            direction.add(forward);
        }

        if(Input.isActionActive(Input.ACTION_BACK)){
            direction.sub(forward);
        }

        if(Input.isActionActive(Input.ACTION_LEFT)){
            if(strafe){
                direction.add(left);
            }else{
                yaw += MathUtil.PI * delta;
            }
        }

        if(Input.isActionActive(Input.ACTION_RIGHT)){
            if(strafe){
                direction.sub(left);
            }else{
                yaw -= MathUtil.PI * delta;
            }
        }

        if(direction.lengthSquared() > 0 && isOnGround()){
            velocity.set(direction.normalize().mul(moveSpeed));
        }


        if(Input.isActionActive(Input.ACTION_JUMP)){
            if(isOnGround()){
                velocity.y = 10;
            }
        }


    }
}
