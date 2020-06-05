package me.cube.engine.game.animation;

import me.cube.engine.Voxel;
import me.cube.engine.game.LivingEntity;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class Avatar {

    private final Voxel torso, leftLeg, rightLeg, leftHand, rightHand, head;
    private float torsoWeight, leftLegWeight, rightLegWeight, leftHandWeight, rightHandWeight, headWeight;

    public float globalWeight;
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
                defaultTranslations.put(bodyPart, new Vector3f(part.position));
            }
        }
    }

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

    public void resetAllParts(){
        for(BodyPart bodyPart : BodyPart.values()){
            Voxel part = getBodyPart(bodyPart);
            if(part != null){
                part.position.set(defaultTranslations.get(bodyPart));
                part.rotation.identity();
            }
        }
    }

    public void rotate(BodyPart bodyPart, float angle, float x, float y, float z){
        Voxel voxel = getBodyPart(bodyPart);
        float weight = getWeight(bodyPart);

        if (voxel != null) {
            voxel.rotation.rotateAxis(angle * weight, x, y, z);
        }
    }

    public void translate(BodyPart bodyPart, float x, float y, float z){
        Voxel voxel = getBodyPart(bodyPart);
        float weight = getWeight(bodyPart);

        if (voxel != null) {
            voxel.position.add(x * weight, y * weight, z * weight);
        }
    }

    public void scale(BodyPart bodyPart, float x, float y, float z){
        Voxel voxel = getBodyPart(bodyPart);
        float weight = getWeight(bodyPart);

        if (voxel != null) {
            voxel.scale.mul(x * weight, y * weight, z * weight);
        }
    }

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


    public static enum BodyPart {
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
