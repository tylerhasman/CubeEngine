package me.cube.engine.game.particle;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glVertex3f;

public class WeaponSwooshParticle extends Particle {

    private final Vector3f top, bottom;
    private final WeaponSwooshParticle last;

    public WeaponSwooshParticle(Vector3f top, Vector3f bottom, WeaponSwooshParticle last) {
        super(0.1f);
        this.top = top;
        this.bottom = bottom;
        this.last = last;
    }

    @Override
    public void render() {
        if(last != null){
            glColor4f(0.8f, 0.8f, 0.8f, 0.7f - (0.35f - life));
            glVertex3f(bottom.x, bottom.y, bottom.z);
            glVertex3f(last.bottom.x, last.bottom.y, last.bottom.z);

            glVertex3f(last.top.x, last.top.y, last.top.z);
            glVertex3f(top.x, top.y, top.z);
        }
    }
}
