package com.example.p.engine.hardware;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.p.engine.MainActivity;

public enum DeviceMovement implements SensorEventListener, Hardware {

	INSTANCE;

	private static final float ALPHA = 0.05f;

	private SensorManager sensorManager;

	private boolean isListening;

	private Sensor accelerometer;
	private Sensor magnetometer;
	private Sensor gyroscope;

	private boolean hasGyro;

	private float[] accelerometerData = new float[3];
	private float[] magnetometerData = new float[3];
	private float[] rotationData = new float[5];
	private boolean hasAccelerometerData;
	private boolean hasMagnetoMeterData;
	private boolean hasRotationData;

	private float[] accelStorage = new float[3];

	private final float[] orientationAngles = new float[3];

	float[] earthAcceleration = new float[16];

	DeviceMovement() {
		Context context = MainActivity.INSTANCE.getApplicationContext();
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

		hasGyro = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
		Log.i("gyro", "hasGyro = " + hasGyro);
	}

	@Override
	public void start() {
		if (!isListening) {
			if (hasGyro) {
				sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
			} else {
				sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
				sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
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

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		int sensorType = sensorEvent.sensor.getType();

		switch (sensorType) {
			case Sensor.TYPE_ROTATION_VECTOR:
				rotationData = sensorEvent.values.clone();
				hasRotationData = true;
				break;
			case Sensor.TYPE_ACCELEROMETER:
				accelerometerData = lowPass(sensorEvent.values.clone(), accelerometerData);
				hasAccelerometerData = true;
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				magnetometerData = lowPass(sensorEvent.values.clone(), magnetometerData);
				hasMagnetoMeterData = true;
				break;
		}

		updateOrientationAndAccel();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {

	}

	public float[] getOrientationAngles() {
		return orientationAngles;
	}

	public float[] getEarthAcceleration() {
		return earthAcceleration;
	}

	private float[] average(float[][] data) {
		int dataLen = data.length;
		int n = data[0].length;

		float[] ret = new float[n];

		for (int i = 0; i < n; i++) {
			ret[i] = dataLen / 2 * (data[0][i] + data[dataLen - 1][i]) / dataLen;
		}

		return ret;
	}

	public void updateOrientationAndAccel() {
		float[] rotationMatrix = new float[9];

		if (hasRotationData) {
			SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationData);
			hasRotationData = false;
		} else if (hasMagnetoMeterData && hasAccelerometerData) {
			boolean res = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData,
					magnetometerData);
			if (!res) {
				System.err.println("failed");
				return;
			}
			hasMagnetoMeterData = false;
			hasAccelerometerData = false;
		} else {
			return;
		}

		float[] outR = new float[9];
		SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X,
				SensorManager.AXIS_Z, outR);
		SensorManager.getOrientation(outR, orientationAngles);

		//orientationAngles[1] += Math.PI / 2.0f;

		/*if(gravityData[2] < 0) {
			if (orientationAngles[1] > 0) {
				orientationAngles[1] = (float) (Math.PI - orientationAngles[1]);
			}
			else {
				orientationAngles[1] = (float) (-Math.PI - orientationAngles[1]);
			}
		}*/

		//orientationAngles[0] = (float) Math.toRadians(orientationData[0]);
		//orientationAngles[1] = (float) Math.toRadians(orientationData[1]);
		//orientationAngles[2] = (float) Math.toRadians(orientationData[2]);

		/*for (int i = 0; i < orientationAngles.length; i++) {
			//orientationAngles[i] /= Math.PI;
			orientationAngles[i] *= 100;
			orientationAngles[i] = (int)orientationAngles[i];
			orientationAngles[i] /= 100;
		}*/

		/*Log.d("yaw", ":" + Math.toDegrees(orientationAngles[0]));
		Log.d("pitch", ":" + Math.toDegrees(orientationAngles[1]));
		Log.d("roll", ":" + Math.toDegrees(orientationAngles[2]));*/

		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/

		//System.out.print("--------");

        /*int displayRotation = Main.INSTANCE.getResources().getConfiguration().orientation;
        float[] orientation = adjustAccelOrientation(displayRotation, accelerometerData);

        System.out.print("[\t");
        System.out.print(orientation[0]);
        System.out.print(",\t");
        System.out.print(orientation[1]);
        System.out.print(",\t");
        System.out.print(orientation[2]);
        System.out.println("]");*/

		/*SensorManager.getRotationMatrix(rotationMatrix, null,
				gravityData, magnetometerData);

		SensorManager.getOrientation(rotationMatrix, orientationAngles);

		float[] inv = new float[16];

		Matrix.invertM(inv, 0, rotationMatrix, 0);
		Matrix.multiplyMV(earthAcceleration, 0, inv, 0, accelerometerData, 0);

		System.out.print("[\t");
		System.out.print(orientationAngles[0]);
		System.out.print(",\t");
		System.out.print(orientationAngles[1]);
		System.out.print(",\t");
		System.out.print(orientationAngles[2]);
		System.out.println("]");*/
	}
}
