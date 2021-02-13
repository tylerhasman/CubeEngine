package me.cube.engine.game.animation;

import me.cube.engine.Voxel;
import me.cube.engine.game.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * Avatars are the buffer between an {@link Animation} and a group of {@link Voxel}s.
 * Right now we only support humanoids.
 * For more simple animations of world objects, we can prograqm them directly into {@link Entity#update(float)}
 * The point of using this buffer is to allow more finely grained animations for more complex creatures such as the Player or an enemy
 */
public class Avatar {

    private final Voxel torso, leftLeg, rightLeg, leftHand, rightHand, head;

    /**
     * All weights go from 0.0 to 1.0
     * Any other values are undefined
     */
    private float torsoWeight, leftLegWeight, rightLegWeight, leftHandWeight, rightHandWeight, headWeight;

    /**
     * A global modifier applied to all weights when queried with {@link Avatar#getWeight(BodyPart)}
     */
    protected float globalWeight;

    /**
     * This map is really important.
     * Some voxels have default positions. For example, the hands or legs of a humanoid.
     * When we reset the objects body parts with {@link #resetAllParts()} we need to know their default positions.
     * In the future we may also need to store default rotations but for now this isn't necessary
     */
    private Map<BodyPart, Vector3f> defaultTranslations;

    private Avatar(Voxel torso, Voxel leftLeg, Voxel rightLeg, Voxel leftHand, Voxel rightHand, Voxel head,
                   float torsoWeight, float leftLegWeight, float rightLegWeight, float leftHandWeight,
                   float rightHandWeight, float headWeight) {
        this.torso = torso;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;

        this.leftHand = leftHand;
        this.rightHand = rightHand;
        this.head = head;
        this.torsoWeight = torsoWeight;
        this.leftLegWeight = leftLegWeight;
        this.rightLegWeight = rightLegWeight;
        this.leftHandWeight = leftHandWeight;
        this.rightHandWeight = rightHandWeight;
        this.headWeight = headWeight;
        defaultTranslations = new HashMap<>();
        globalWeight = 1;

        for(BodyPart bodyPart : BodyPart.values()){
            Voxel part = getBodyPart(bodyPart);
            if(part != null){
                defaultTranslations.put(bodyPart, new Vector3f(part.getTransform().getLocalPosition()));
            }
        }
    }

    /**
     * Copies another avatar onto this
     */
    Avatar(Avatar other){
        this.torso = other.torso;
        this.leftLeg = other.leftLeg;
        this.rightLeg = other.rightLeg;

        this.leftHand = other.leftHand;
        this.rightHand = other.rightHand;
        this.head = other.head;
        this.torsoWeight = other.torsoWeight;
        this.leftLegWeight = other.leftLegWeight;
        this.rightLegWeight = other.rightLegWeight;
        this.leftHandWeight = other.leftHandWeight;
        this.rightHandWeight = other.rightHandWeight;
        this.headWeight = other.headWeight;
        this.defaultTranslations = other.defaultTranslations;
        globalWeight = 1;
    }

    private Voxel getBodyPart(BodyPart bodyPart){
        switch (bodyPart){
            case Head:
                return head;
            case LeftHand:
                return leftHand;
            case LeftLeg:
                return leftLeg;
            case RightHand:
                return rightHand;
            case Torso:
                return torso;
            case RightLeg:
                return rightLeg;
        }
        return null;
    }

    private float getWeight(BodyPart bodyPart){
        switch (bodyPart){
            case Head:
                return headWeight * globalWeight;
            case LeftHand:
                return leftHandWeight * globalWeight;
            case LeftLeg:
                return leftLegWeight * globalWeight;
            case RightHand:
                return rightHandWeight * globalWeight;
            case Torso:
                return torsoWeight * globalWeight;
            case RightLeg:
                return rightLegWeight * globalWeight;
        }
        return 0f;
    }

    /**
     * Resets all voxels to their default positions.
     * Default positions are defined as the position the voxel was in when this avatar was created.
     */
    public void resetAllParts(){
        for(BodyPart bodyPart : BodyPart.values()){
            Voxel part = getBodyPart(bodyPart);
            if(part != null){
                part.getTransform().set(defaultTranslations.get(bodyPart), new Quaternionf(), new Vector3f(1,1, 1));
            }
        }
    }

    /**
     * Rotate a body part. (Additively!)
     * The weight will affect how much it is rotated.
     * If the weight of the body part is 0.5, and the rotated angle is 180, it will rotate 90 instead.
     */
    public void rotate(BodyPart bodyPart, float angle, float x, float y, float z){
        Voxel voxel = getBodyPart(bodyPart);
        float weight = getWeight(bodyPart);

        if (voxel != null) {
            voxel.getTransform().rotateAxis(angle * weight, x, y, z);
        }
    }

    /**
     * Translate a body part. (Additively!)
     * The weight will affect how much it is translated.
     * If the weight of the body part is 0.5, and the translation is (0, 1, 0), it will translate (0, 0.5, 0) instead
     */
    public void translate(BodyPart bodyPart, float x, float y, float z){
        Voxel voxel = getBodyPart(bodyPart);
        float weight = getWeight(bodyPart);

        if (voxel != null) {
            voxel.getTransform().translate(x * weight, y * weight, z * weight);
        }
    }

    /**
     * Scale a body part! (Additively!)
     * The weight will affect the scaled amount.
     * If the weight of the body part is 0.5, and the scaling is (1, 1, 1), the scale will increase by (0.5, 0.5, 0.5) instead
     */
/*    public void scale(BodyPart bodyPart, float x, float y, float z){
        Voxel voxel = getBodyPart(bodyPart);
        float weight = getWeight(bodyPart);

        if (voxel != null) {
            voxel.scale.add((x - 1.0f) * weight, (y - 1.0f) * weight, (z - 1.0f) * weight);
        }
    }*/

    /**
     * Change the weight of a body part
     * @param weight 0.0 to 1.0
     */
    public void setWeight(BodyPart bodyPart, float weight){
        switch (bodyPart){
            case Head:
                headWeight = weight;
                break;
            case LeftHand:
                leftHandWeight = weight;
                break;
            case LeftLeg:
                leftLegWeight = weight;
                break;
            case RightHand:
                rightHandWeight = weight;
                break;
            case Torso:
                torsoWeight = weight;
                break;
            case RightLeg:
                rightLegWeight = weight;
                break;
        }
    }


    /**
     * Current supported body parts
     */
    public enum BodyPart {
        Torso,
        LeftLeg,
        RightLeg,
        LeftHand,
        RightHand,
        Head
    }

    public static class AvatarBuilder {
        private Voxel torso, leftLeg, rightLeg, leftHand, rightHand, head;
        private float torsoWeight, leftLegWeight, rightLegWeight, leftHandWeight, rightHandWeight, headWeight;

        public AvatarBuilder(){
            torsoWeight = 0f;
            leftLegWeight = 0f;
            rightLegWeight = 0f;
            leftHandWeight = 0f;
            rightHandWeight = 0f;
            headWeight = 0f;
        }

        public Avatar build(){
            return new Avatar(torso, leftLeg, rightLeg, leftHand, rightHand, head, torsoWeight, leftLegWeight, rightLegWeight, leftHandWeight, rightHandWeight, headWeight);
        }

        public AvatarBuilder withTorso(Voxel voxel, float weight){
            this.torso = voxel;
            this.torsoWeight = weight;
            return this;
        }

        public AvatarBuilder withLeftLeg(Voxel voxel, float weight){
            this.leftLeg = voxel;
            this.leftLegWeight = weight;
            return this;
        }

        public AvatarBuilder withRightLeg(Voxel voxel, float weight){
            this.rightLeg = voxel;
            this.rightLegWeight = weight;
            return this;
        }

        public AvatarBuilder withLeftHand(Voxel voxel, float weight){
            this.leftHand = voxel;
            this.leftHandWeight = weight;
            return this;
        }

        public AvatarBuilder withRightHand(Voxel voxel, float weight){
            this.rightHand = voxel;
            this.rightHandWeight = weight;
            return this;
        }

        public AvatarBuilder withHead(Voxel voxel, float weight){
            this.head = voxel;
            this.headWeight = weight;
            return this;
        }


    }

}
