package com.vincentzhang.sixlegrobot.math;

public class Vector3D {
    private float x;
    private float y;
    private float z;

    public Vector3D(Vector3D other){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getZ(){
        return z;
    }

    public Vector3D(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D add(Vector3D other){
        Vector3D v = new Vector3D(x,y,z);
        v.x += other.x;
        v.y += other.y;
        v.z += other.z;

        return v;
    }

    public Vector3D multi(float l){
        Vector3D v = new Vector3D(x,y,z);
        v.x *= l;
        v.y *= l;
        v.z *= z;
        return v;
    }

    public float length(){
        return (float) Math.sqrt(x*x+y*y+z*z);
    }

    public Vector3D normalize() {
        float leng = length();
        return new Vector3D(x/leng, y/leng, z/leng);
    }
}
