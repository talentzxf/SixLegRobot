package com.vincentzhang.sixlegrobot;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.vincentzhang.sixlegrobot.geometry.Line3D;
import com.vincentzhang.sixlegrobot.math.Vector3D;
import com.vincentzhang.sixlegrobot.robot.DrawableJoint;
import com.vincentzhang.sixlegrobot.robot.Joint;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class RobotGLRenderer implements GLSurfaceView.Renderer {



    Joint joint;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        joint = new DrawableJoint(new Vector3D(0.f, 0.f,0.f),
                new Vector3D(1.f, 0.f,0.f),
                10.f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
