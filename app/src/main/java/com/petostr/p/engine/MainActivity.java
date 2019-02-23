package com.petostr.p.engine;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.petostr.p.engine.hardware.AGSensorManager;

public class MainActivity extends Activity
		implements ActivityCompat.OnRequestPermissionsResultCallback {

	private AGSurfaceView glSurfaceView;

	private static AGSensorManager sensorManager;

	static {
		System.loadLibrary("native-lib");
	}

	private void initSurfaceView() {
		glSurfaceView = new AGSurfaceView(this);
		setContentView(glSurfaceView);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		MainActivity.sensorManager = new AGSensorManager();

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
				!= PackageManager.PERMISSION_GRANTED) {
			String[] permissions = new String[] { Manifest.permission.CAMERA };
			ActivityCompat.requestPermissions(this, permissions, 1);
		} else {
			initSurfaceView();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		sensorManager.startSensors();
		if (glSurfaceView != null) {
			glSurfaceView.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (glSurfaceView != null) {
			glSurfaceView.onPause();
		}
		sensorManager.pauseSensors();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		for (int result : grantResults) {
			if (result == PackageManager.PERMISSION_DENIED) {
				System.exit(3);
			}
		}

		initSurfaceView();
	}

	public static AGSensorManager getSensorManager() {
		return sensorManager;
	}
}
