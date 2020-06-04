package me.cube.engine.game.animation;

import me.cube.engine.Voxel;

public abstract class Animation {

    protected static final float PI = (float) Math.PI;
    protected static final float PI2 = PI * 2f;

    public abstract void update(Avatar avatar, float time);

}
