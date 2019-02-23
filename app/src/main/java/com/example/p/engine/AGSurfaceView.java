package com.example.p.engine;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.example.p.agreality.MainLoop;

public class AGSurfaceView extends GLSurfaceView {

	public static final int EGL_CONTEXT_CLIENT_VERSION = 3;

	private AGRenderer agRenderer;

	public AGSurfaceView(Context context) {
		super(context);

		setEGLContextClientVersion(EGL_CONTEXT_CLIENT_VERSION);
		setPreserveEGLContextOnPause(true);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		AGLoop agLoop = new MainLoop();

		agRenderer = new AGRenderer(agLoop);
		setEGLContextFactory(agRenderer);
		setRenderer(agRenderer);
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		performClick();
		int actionIndex = event.getActionIndex();

		int pointerId = event.getPointerId(actionIndex);
		int action = event.getActionMasked();
		float x = event.getX(actionIndex);
		float y = event.getY(actionIndex);

		agRenderer.onTouch(pointerId, x, y, action);

		return true;
	}

}
