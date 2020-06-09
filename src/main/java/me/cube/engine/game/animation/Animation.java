package me.cube.engine.game.animation;

public abstract class Animation {

    protected String fadeOnFinish = "";
    protected float speed = 1f;
    protected float transitionSpeedBack = 1f;
    protected boolean looping;

    public abstract void update(Avatar avatar, float time);

    /**
     * Played every time this animation is reset
     */
    protected void onAnimationComplete(){

    }

    /**
     * Called when this animation was playing but no longer is
     */
    protected void onAnimationFadeOut(){

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

    public Animation setLooping(boolean looping) {
        this.looping = looping;
        return this;
    }
}
