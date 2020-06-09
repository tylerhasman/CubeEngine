package me.cube.engine.game.entity;

import me.cube.engine.Voxel;
import me.cube.engine.VoxelModel;
import me.cube.engine.file.Assets;
import me.cube.engine.game.World;
import me.cube.engine.game.animation.*;
import me.cube.engine.game.item.Armor;
import me.cube.engine.game.item.Item;
import me.cube.engine.game.item.Weapon;
import me.cube.engine.game.particle.WeaponSwooshParticle;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Humanoid extends LivingEntity {

    protected static final int ANIMATION_LAYER_BASE = 0;
    protected static final int ANIMATION_LAYER_COMBAT = 1;

    public static final int EQUIPMENT_SLOT_HELMET = 0;
    public static final int EQUIPMENT_SLOT_TORSO = 1;
    public static final int EQUIPMENT_SLOT_HANDS = 2;
    public static final int EQUIPMENT_SLOT_FEET = 3;

    public static final int MAIN_HAND = 0;
    public static final int OFF_HAND = 1;

    //TODO: I'm not a huge fan of these constants, however, its fine for now
    private static final String MAIN_HAND_VOXEL_NAME = "main-hand-weapon";
    private static final String MAIN_HAND_ON_BACK_VOXEL_NAME = "main-hand-weapon-on-back";
    private static final String OFF_HAND_VOXEL_NAME = "off-hand-weapon";
    private static final String OFF_HAND_ON_BACK_VOXEL_NAME = "off-hand-weapon-on-back";

    private Item helmet, armor, shoes, gloves;
    private Weapon mainHand, offHand;

    private final String head, torso, hands, feet;

    private WeaponSwooshParticle lastSwooshParticle;

    public Humanoid(World world, String head, String torso, String hands, String feet) {
        super(world);

        this.head = head;
        this.torso = torso;
        this.hands = hands;
        this.feet = feet;

        initAppearance();
        initAnimations();
    }

    public final boolean isRolling(){
        return rollTime > 0f;
    }

    /**
     * When in combat Humanoids will pull out their weapons
     */
    public boolean isInCombat(){
        return false;
    }

    public boolean isBlocking(){
        return false;
    }

    public boolean isAttacking(){
        String combatAnimation = animationController.getCurrentAnimation(ANIMATION_LAYER_COMBAT);
        return !combatAnimation.equals("idle") && !combatAnimation.equals("prone");
    }

    @Override
    public void update(float delta) {

        Voxel weapon = root.getChild(MAIN_HAND_VOXEL_NAME);
        Voxel weaponOnBack = root.getChild(MAIN_HAND_ON_BACK_VOXEL_NAME);

        Voxel offHand = root.getChild(OFF_HAND_VOXEL_NAME);
        Voxel offHandOnBack = root.getChild(OFF_HAND_ON_BACK_VOXEL_NAME);

        weaponOnBack.enabled = !isInCombat();
        weapon.enabled = isInCombat();

        offHand.enabled = isInCombat();
        offHandOnBack.enabled = !isInCombat();

        if(animationController.isAnimationFlagEnabled(Animation.ANIMATION_FLAG_WEAPON_TRAIL)){
            emitWeaponSwoosh();
        }else{
            lastSwooshParticle = null;
        }

        if(rollTime > 0f){
            animationController.setActiveAnimation(ANIMATION_LAYER_BASE, "rolling");
        }else if(isOnGround()){
            if((Math.abs(velocity.x) > 0 || Math.abs(velocity.z) > 0)){
                animationController.transitionAnimation(ANIMATION_LAYER_BASE, "walking");
            }else{
                animationController.transitionAnimation(ANIMATION_LAYER_BASE, "idle");
            }
        }else{
            animationController.setActiveAnimation(ANIMATION_LAYER_BASE, "falling");
        }

        if(isInCombat()){
            animationController.setLayerWeight(ANIMATION_LAYER_BASE, Avatar.BodyPart.LeftHand, 0.25f);
            animationController.setLayerWeight(ANIMATION_LAYER_BASE, Avatar.BodyPart.RightHand, 0.25f);
        }else{

            animationController.setLayerWeight(ANIMATION_LAYER_BASE, Avatar.BodyPart.LeftHand, 1);
            animationController.setLayerWeight(ANIMATION_LAYER_BASE, Avatar.BodyPart.RightHand, 1);
            animationController.transitionAnimation(ANIMATION_LAYER_COMBAT, "idle");
        }

/*
        if(animationController.getCurrentAnimation(ANIMATION_LAYER_COMBAT).equals("prone")){
            if(blocking){
                animationController.transitionAnimation(ANIMATION_LAYER_COMBAT, "block");
            }else if(isInCombat()){
                animationController.transitionAnimation(ANIMATION_LAYER_COMBAT, "prone");
            }
        }*/

        super.update(delta);
    }

    private void emitWeaponSwoosh(){
        Voxel weapon = root.getChild(MAIN_HAND_VOXEL_NAME);

        Vector3f top = new Vector3f(0, weapon.model.height / 2f, 0);
        Vector3f bottom = new Vector3f(0, 0, 0);

        Matrix4f transform = weapon.getTransform();

        transform.transformPosition(top);
        transform.transformPosition(bottom);

        WeaponSwooshParticle particle = new WeaponSwooshParticle(top, bottom, lastSwooshParticle);

        getWorld().getParticleEngine().addParticle(particle);

        lastSwooshParticle = particle;
    }

    @Override
    public void attack() {
        super.attack();
        if(mainHand != null && mainHand.weaponType == Weapon.WeaponType.SWORD){
            animationController.setActiveAnimation(ANIMATION_LAYER_COMBAT, "sword");
        }
    }

    private void initAnimations(){
        Avatar avatar = new Avatar.AvatarBuilder()
                .withHead(root.getChild("head"), 1)
                .withTorso(root.getChild("torso"), 1)
                .withLeftHand(root.getChild("left-hand"), 1)
                .withRightHand(root.getChild("right-hand"), 1)
                .withLeftLeg(root.getChild("left-foot"), 1)
                .withRightLeg(root.getChild("right-foot"), 1)
                .build();

        animationController = new AnimationController(avatar);

        animationController.addAnimation(ANIMATION_LAYER_BASE, "idle", new IdleAnimation());
        animationController.addAnimation(ANIMATION_LAYER_BASE, "walking", new WalkingAnimation());
        animationController.addAnimation(ANIMATION_LAYER_BASE, "falling", new FallingAnimation());
        animationController.addAnimation(ANIMATION_LAYER_BASE, "rolling", new RollingAnimation().setTransitionSpeedBack(5f));

        animationController.addAnimation(ANIMATION_LAYER_COMBAT, "prone", new WeaponProneAnimation());
        animationController.addAnimation(ANIMATION_LAYER_COMBAT, "sword", new SwordSlashAnimation().setSpeed(9f).setFadeOnFinish("prone"));
        animationController.addAnimation(ANIMATION_LAYER_COMBAT, "block", new ShieldBlockAnimation());

    }

    public void equipArmor(int slot, Armor armor){
        //TODO: Implement
    }

    public void equipWeapon(int slot, Weapon weapon){
        Voxel toReplace = null;

        if(slot == MAIN_HAND){
            toReplace = root.getChild(MAIN_HAND_VOXEL_NAME);
            mainHand = weapon;
        }else if(slot == OFF_HAND){
            toReplace = root.getChild(OFF_HAND_VOXEL_NAME);
            offHand = weapon;
        }else{
            System.err.println("Unknown weapon slot "+slot);
        }

        if(toReplace != null && weapon != null){
            toReplace.model = Assets.loadModel(weapon.model);
        }
    }

    /**
     * Initializes all the positions of body parts. But they are all naked!
     */
    private void initAppearance(){
        VoxelModel torsoModel = Assets.loadModel(this.torso);
        VoxelModel handModel = Assets.loadModel(this.hands);
        VoxelModel footModel = Assets.loadModel(this.feet);
        VoxelModel headModel = Assets.loadModel(this.head);

        Voxel torso = new Voxel("torso", torsoModel);

        Voxel head = new Voxel("head", headModel);
        head.position.y = 10;

        Voxel leftHand = new Voxel("left-hand", handModel);
        leftHand.position.x = -8;

        Voxel offHand = new Voxel(OFF_HAND_VOXEL_NAME, null);
        offHand.scale.set(1f);
        leftHand.addChild(offHand);

        Voxel offHandOnBack = new Voxel(OFF_HAND_ON_BACK_VOXEL_NAME, null);
        offHandOnBack.scale.set(1f);
        torso.addChild(offHandOnBack);

        offHandOnBack.position.z = 6;
        offHandOnBack.position.x = 0;
        offHandOnBack.position.y = 5;
        offHandOnBack.rotation.identity();
        offHandOnBack.rotation.rotateAxis((float) Math.toRadians(90f), 0, 1, 0);
        offHandOnBack.rotation.rotateAxis((float) Math.toRadians(180f + 45f), 1, 0, 0);

        Voxel rightHand = new Voxel("right-hand", handModel);
        rightHand.position.x = 8;

        Voxel weapon = new Voxel(MAIN_HAND_VOXEL_NAME, null);
        weapon.scale.set(0.7f);

        rightHand.addChild(weapon);

        Voxel weaponOnBack = new Voxel(MAIN_HAND_ON_BACK_VOXEL_NAME, null);

        weaponOnBack.scale.set(0.7f);

        weaponOnBack.position.z = 6;
        weaponOnBack.position.x = 6;
        weaponOnBack.position.y = 10;
        weaponOnBack.rotation.identity();
        weaponOnBack.rotation.rotateAxis((float) Math.toRadians(90f), 0, 1, 0);
        weaponOnBack.rotation.rotateAxis((float) Math.toRadians(180f + 45f), 1, 0, 0);

        torso.addChild(weaponOnBack);

        Voxel leftFoot = new Voxel("left-foot", footModel);
        leftFoot.position.y = -6;
        leftFoot.position.x = -4;
        leftFoot.position.z = 1;

        Voxel rightFoot = new Voxel("right-foot", footModel);
        rightFoot.position.y = -6;
        rightFoot.position.x = 4;
        rightFoot.position.z = 1;

        torso.position.y += 9.5f;

        root.addChild(torso);

        torso.addChild(head);
        torso.addChild(leftHand);
        torso.addChild(rightHand);
        torso.addChild(leftFoot);
        torso.addChild(rightFoot);
    }

}
