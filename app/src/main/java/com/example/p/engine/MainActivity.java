package com.example.p.engine;

import android.app.Activity;
import android.os.Bundle;

import com.example.p.engine.hardware.Camera2Manager;
import com.example.p.engine.hardware.DeviceMovement;

public class MainActivity extends Activity {

	static {
		System.loadLibrary("native-lib");
	}

	private AGSurfaceView glSurfaceView;

	public static MainActivity INSTANCE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		INSTANCE = this;

		glSurfaceView = new AGSurfaceView(this);
		setContentView(glSurfaceView);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Camera2Manager.INSTANCE.start();
		DeviceMovement.INSTANCE.start();
		glSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		/* TODO DeviceMovement should not be stopped */
		Camera2Manager.INSTANCE.stop();
		DeviceMovement.INSTANCE.stop();
		glSurfaceView.onPause();
	}

}
