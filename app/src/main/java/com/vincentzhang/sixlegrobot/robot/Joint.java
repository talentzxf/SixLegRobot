package com.vincentzhang.sixlegrobot.robot;

import android.opengl.GLES20;

import com.vincentzhang.sixlegrobot.geometry.Line3D;
import com.vincentzhang.sixlegrobot.math.Vector3D;

public class Joint {
    protected Line3D line3D;

    public Joint(Vector3D startPoint, Vector3D dir, float length){
        Vector3D endPoint = new Vector3D(startPoint.add(dir.normalize().multi(length)));
        line3D = new Line3D(startPoint, endPoint);
    }
}
