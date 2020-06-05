package me.cube.engine.game.animation;

import org.joml.Math;

import static me.cube.engine.util.MathUtil.PI;

public class WalkingAnimation extends Animation {

    @Override
    public void update(Avatar avatar, float time) {
        avatar.rotate(Avatar.BodyPart.LeftLeg, Math.sin(time * 14) * PI / 3f, 1, 0, 0);
        avatar.rotate(Avatar.BodyPart.RightLeg, Math.sin(-time * 14) * PI / 3f, 1, 0, 0);
        avatar.rotate(Avatar.BodyPart.Torso, -Math.sin(Math.min(time * 4, 1f)) * PI / 12f, 1, 0, 0);
        avatar.translate(Avatar.BodyPart.LeftHand, 0, Math.sin(time * 14) * 2.5f, 0);
        avatar.translate(Avatar.BodyPart.RightHand, 0, Math.sin(-time * 14) * 2.5f, 0);
    }

}
