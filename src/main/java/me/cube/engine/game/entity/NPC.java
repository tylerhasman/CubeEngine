package me.cube.engine.game.entity;

import me.cube.engine.game.world.World;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class NPC extends LivingEntity {

    public boolean friendly;
    public float viewRange;

    private LivingEntity target;
    private float targetScanTimer;

    private float outOfCombatTimer;

    public NPC(World world) {
        super(world);
        friendly = false;
        viewRange = 300;
        outOfCombatTimer = 0;
    }

    private void moveTowards(Vector3f point, float acceleration){
        if(point.equals(position)){
            return;
        }

        Vector3f v = point.sub(position, new Vector3f()).normalize();

        walk(v.x, v.z, acceleration);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if(target == null){

            targetScanTimer -= delta;
            if(targetScanTimer <= 0){
                targetScanTimer += 0.5f;

                aquireTarget();
            }

            walk(0, 0, 300 * delta);


            outOfCombatTimer += delta;
            if(outOfCombatTimer >= 1){
                putAwayWeapon();
            }

        }else{



            if(target.isDead()) {
                target = null;
            }else if(target.position.distance(position) > viewRange * 1.2f){
                target = null;
            }else{

                if(target.position.distance(position) < 200){
                    takeOutWeapon();
                }

                if(target.position.distance(position) < 50){
                    attack();
                    walk(0, 0, 300 * delta);
                }else{
                    moveTowards(target.position, 800 * delta);
                }

            }


            outOfCombatTimer = 0;
        }

    }

    private boolean isEnemy(LivingEntity entity){
        if(friendly){
            return entity instanceof NPC && !((NPC) entity).friendly;
        }else{
            return entity instanceof Player;
        }
    }

    private void aquireTarget(){

        LivingEntity closest = null;
        float dst = 0;

        for(Entity entity : getWorld().getEntities()){
            if(entity instanceof LivingEntity){
                LivingEntity livingEntity = (LivingEntity) entity;

                if(isEnemy(livingEntity)){
                    float distance = livingEntity.position.distance(position);
                    if(closest == null){
                        closest = livingEntity;
                        dst = distance;
                    }else if(distance < dst){
                        closest = livingEntity;
                        dst = distance;
                    }
                }

            }
        }

        if(dst <= viewRange){
            target = closest;
        }

    }

}
