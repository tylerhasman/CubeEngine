package me.cube.engine.game.animation;

public abstract class Animation {

    public static final int ANIMATION_FLAG_WEAPON_TRAIL = 1;

    protected String fadeOnFinish = "";
    protected float speed = 1f;
    protected float transitionSpeedBack = 1f;
    /**
     * Flags can be used to communicate with the Entity that is using this animation.
     * They need to be changed with each {@link #update(Avatar, float)} call as they will be reset before each update
     */
    protected int animationFlags;

    public abstract void update(Avatar avatar, float time);

    protected void onAnimationComplete(){

    }

    public float getDuration(){
        return 1f;
    }

    public Animation setSpeed(float speed){
        this.speed = speed;
        return this;
    }

    public Animation setTransitionSpeedBack(float transitionSpeedBack) {
        this.transitionSpeedBack = transitionSpeedBack;
        return this;
    }

    public Animation setFadeOnFinish(String fadeOnFinish) {
        this.fadeOnFinish = fadeOnFinish;
        return this;
    }
}
