package me.cube.engine.game.animation;

import me.cube.engine.Voxel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controls the animations for an entity
 *
 * Animations are split between layers.
 * The goal of this is to allow different animations to play on different layers.
 * By doing this we can have a running animation and a sword slash animation playing at the same time without
 * needing to program in combinations of them.
 */
public class AnimationController {

    private final Map<Integer, AnimationLayer> layers;
    private Avatar avatar;

    public AnimationController(Avatar avatar){
        layers = new HashMap<>();
        this.avatar = avatar;
    }

    public void setLayerWeight(int layer, Avatar.BodyPart bodyPart, float weight){
        if(!layers.containsKey(layer)){
            layers.put(layer, new AnimationLayer(avatar));
        }

        layers.get(layer).setWeight(bodyPart, weight);
    }

    public void addAnimation(int layer, String animationId, Animation animation){
        if(!layers.containsKey(layer)){
            layers.put(layer, new AnimationLayer(avatar));
        }

        AnimationLayer l = layers.get(layer);

        l.addAnimation(animationId, animation);
    }

    public void setActiveAnimation(int layer, String animationId){
        if(layers.containsKey(layer)){
            AnimationLayer l = layers.get(layer);
            l.setActiveAnimation(animationId);
        }else{
            System.err.println("Unknown animation layer "+layer+" ("+animationId+")");
        }
    }

    public void transitionAnimation(int layer, String nextAnimationId){
        if(layers.containsKey(layer)){
            AnimationLayer l = layers.get(layer);
            l.transitionAnimation(nextAnimationId);
        }else{
            System.err.println("Unknown animation layer "+layer+" ("+nextAnimationId+")");
        }
    }

    public void update(float delta){

        avatar.resetAllParts();
        for(AnimationLayer layer : layers.values()){
            layer.update(delta);
        }
    }

}
