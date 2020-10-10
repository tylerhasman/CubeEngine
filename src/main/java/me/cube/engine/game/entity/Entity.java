package me.cube.engine.game.entity;

import me.cube.engine.Voxel;
import me.cube.engine.game.world.World;
import org.joml.AABBf;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Entity {

    private final World world;

    public final Vector3f position, scale;
    public final Quaternionf rotation;

    public final Vector3f velocity;

    public final Voxel root;

    public float life;

    public float gravity;

    public final AABBf boundingBox;
    private final Vector3f bbMin, bbMax;

    private boolean onGround;

    public boolean physics;

    public Entity(World world){
        this.world = world;
        root = new Voxel("root", null);
        position = root.position;
        scale = root.scale;
        rotation = root.rotation;
        velocity = new Vector3f();
        gravity = -168f;
        boundingBox = new AABBf();
        bbMin = new Vector3f();
        bbMax = new Vector3f();
        physics = false;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public World getWorld() {
        return world;
    }

    public void updatePhysics(float delta){

        if(!physics)
            return;

        onGround = false;

        velocity.y += gravity * delta * 0.2f;

        if(velocity.y < gravity){
            velocity.y = gravity;
        }

        delta = delta / 10f;

        Vector3f beforePosition = new Vector3f(position);

        for(int i = 0; i < 10;i++){//Update physics more finely grained

            position.add(velocity.x * delta, 0, 0);
            updateBoundingBox();

            boolean pushUp = false;

            if(getWorld().getTerrain().isColliding(boundingBox)){

                boundingBox.minY += 10;
                boundingBox.maxY += 10;

                if(getWorld().getTerrain().isColliding(boundingBox)){
                    position.x = beforePosition.x;
                }else{
                    pushUp = true;
                }

            }

            position.add(0, 0, velocity.z * delta);
            updateBoundingBox();

            if(getWorld().getTerrain().isColliding(boundingBox)){
                /*position.z = beforePosition.z;
                //velocity.z = 0;*/

                boundingBox.minY += 10;
                boundingBox.maxY += 10;

                if(getWorld().getTerrain().isColliding(boundingBox)){
                    position.z = beforePosition.z;
                }else{
                    pushUp = true;
                }
            }


            position.add(0, velocity.y * delta, 0);
            updateBoundingBox();

            if(getWorld().getTerrain().isColliding(boundingBox)){
                position.y = beforePosition.y;
                velocity.y = 0;
                onGround = true;
            }

            if(pushUp){
                position.add(0, 1000 * delta, 0);
            }
        }


    }

    private void updateBoundingBox(){
        position.sub(8, 0, 8, bbMin);
        position.add(8, 24, 8, bbMax);

        boundingBox.setMin(bbMin);
        boundingBox.setMax(bbMax);
    }

    public void update(float delta){
        life += delta;

    }

}
