package me.cube.engine.game.animation;

import me.cube.engine.util.MathUtil;
import org.joml.Math;
import org.joml.Vector3f;

public class SwordSlashAnimation extends Animation {

    private int swingType = 0;

    @Override
    public void update(Avatar avatar, float time) {

        if(swingType == 0){
            swingOne(avatar, time);
        }else if(swingType == 1){
            swingTwo(avatar, time);
        }

    }

    @Override
    protected void onAnimationComplete() {
        swingType++;
        if(swingType > 1){
            swingType = 0;
        }
    }

    private void swingTwo(Avatar avatar, float time) {
        float alphaTime = Math.min(1f, time);

        avatar.translate(Avatar.BodyPart.LeftHand, 0, 4 * alphaTime, 0);
        avatar.translate(Avatar.BodyPart.RightHand, -14, 4 * alphaTime, 0);

        avatar.rotate(Avatar.BodyPart.RightHand, Math.toRadians(10f), 0, 1, 0);
        avatar.rotate(Avatar.BodyPart.LeftHand, Math.toRadians(10f), 0, 1, 0);

        if(time >= 1f){
            float norTime = Math.min(0.8f, (time - 1f) * 1f/3f);

            //avatar.translate(Avatar.BodyPart.RightHand, Math.sin(norTime * MathUtil.PI2) * 5, Math.sin(norTime * MathUtil.PI2) * 10, -Math.sin(norTime * MathUtil.PI2) * 5);
            //avatar.translate(Avatar.BodyPart.LeftHand, Math.sin(norTime * MathUtil.PI2) * 5, Math.sin(norTime * MathUtil.PI2) * 10, -Math.sin(norTime * MathUtil.PI2) * 5);

            avatar.rotate(Avatar.BodyPart.RightHand, Math.sin(norTime * MathUtil.PI2) * MathUtil.PI / 2F, 1, 0, 0);
            avatar.rotate(Avatar.BodyPart.LeftHand, Math.sin(norTime * MathUtil.PI2) * MathUtil.PI / 2F, 1, 0, 0);

            avatar.rotate(Avatar.BodyPart.Torso, Math.toRadians(5 - norTime * norTime * 10f), 1, 0, 0);

        }else{
            avatar.rotate(Avatar.BodyPart.Torso, Math.toRadians(5 * alphaTime), 1, 0, 0);
        }
    }

    private void swingOne(Avatar avatar, float time){
        float alphaTime = Math.min(1f, time);

        avatar.translate(Avatar.BodyPart.LeftHand, 14, 4 * alphaTime, 0);
        avatar.translate(Avatar.BodyPart.RightHand, 0, 4 * alphaTime, 0);

        avatar.rotate(Avatar.BodyPart.RightHand, Math.toRadians(10f), 0, 1, 0);
        avatar.rotate(Avatar.BodyPart.LeftHand, Math.toRadians(10f), 0, 1, 0);

        float norTime = time * 1f / getDuration();

        avatar.translate(Avatar.BodyPart.RightHand, -16 * norTime, 0, -16 * norTime);
        avatar.rotate(Avatar.BodyPart.RightHand, Math.sin(norTime * MathUtil.PI / 2f) * MathUtil.PI / 4f,0, 1, 0);
        avatar.rotate(Avatar.BodyPart.RightHand, Math.sin(norTime * MathUtil.PI / 2f) * MathUtil.PI / 2,0, 0, -1);
        //avatar.rotate(Avatar.BodyPart.Torso, Math.sin(norTime * MathUtil.PI / 2f) * MathUtil.PI / 4f, -1, 1, 0);
    }

    @Override
    public float getDuration() {
        return 3.5f;
    }
}
