package me.cube.engine.game.animation;

import org.joml.Math;

public class ShieldBlockAnimation extends Animation {
    @Override
    public void update(Avatar avatar, float time) {
        avatar.translate(Avatar.BodyPart.LeftHand, 4, 2, -4);
        avatar.translate(Avatar.BodyPart.RightHand, 0, 2, 4);

        avatar.rotate(Avatar.BodyPart.RightHand, Math.toRadians(10f), 0, 1, 0);
        avatar.rotate(Avatar.BodyPart.LeftHand, Math.toRadians(-90), 0, 1, 0);
    }
}
