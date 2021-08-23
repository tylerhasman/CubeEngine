package me.cube.engine.game.entity;

import me.cube.engine.Voxel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CreatureAvatar {

    private Map<CreatureAppearance.PartType, List<Voxel>> parts;

    protected CreatureAvatar(Map<CreatureAppearance.PartType, List<Voxel>> parts){
        this.parts = parts;
    }

    public List<Voxel> getBodyParts(CreatureAppearance.PartType partType){
        return Collections.unmodifiableList(parts.getOrDefault(partType, Collections.emptyList()));
    }

    public Voxel getTorso(){
        return getBodyParts(CreatureAppearance.PartType.Torso).get(0);
    }

}
