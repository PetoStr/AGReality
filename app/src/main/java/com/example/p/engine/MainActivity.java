package com.example.p.engine;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.example.p.engine.hardware.AGSensorManager;
public class MainActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

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
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		for (int result : grantResults) {
			if (result == PackageManager.PERMISSION_DENIED) {
				System.exit(3);
			}
		}

		initSurfaceView();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		MainActivity.sensorManager = new AGSensorManager();

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, 1);
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
		/* TODO DeviceMovement should not be stopped */
		sensorManager.pauseSensors();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public static AGSensorManager getSensorManager() {
		return sensorManager;
	}
}
