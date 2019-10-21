package com.vincentzhang.sixlegrobot.geometry;

import com.vincentzhang.sixlegrobot.math.Vector3D;

public class Line3D {
    private Vector3D p1;
    private Vector3D p2;

    public Line3D(Vector3D p1, Vector3D p2){
        this.p1 = p1;
        this.p2 = p2;
    }

    public Vector3D getStartPoint(){
        return p1;
    }

    public Vector3D getEndPoint(){
        return p2;
    }
}
