package com.example.p.engine;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.p.agreality.AGLogic;
import com.example.p.agreality.SimpleLogic;
import com.example.p.engine.hardware.Camera2Manager;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

public class AGRenderer implements GLSurfaceView.Renderer, GLSurfaceView.EGLContextFactory {

	private static final String TAG = "AGRenderer";

	private SimpleLogic agReality;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Camera2Manager.INSTANCE.createSurfaceTexture(create_oes_texture());

		init(App.getContext().getAssets());

		agReality = new AGLogic();
		agReality.init();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		agReality.draw(this);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		surface_changed(width, height);
		Screen.setWidth(width);
		Screen.setHeight(height);
	}

	private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

	@Override
	public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
		int[] attrib_list = { EGL_CONTEXT_CLIENT_VERSION, AGSurfaceView.EGL_CONTEXT_CLIENT_VERSION,
				EGL10.EGL_NONE };

		return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attrib_list);
	}

	@Override
	public void destroyContext(EGL10 egl, EGLDisplay display,
							   EGLContext context) {
		on_destroy();
		Camera2Manager.INSTANCE.eglContextDestroyed();
		if (!egl.eglDestroyContext(display, context)) {
			Log.e(TAG, "display:" + display + " context: " + context);
		}
	}

	public void onTouch(int pointerId, float x, float y, int action) {
		if (agReality != null) {
			agReality.onTouch(pointerId, x, Screen.getHeight() - y, action);
		}
	}

	private native void init(AssetManager assetManager);

	private native void on_destroy();

	public native void clear();

	public native void draw(Scene scene);

	public native void draw_camera(float rotation, int width, int height);

	public native void draw_text(String text, float x, float y, float scale, float[] color);

	private native int create_oes_texture();

	private native void surface_changed(int width, int height);


}
