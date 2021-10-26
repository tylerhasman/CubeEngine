package me.cube.game.entity;

import me.cube.engine.Renderer;
import me.cube.engine.Voxel;
import me.cube.game.world.World;
import me.cube.engine.util.MathUtil;
import org.joml.*;
import org.joml.Math;

import java.util.Collections;
import java.util.List;

public class Creature extends Entity {

    private float timeAlive;

    private float animationTime;

    private CreatureAvatar avatar;

    private Voxel modelRoot;

    public float yaw;

    public float moveSpeed;

    public Creature(World world) {
        super(world);
        physics = true;
        timeAlive = 0;
        animationTime = 0;
        moveSpeed = 10f;

        modelRoot = new Voxel("");
    }

    public Vector3f getForward(){

        Matrix4f m = new Matrix4f().identity().rotate(yaw, 0, 1, 0);

        Vector4f forward = new Vector4f(0, 0, 1, 0);

        forward.mul(m);

        return new Vector3f(forward.x, forward.y, forward.z).normalize();
    }

    public void changeAppearance(CreatureAppearance appearance){
        this.avatar = appearance.compile();
        this.modelRoot = avatar.getTorso();
    }

    @Override
    public void render(Renderer renderer) {
        super.render(renderer);

        modelRoot.position.set(position);
        //modelRoot.scale.set(1, 1, 1);
        modelRoot.rotation.identity().rotateAxis(yaw, new Vector3f(0, 1, 0));

        renderer.render(modelRoot);

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
