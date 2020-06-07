package me.cube.engine.game.animation;

public abstract class Animation {

    protected String fadeOnFinish = "";
    protected float speed = 1f;
    protected float transitionSpeedBack = 1f;

    public abstract void update(Avatar avatar, float time);

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
