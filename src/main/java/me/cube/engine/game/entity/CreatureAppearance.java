package me.cube.engine.game.entity;

import me.cube.engine.Voxel;
import me.cube.engine.file.Assets;
import me.cube.engine.model.SimpleVoxelMesh;
import org.joml.Vector3f;

import java.util.*;

/**
 * Defines a creatures appearance using a collection of different body parts and models
 */
public class CreatureAppearance {

    private List<BodyPart> bodyParts;

    public CreatureAppearance(){
        bodyParts = new ArrayList<>();
    }

    public void addBodyPart(BodyPart bodyPart){
        bodyParts.add(bodyPart);
    }

    public Voxel compile(){

        List<BodyPart> remaining = new ArrayList<>(bodyParts);

        int torsoIndex = findFirst(remaining, PartType.Torso);

        if(torsoIndex < 0){
            throw new IllegalStateException("Creatures require a torso");
        }

        BodyPart torso = remaining.remove(torsoIndex);

        SimpleVoxelMesh torsoMesh = Assets.loadModel(torso.model);

        Voxel voxel = new Voxel("Torso", torsoMesh);

        voxel.getTransform().scale(0.1f);

        for(PartType partType : torso.connectors.keySet()){
            List<Vector3f> connectors = torso.connectors.get(partType);

            for(Vector3f connector : connectors){

                int partIndex = findFirst(remaining, partType);

                if(partIndex < 0){
                    System.err.println("Missing part "+partType+" for torso");
                    break;
                }

                BodyPart part = remaining.remove(partIndex);
                System.out.println(part.model);
                Voxel vox = new Voxel(partType.name()+partIndex, Assets.loadModel(part.model));
                voxel.getTransform().addChild(vox.getTransform());
                vox.getTransform().setLocalPosition(connector.x - torsoMesh.pivot.x, connector.y - torsoMesh.pivot.y, connector.z - torsoMesh.pivot.z);


            }

        }

        return voxel;
    }

    private static int findFirst(List<BodyPart> bodyParts, PartType type){
        for(int i = 0; i < bodyParts.size();i++){
            if(bodyParts.get(i).partType == type){
                return i;
            }
        }
        return -1;
    }

    /**
     * Each body part knows its model and also where other body parts of different types can connect to.
     */
    public static class BodyPart {
        private String model;
        private final PartType partType;
        private Map<PartType, List<Vector3f>> connectors;

        public BodyPart(String model, PartType partType){
            this.model = model;
            this.partType = partType;
            connectors = new HashMap<>();
        }

        public void addConnector(PartType partType, Vector3f position){
            if(!connectors.containsKey(partType)){
                connectors.put(partType, new ArrayList<>());
            }

            connectors.get(partType).add(position);
        }

        public String getModel() {
            return model;
        }

        public List<Vector3f> getConnectors(PartType partType){
            return connectors.getOrDefault(partType, Collections.emptyList());
        }
    }

    public enum PartType{
        Torso,
        Head,
        LeftHand,
        RightHand,
        LeftLeg,
        RightLeg
    }
}
