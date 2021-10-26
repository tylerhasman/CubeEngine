package me.cube.game.entity;

import me.cube.engine.Renderer;
import me.cube.engine.Voxel;
import me.cube.game.world.World;
import org.joml.AABBf;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Entity {

    private final World world;

    public final Vector3f position;

    public final Vector3f velocity;

    public float life;

    public float gravity;

    public final AABBf boundingBox;
    private final Vector3f bbMin, bbMax;

    private boolean onGround;

    public boolean physics;

    public Entity(World world){
        this.world = world;
        position = new Vector3f();
        velocity = new Vector3f();
        gravity = -18.9f;
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

        velocity.y += gravity * delta;

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

                boundingBox.minY += 1;
                boundingBox.maxY += 1;

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

                boundingBox.minY += 1;
                boundingBox.maxY += 1;

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
                position.add(0, 100 * delta, 0);
            }
        }


    }

    private void updateBoundingBox(){
        position.sub(0, 0, 0, bbMin);
        position.add(0, 2.4f, 0, bbMax);

        boundingBox.setMin(bbMin);
        boundingBox.setMax(bbMax);
    }

    public void update(float delta){
        life += delta;
    }

    public void render(Renderer renderer){

    }
}
