package me.cube.engine.game.animation;

import me.cube.engine.util.MathUtil;
import org.joml.Math;

import static me.cube.engine.util.MathUtil.PI;

public class SwordSlashAnimation extends Animation {

    @Override
    public void update(Avatar avatar, float time) {

        avatar.rotate(Avatar.BodyPart.Torso, PI / 8f, 0, 1, 0);
        avatar.translate(Avatar.BodyPart.LeftHand, 0, 0, 4);

        swingOne(avatar, time);

    }

    @Override
    protected void onAnimationComplete() {

    }

    @Override
    protected void onAnimationFadeOut() {

    }

    private void swingTwo(Avatar avatar, float time) {
        swingOne(avatar, 1f);

        float timeWindup = Math.min(time * 1.5f, 1f);
        float timeSwingBack = Math.max(0f, time - 1f / 1.5f) * 3f;

        avatar.rotate(Avatar.BodyPart.RightHand, Math.sin(timeWindup * PI / 2f) * PI, 0, 1, 0);

    }

    private void swingOne(Avatar avatar, float time){

        float timeWindup = Math.min(time * 1.5f, 1f);

        avatar.translate(Avatar.BodyPart.RightHand, -14 * Math.sin(timeWindup * PI / 2f), 0, -6 * Math.sin(timeWindup * PI / 2f));
        avatar.rotate(Avatar.BodyPart.RightHand, Math.sin(timeWindup * PI / 2f) * PI / 2f, 0, 0, 1);

        float timeSwingBack = Math.max(0f, time - 1f / 1.5f) * 3f;

        avatar.translate(Avatar.BodyPart.RightHand, 14 * Math.sin(timeSwingBack * PI / 1.5f), 0, -6 * Math.sin(timeSwingBack * PI));
        avatar.rotate(Avatar.BodyPart.RightHand, Math.sin(timeSwingBack * PI / 2f) * -PI / 1.5f, 1, 0, 0);

    }

}
