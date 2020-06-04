package me.cube.engine.game.animation;

import org.joml.Math;

public class FallingAnimation extends Animation {
    @Override
    public void update(Avatar avatar, float time) {

        avatar.rotate(Avatar.BodyPart.LeftLeg, Math.toRadians(45f), 1, 0, 0);
        avatar.rotate(Avatar.BodyPart.RightLeg, Math.toRadians(-45f), 1, 0, 0);

    }
}
