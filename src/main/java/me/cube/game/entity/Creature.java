package me.cube.game.entity;

import me.cube.engine.Voxel;
import me.cube.game.world.World;
import me.cube.engine.util.MathUtil;
import org.joml.Math;
import org.joml.Quaternionf;

import java.util.List;

public class Creature extends Entity {

    private float timeAlive;

    private float animationTime;

    private CreatureAvatar avatar;

    public Creature(World world) {
        super(world);
        physics = true;
        timeAlive = 0;
        animationTime = 0;
    }

    public void changeAppearance(CreatureAppearance creatureAppearance){
        avatar = creatureAppearance.compile();

        root.addChild(avatar.getTorso());

    }

    public void updateAnimation(float delta){

        List<Voxel> leftLegs = avatar.getBodyParts(CreatureAppearance.PartType.LeftLeg);
        List<Voxel> rightLegs = avatar.getBodyParts(CreatureAppearance.PartType.RightLeg);

        for(int i = 0; i < leftLegs.size();i++){
            int direction = ((i & 1) == 1) ? 1 : -1;

            float rotation = Math.sin(animationTime * direction * 5) * MathUtil.PI / 4;

            leftLegs.get(i).rotation.set(new Quaternionf().rotateAxis(rotation, 1, 0, 0));
        }

        for(int i = 0; i < rightLegs.size();i++){
            int direction = ((i & 1) == 1) ? -1 : 1;
            float rotation = Math.cos(animationTime * direction * 5) * MathUtil.PI / 4;

            rightLegs.get(i).rotation.set(new Quaternionf().rotateAxis(rotation, 1, 0, 0));
        }

        animationTime += delta;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        updateAnimation(delta);
        timeAlive += delta;
    }

}
