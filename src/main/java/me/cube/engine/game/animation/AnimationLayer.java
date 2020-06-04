package me.cube.engine.game.animation;

import java.util.HashMap;
import java.util.Map;

public class AnimationLayer {

    private final Map<String, Animation> animations;
    private String activeAnimation;
    private String previousAnimation;
    private final Avatar avatar;
    private float time, prevTime;


    protected AnimationLayer(Avatar avatar) {
        this.animations = new HashMap<>();
        activeAnimation = "";
        previousAnimation = "";
        this.avatar = new Avatar(avatar);
    }

    protected void update(float delta){
        time += delta;
        Animation active = getActiveAnimation();
        Animation previous = getPreviousAnimation();
        float interpolated = Math.min(1f, time * 5f);
        if(previous != null){
            avatar.globalWeight = 1f - interpolated;
            previous.update(avatar, prevTime);
        }
        if(active != null){
            avatar.globalWeight = interpolated;
            active.update(avatar, time);
        }
    }

    public void setWeight(Avatar.BodyPart bodyPart, float weight){
        avatar.setWeight(bodyPart, weight);
    }

    protected void addAnimation(String id, Animation animation){
        animations.put(id, animation);
    }

    protected void setActiveAnimation(String activeAnimation) {
        if(!this.activeAnimation.equalsIgnoreCase(activeAnimation)){
            prevTime = time;
            time = 0f;
            this.previousAnimation = this.activeAnimation;
        }
        this.activeAnimation = activeAnimation;
    }

    private Animation getActiveAnimation(){
        if(animations.containsKey(activeAnimation)){
            return animations.get(activeAnimation);
        }
        return null;
    }

    private Animation getPreviousAnimation(){
        if(animations.containsKey(previousAnimation)){
            return animations.get(previousAnimation);
        }
        return null;
    }

}
