package me.cube.engine.game.animation;

import org.joml.Math;

public class FallingAnimation extends Animation {
    @Override
    public void update(Avatar avatar, float time) {
        avatar.rotate(Avatar.BodyPart.LeftHand, (float)(1/(1+Math.exp(-5*time+3.5))), 1, 0, 0);
        avatar.rotate(Avatar.BodyPart.RightHand, (float)(1/(1+Math.exp(-5*time+3.5))), 1, 0, 0);
        avatar.translate(Avatar.BodyPart.LeftHand, 0, (float)(5/(1+Math.exp(-5*time+3.5))), 0);
        avatar.translate(Avatar.BodyPart.RightHand, 0, (float)(5/(1+Math.exp(-5*time+3.5))), 0);

        avatar.rotate(Avatar.BodyPart.LeftLeg, Math.toRadians(45f), 1, 0, 0);
        avatar.rotate(Avatar.BodyPart.RightLeg, Math.toRadians(-45f), 1, 0, 0);
    }
}
