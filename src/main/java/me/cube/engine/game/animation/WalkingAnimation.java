package me.cube.engine.game.animation;

import org.joml.Math;

public class WalkingAnimation extends Animation {

    @Override
    public void update(Avatar avatar, float time) {
        avatar.rotate(Avatar.BodyPart.LeftLeg, Math.sin(time * 14) * PI / 3f, 1, 0, 0);
        avatar.rotate(Avatar.BodyPart.RightLeg, Math.sin(-time * 14) * PI / 3f, 1, 0, 0);
    }

}
