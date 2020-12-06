package me.cube.engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera {

    public static Matrix4f projectionMatrix = new Matrix4f();
    public static  Matrix4f cameraMatrix = new Matrix4f();


    /**
     * @param screen must be normalized between -1.0 to 1.0
     */
    public static Vector3f screenToWorld(Vector2f screen){

        //Matrix4f combined = projectionMatrix.mul(cameraMatrix, new Matrix4f()).invert();

        Vector4f rayClip = new Vector4f(screen.x, screen.y, -1f, 1.0f);

        rayClip.mul(projectionMatrix.invert(new Matrix4f()));
        rayClip.mul(cameraMatrix.invert(new Matrix4f()));

        rayClip.mul(1f / rayClip.w);

        return new Vector3f(rayClip.x, rayClip.y, rayClip.z);
    }

    /**
     * @param screen must be normalized between -1.0 to 1.0
     */
    public static Vector3f screenToDirection(Vector2f screen){
        Vector4f rayClip = new Vector4f(screen.x, screen.y, -1.0f, 1.0f);

        Vector4f rayEye = rayClip.mul(projectionMatrix.invert(new Matrix4f()), new Vector4f());

        rayEye.z = -1.0f;
        rayEye.w = 0.0f;

        Vector4f rayWorld = rayEye.mul(cameraMatrix.invert(new Matrix4f()), new Vector4f());

        return new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z).normalize();
    }

}
