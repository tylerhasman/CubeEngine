package me.cube.engine.game.animation;

import java.util.HashMap;
import java.util.Map;

/**
 * The animation layer is responsible for playing and transitioning to and from different animations;
 */
public class AnimationLayer {

    private static final float DEFAULT_ANIMATION_TRANSITION_SPEED = 6f;

    private final Map<String, Animation> animations;
    /**
     * The current animation playing
     */
    private String activeAnimation;

    /**
     * The last animation that was playing
     */
    private String previousAnimation;

    private final Avatar avatar;

    /**
     * The duration the current time has been playing as well as how long the previous animation played for.
     * The previous animation is used to smoothly transition from the previous animation to the current one.
     */
    private float time, prevTime;


    protected AnimationLayer(Avatar avatar) {
        this.animations = new HashMap<>();
        activeAnimation = "";
        previousAnimation = "";
        this.avatar = new Avatar(avatar);
    }

    protected void update(float delta){
        Animation active = getActiveAnimation();
        Animation previous = getPreviousAnimation();

        if(active != null){
            time += delta * active.speed;//Some animations will play faster
        }else{
            time += delta;//There is currently no active animation, we could do nothing here as well
        }

        //the interpolated time in between the current animation and the previous one
        float transitionTimeBack = time * DEFAULT_ANIMATION_TRANSITION_SPEED;

        //Sometimes the previous animation will transition back faster than usual
        if(previous != null){
            transitionTimeBack *= previous.transitionSpeedBack;
        }

        //Normalize the transition time.
        //Once it is '1f' we have fully transitioned from the previous animation to the active one
        float interpolated = Math.min(1f, transitionTimeBack);

        //If the previous animation exists we update it onto the avatar
        if(previous != null){
            avatar.globalWeight = 1f - interpolated;//It's weight goes from zero to one
            previous.update(avatar, prevTime);
        }

        if(active != null){
            avatar.globalWeight = interpolated;//Active animations weight goes from zero to one
            active.update(avatar, time);

            //If the animation has completed we check if we should transition into another animation
            if(time >= active.getDuration()){
                if(!active.fadeOnFinish.isEmpty()){
                    transitionAnimation(active.fadeOnFinish);
                }
            }
        }

    }

    /**
     * Sets the weight an animation has on a body part
     * @param weight 0.0 to 1.0
     */
    public void setWeight(Avatar.BodyPart bodyPart, float weight){
        avatar.setWeight(bodyPart, weight);
    }

    protected void addAnimation(String id, Animation animation){
        animations.put(id, animation);
    }

    /**
     * Request this animation layer smoothly transitions between current animation and desired one
     */
    protected void transitionAnimation(String animationId) {
        if(!this.activeAnimation.equals(animationId)){
            prevTime = time;
            time = 0f;
            this.previousAnimation = this.activeAnimation;
        }
        this.activeAnimation = animationId;
    }

    /**
     * Change this animation layer instantly to another animation (no transition)
     */
    protected void setActiveAnimation(String animationId) {
        if(!this.activeAnimation.equalsIgnoreCase(animationId)){
            prevTime = 0f;
            time = 0f;
        }
        this.previousAnimation = animationId;
        this.activeAnimation = animationId;
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

    public String getCurrentAnimationId() {
        if(activeAnimation == null){
            return "";
        }
        return activeAnimation;
    }

    public float getCurrentAnimationTime(){
        return time;
    }
}
