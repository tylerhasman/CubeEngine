package me.cube.engine.game.animation;

import me.cube.engine.util.MathUtil;
import org.joml.Math;
import org.joml.Vector3f;

public class WeaponProneAnimation extends Animation {

    @Override
    public void update(Avatar avatar, float time) {

        avatar.translate(Avatar.BodyPart.LeftHand, 0, 2, -1.8f);
        avatar.translate(Avatar.BodyPart.RightHand, 0, 2, 0);

        avatar.rotate(Avatar.BodyPart.RightHand, Math.toRadians(10f), 0, 1, 0);
        avatar.rotate(Avatar.BodyPart.LeftHand, Math.toRadians(-35), 0, 1, 0);

    }

}
