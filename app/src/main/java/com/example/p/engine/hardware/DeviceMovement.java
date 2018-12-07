package com.example.p.engine.hardware;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.p.engine.MainActivity;

public class DeviceMovement implements SensorEventListener, Hardware {

	public static final DeviceMovement INSTANCE = new DeviceMovement();

	private static final float ALPHA = 0.05f;

	private SensorManager sensorManager;

	private Sensor accelerometer;
	private Sensor magnetometer;
	private Sensor gravitySensor;

	private float[] accelerometerData = new float[3];
	private float[] magnetometerData = new float[3];
	private float[] gravityData = new float[3];
	private boolean hasAccelerometerData;
	private boolean hasMagnetoMeterData;

	private float[] accelStorage = new float[3];

	private final float[] rotationMatrix = new float[9];
	private final float[] orientationAngles = new float[3];

	float[] earthAcceleration = new float[16];

	private DeviceMovement() {
		Context context = MainActivity.INSTANCE.getApplicationContext();
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
	}

	@Override
	public void start() {
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
		//sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void stop() {
		sensorManager.unregisterListener(this);
	}

	private float[] lowPass(float[] input, float[] output) {
		if (output == null) return input;

		for (int i = 0; i < input.length; i++) {
			output[i] = output[i] + ALPHA * (input[i] - output[i]);
		}
		return output;
	}

	private float[] filterData(float[] output, float[] data, float filteringFactor) {
		for (int i = 0; i < data.length; i++) {
			accelStorage[i] = data[i] * filteringFactor + accelStorage[i] * (1.0f - filteringFactor);
			output[i] = data[i] - accelStorage[i];
		}

		return output;
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		int sensorType = sensorEvent.sensor.getType();

		switch (sensorType) {
			case Sensor.TYPE_ACCELEROMETER:
				//filterData(accelerometerData, sensorEvent.values, 0.1f);
				accelerometerData = lowPass(sensorEvent.values.clone(), accelerometerData);
				hasAccelerometerData = true;
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				//exponentialSmoothing(sensorEvent.values, magnetometerData, 0.5f);
				magnetometerData = lowPass(sensorEvent.values.clone(), magnetometerData);
				hasMagnetoMeterData = true;
				break;
			case Sensor.TYPE_GRAVITY:
				System.arraycopy(sensorEvent.values, 0, gravityData, 0, gravityData.length);
				break;
		}
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

	public static float[] adjustAccelOrientation(int displayRotation, float[] eventValues) {
		float[] adjustedValues = new float[3];

		final int axisSwap[][] = {
				{ 1, -1, 0, 1 },     // ROTATION_0
				{ -1, -1, 1, 0 },     // ROTATION_90
				{ -1, 1, 0, 1 },     // ROTATION_180
				{ 1, 1, 1, 0 } }; // ROTATION_270

		final int[] as = axisSwap[displayRotation];
		adjustedValues[0] = (float) as[0] * eventValues[as[2]];
		adjustedValues[1] = (float) as[1] * eventValues[as[3]];
		adjustedValues[2] = eventValues[2];

		return adjustedValues;
	}

	public void updateOrientationAndAccel() {
		if (!hasMagnetoMeterData || !hasAccelerometerData) {
			return;
		}
		hasMagnetoMeterData = false;
		hasAccelerometerData = false;
		boolean res = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData,
				magnetometerData);
		if (!res) {
			System.err.println("failed");
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
