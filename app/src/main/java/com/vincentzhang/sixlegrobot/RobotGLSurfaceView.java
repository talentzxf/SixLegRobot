package com.vincentzhang.sixlegrobot;

import android.content.Context;
import android.opengl.GLSurfaceView;

class RobotGLSurfaceView extends GLSurfaceView {
    private final RobotGLRenderer renderer;

    public RobotGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);

        renderer = new RobotGLRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
    }
}
