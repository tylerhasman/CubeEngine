package me.cube.engine.game.entity;

import me.cube.engine.Voxel;
import me.cube.engine.game.world.World;

public class Creature extends Entity {

    public Creature(World world) {
        super(world);
    }

    public void changeAppearance(CreatureAppearance creatureAppearance){
        Voxel v = creatureAppearance.compile();

        root.getTransform().addChild(v.getTransform());
    }

}
