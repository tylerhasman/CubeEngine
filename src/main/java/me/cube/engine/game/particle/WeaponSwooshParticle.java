package me.cube.engine.game.particle;

import org.joml.Vector3f;

public class WeaponSwooshParticle extends Particle {

    private final Vector3f top, bottom;
    private final Vector3f startTop, startBot;
    private final WeaponSwooshParticle last;

    public WeaponSwooshParticle(Vector3f top, Vector3f bottom, WeaponSwooshParticle last) {
        super(0.15f);
        this.top = top;
        this.bottom = bottom;
        this.last = last;
        startTop = new Vector3f(top);
        startBot = new Vector3f(bottom);
    }

    @Override
    public void render() {
/*        if(last != null){
            float norLife = life / 0.15f;
            glColor4f(0.8f, 0.8f, 0.8f, 0.7f - (1f - norLife) * 0.2f);

            glVertex3f(bottom.x, bottom.y, bottom.z);
            glVertex3f(last.bottom.x, last.bottom.y, last.bottom.z);

            glVertex3f(last.top.x, last.top.y, last.top.z);
            glVertex3f(top.x, top.y, top.z);
        }*/
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        float norLife = 1f - life / 0.15f;

        Vector3f half = new Vector3f();
        half.add(startTop).add(startBot).mul(0.5f);

        startTop.lerp(half, norLife, top);
        startBot.lerp(half, norLife, bottom);

    }
}
