package com.example.p.engine;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class AGSurfaceView extends GLSurfaceView {

	public static final int EGL_CONTEXT_CLIENT_VERSION = 3;

	private AGRenderer agRenderer;

	public AGSurfaceView(Context context) {
		super(context);

		setEGLContextClientVersion(EGL_CONTEXT_CLIENT_VERSION);
		setPreserveEGLContextOnPause(true);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		agRenderer = new AGRenderer();
		setEGLContextFactory(agRenderer);
		setRenderer(agRenderer);
	}

}
