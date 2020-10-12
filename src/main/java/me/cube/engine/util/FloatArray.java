package me.cube.engine.util;

import java.util.Arrays;

public class FloatArray {

    private float[] array;
    private int index;

    public FloatArray(int initialCapacity){
        array = new float[initialCapacity];
    }

    public FloatArray(){
        this(64);
    }

    public void add(float... floats){
        for(float f : floats){
            add(f);
        }
    }

    public void add(float f){
        if(index + 1 >= array.length){
            resize(array.length * 2);
        }
        array[index++] = f;
    }

    public void addRepeat(float f, int times){
        for(int i = 0; i < times;i++){
            add(f);
        }
    }

    public void addRepeat(float[] floats, int times){
        for(int i = 0; i < times;i++){
            add(floats);
        }
    }

    public float[] toArray(){
        return Arrays.copyOf(array, index);
    }

    public int count(){
        return index;
    }

    private void resize(int capacity){
        array = Arrays.copyOf(array, capacity);
    }

}
