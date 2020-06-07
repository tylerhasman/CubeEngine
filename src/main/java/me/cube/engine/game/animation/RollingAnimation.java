package me.cube.engine.game.animation;

public class RollingAnimation extends Animation {
    @Override
    public void update(Avatar avatar, float time) {
        avatar.translate(Avatar.BodyPart.LeftHand, 0, 10, 0);
        avatar.translate(Avatar.BodyPart.RightHand, 0, 10, 0);
    }
}
