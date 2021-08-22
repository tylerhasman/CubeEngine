package me.cube.engine.game.animation;

import me.cube.engine.Voxel;

public abstract class Animator {

    public Voxel head, torso, leftArm, rightArm;
    public Voxel[] leftLegs, rightLegs;

    public abstract void update(float delta);

}
