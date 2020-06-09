package me.cube.engine.game.animation;

import me.cube.engine.util.MathUtil;
import org.joml.Math;

import static me.cube.engine.util.MathUtil.PI;
import static me.cube.engine.util.MathUtil.PI2;

public class WalkingAnimation extends Animation {

    private float totalTime;

    @Override
    public void update(Avatar avatar, float time) {

        time = time * PI2;

        avatar.rotate(Avatar.BodyPart.LeftLeg, Math.sin(time * 2) * PI / 3f, 1, 0, 0);
        avatar.rotate(Avatar.BodyPart.RightLeg, Math.sin(-time * 2) * PI / 3f, 1, 0, 0);
        avatar.translate(Avatar.BodyPart.LeftHand, 0, Math.sin(time * 2) * 2.5f, 0);
        avatar.translate(Avatar.BodyPart.RightHand, 0, Math.sin(-time * 2) * 2.5f, 0);

        //Note this devilish use of onAnimationComplete and onAnimationFadeOut to track the total time the animation has played
        //This is the intended purpose
        avatar.rotate(Avatar.BodyPart.Torso, -Math.sin(Math.min((time + totalTime) * 4, 1f)) * PI / 12f, 1, 0, 0);
    }

    @Override
    protected void onAnimationComplete() {
        super.onAnimationComplete();

        totalTime += PI2;
    }

    @Override
    protected void onAnimationFadeOut() {
        super.onAnimationFadeOut();

        totalTime = 0f;
    }
}
