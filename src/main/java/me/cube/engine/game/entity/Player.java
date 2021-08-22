package me.cube.engine.game.entity;

import me.cube.engine.file.Assets;
import me.cube.engine.game.world.World;

public class Player extends Creature{
    public Player(World world) {
        super(world);

        CreatureAppearance appearance = new CreatureAppearance();
        appearance.addBodyPart(Assets.loadBodyPart("test_torso.json"));
        appearance.addBodyPart(new CreatureAppearance.BodyPart("head.vxm", CreatureAppearance.PartType.Head));
        appearance.addBodyPart(new CreatureAppearance.BodyPart("hand.vxm", CreatureAppearance.PartType.LeftHand));
        appearance.addBodyPart(new CreatureAppearance.BodyPart("hand.vxm", CreatureAppearance.PartType.RightHand));
        appearance.addBodyPart(new CreatureAppearance.BodyPart("foot.vxm", CreatureAppearance.PartType.LeftLeg));
        appearance.addBodyPart(new CreatureAppearance.BodyPart("foot.vxm", CreatureAppearance.PartType.RightLeg));

        changeAppearance(appearance);
    }
}
