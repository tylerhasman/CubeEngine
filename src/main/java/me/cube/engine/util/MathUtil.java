package me.cube.engine.util;

import org.joml.Vector3f;

public class MathUtil {

    public static final float PI = (float) Math.PI;
    public static final float PI2 = PI * 2f;

    /** Linearly interpolates between two angles in radians. Takes into account that angles wrap at two pi and always takes the
     * direction with the smallest delta angle.
     *
     * @param fromRadians start angle in radians
     * @param toRadians target angle in radians
     * @return the interpolated angle in the range [0, PI2[ */
    public static float moveAngleTowards (float fromRadians, float toRadians, float speed) {
        float max = PI2;

        float da = (toRadians - fromRadians) % max;

        float shortAngleDist = 2 * da % max - da;

        float newAngle = fromRadians + shortAngleDist * speed;

        return newAngle;
    }

    public static float moveValueTo(float from, float to, float speed){
        if(from < to){
            from += speed;
            if(from > to){
                from = to;
            }
        }else if(from > to){
            from -= speed;
            if(from < to){
                from = to;
            }
        }
        return from;
    }

    public static float swingIn(float a, float scale){
        return a = a * a * ((scale + 1) * a - scale);
    }

    public static float angleDifferenceRad(float angle1, float angle2){
        float diff = Math.max(angle1, angle2) - Math.min(angle1, angle2);

        while (diff > PI) diff = PI2 - diff;

        return diff;
    }

    public static void moveTowards(Vector3f from, Vector3f to, float speed) {
        from.x = moveValueTo(from.x, to.x, speed);
        from.y = moveValueTo(from.y, to.y, speed);
        from.z = moveValueTo(from.z, to.z, speed);
    }
}
