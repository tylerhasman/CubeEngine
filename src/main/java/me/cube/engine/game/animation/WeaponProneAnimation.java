package me.cube.engine.game.animation;

import me.cube.engine.util.MathUtil;

public class WeaponProneAnimation extends Animation {
    @Override
    public void update(Avatar avatar, float time) {
        avatar.translate(Avatar.BodyPart.LeftHand, 7, 0, -10);
        avatar.translate(Avatar.BodyPart.RightHand, -7, 0, -10);
        avatar.rotate(Avatar.BodyPart.RightHand, -MathUtil.PI / 8f, 1f, 0, 0);
    }
}
