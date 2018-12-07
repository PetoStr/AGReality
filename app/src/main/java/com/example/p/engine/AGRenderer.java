package com.example.p.engine;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.util.Size;

import com.example.p.agreality.MainLogic;
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
		Camera.getInstance().reset();

		Camera2Manager.INSTANCE.createSurfaceTexture(create_oes_texture());

		init(MainActivity.INSTANCE.getAssets());

		agReality = new MainLogic(this);
		agReality.init();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		agReality.loop();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		surface_changed(width, height);
	}

	private int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

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
		if (!egl.eglDestroyContext(display, context)) {
			Log.e(TAG, "display:" + display + " context: " + context);
		}
	}

	public void drawCamera() {
		if (Camera2Manager.INSTANCE.isAvailable()) {
			Camera2Manager.INSTANCE.update();
			int dispRotation = MainActivity.INSTANCE
					.getWindowManager().getDefaultDisplay().getRotation();
			float rotation = (float) Math.toRadians(Camera2Manager.INSTANCE.getOrientation(dispRotation));

			Size size = Camera2Manager.INSTANCE.getPreviewSize();

			draw_camera(rotation, size.getWidth(), size.getHeight());
		}
	}

	private native void init(AssetManager assetManager);

	private native void on_destroy();

	public native void clear();

	public native void draw(Scene scene);

	private native void draw_camera(float rotation, int width, int height);

	private native int create_oes_texture();

	private native void surface_changed(int width, int height);


}
