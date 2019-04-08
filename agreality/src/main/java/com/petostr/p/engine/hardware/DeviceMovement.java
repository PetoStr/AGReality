package com.petostr.p.engine.hardware;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.petostr.p.engine.App;

public enum DeviceMovement implements SensorEventListener, AGSensor {

	INSTANCE;

	private static final String TAG = "DeviceMovement";

	private static final boolean DISABLE_DISTANCE_MEASUREMENT = true;

	private static final float ALPHA = 0.05f;

	private SensorManager sensorManager;

	private boolean isListening;

	private Sensor accelerometer;
	private Sensor magnetometer;
	private Sensor gyroscope;
	private Sensor linAccel;
	private Sensor gravity;

	private boolean hasGyro;

	private float[] accelerometerData = new float[3];
	private float[] magnetometerData = new float[3];
	private float[] gravityData = new float[3];
	private float[] rotationData = new float[5];
	private boolean hasAccelerometerData;
	private boolean hasMagnetometerData;
	private boolean hasRotationData;

	private final float[] orientationAngles = new float[3];
	float[] position = new float[3];
	private boolean orientationAnglesDataAvailable = false;

	private long timestamp;
	private double[] initVel = new double[3];
	private double[] totalAccel = new double[3];
	private double[] vel = new double[3];

	DeviceMovement() {
		sensorManager = (SensorManager) App.getContext().getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		linAccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

		PackageManager packageManager = App.getContext().getPackageManager();
		hasGyro = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
		Log.i("gyro", "hasGyro = " + hasGyro);
	}

	@Override
	public void start() {
		if (!isListening) {
			if (!DISABLE_DISTANCE_MEASUREMENT) {
				sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_GAME);
			}

			if (hasGyro) {
				if (!DISABLE_DISTANCE_MEASUREMENT) {
					sensorManager.registerListener(this, linAccel,
												   SensorManager.SENSOR_DELAY_GAME);
				}
				sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
			} else {
				sensorManager.registerListener(this, accelerometer,
											   SensorManager.SENSOR_DELAY_GAME);
				sensorManager.registerListener(this, magnetometer,
											   SensorManager.SENSOR_DELAY_GAME);
			}
			isListening = true;
		}
	}

	@Override
	public void stop() {
		if (isListening) {
			sensorManager.unregisterListener(this);
			isListening = false;
		}
	}

	private float[] lowPass(float[] input, float[] output) {
		if (output == null) return input;

		for (int i = 0; i < input.length; i++) {
			output[i] = output[i] + ALPHA * (input[i] - output[i]);
		}

		return output;
	}

	// https://stackoverflow.com/a/12942776
	void dblIntegrate(float[] data, float dt) {
		for (int i = 0; i < 3; i++) {
			if (!hasGyro) {
				data[i] -= gravityData[i];
			}

			totalAccel[i] += data[i] * dt;
			vel[i] = (initVel[i] + (totalAccel[i])) * dt;
			position[i] += vel[i];
			initVel[i] = vel[i];
		}
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		int sensorType = sensorEvent.sensor.getType();

		switch (sensorType) {
			case Sensor.TYPE_ROTATION_VECTOR:
				rotationData = sensorEvent.values.clone();
				hasRotationData = true;
				break;
			case Sensor.TYPE_ACCELEROMETER:
				if (!DISABLE_DISTANCE_MEASUREMENT) {
					if (timestamp != 0) {
						float dt = (sensorEvent.timestamp - timestamp) / 1000000000.0f;
						dblIntegrate(sensorEvent.values.clone(), dt);
					}
					timestamp = sensorEvent.timestamp;
				}
				accelerometerData = lowPass(sensorEvent.values.clone(), accelerometerData);
				hasAccelerometerData = true;
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				magnetometerData = lowPass(sensorEvent.values.clone(), magnetometerData);
				hasMagnetometerData = true;
				break;
			case Sensor.TYPE_LINEAR_ACCELERATION:
				if (timestamp != 0) {
					float dt = (sensorEvent.timestamp - timestamp) / 1000000000.0f;
					dblIntegrate(sensorEvent.values.clone(), dt);
				}
				timestamp = sensorEvent.timestamp;
				break;
			case Sensor.TYPE_GRAVITY:
				gravityData = sensorEvent.values.clone();
				break;
		}

		updateOrientation();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {

	}

	public boolean isOrientationAnglesDataAvailable() {
		return orientationAnglesDataAvailable;
	}

	public float[] getOrientationAngles() {
		return orientationAngles;
	}

	public float[] getPosition() {
		return position;
	}

	private void updateOrientation() {
		float[] rotationMatrix = new float[9];

		if (hasGyro && hasRotationData) {
			SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationData);
			hasRotationData = false;
		} else if (!hasGyro && hasMagnetometerData && hasAccelerometerData) {
			boolean res = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData,
					magnetometerData);
			if (!res) {
				Log.e(TAG, "failed to get rotation");
				return;
			}
			hasMagnetometerData = false;
			hasAccelerometerData = false;
		} else {
			return;
		}

		float[] outR = new float[9];
		SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X,
				SensorManager.AXIS_Z, outR);
		SensorManager.getOrientation(outR, orientationAngles);
		orientationAnglesDataAvailable = true;
	}
}
