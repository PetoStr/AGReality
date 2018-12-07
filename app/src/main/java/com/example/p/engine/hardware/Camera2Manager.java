package com.example.p.engine.hardware;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;

import com.example.p.engine.MainActivity;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Camera2Manager implements ActivityCompat.OnRequestPermissionsResultCallback, SurfaceTexture.OnFrameAvailableListener, Hardware {

	public static final Camera2Manager INSTANCE = new Camera2Manager();

	private static final String TAG = Camera2Manager.class.getName();

	private String cameraId;

	private Context context;

	private Size previewSize;

	public CameraDevice cameraDevice;

	private SurfaceTexture surfaceTexture;

	private CaptureRequest.Builder captureRequestBuilder;

	private CaptureRequest captureRequest;

	private CameraCaptureSession captureSession;

	private HandlerThread cameraThread;

	private Handler cameraHandler;

	private Semaphore cameraOpenCloseLock = new Semaphore(1);

	private int sensorOrientation;

	private boolean isAvailable;

	private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

	static {
		ORIENTATIONS.append(Surface.ROTATION_0, 90);
		ORIENTATIONS.append(Surface.ROTATION_90, 180);
		ORIENTATIONS.append(Surface.ROTATION_180, 270);
		ORIENTATIONS.append(Surface.ROTATION_270, 0);
	}

	public Camera2Manager() {
		context = MainActivity.INSTANCE.getApplicationContext();
	}

	public void createSurfaceTexture(int textureId) {
		surfaceTexture = new SurfaceTexture(textureId);
	}

	@Override
	public void start() {
		startCameraThread();
		if (surfaceTexture != null) {
			openCamera();
			surfaceTexture.setOnFrameAvailableListener(this, cameraHandler);
		}
	}

	@Override
	public void stop() {
		closeCamera();
		stopCameraThread();
	}

	private void startCameraThread() {
		if (cameraHandler == null) {
			cameraThread = new HandlerThread("CameraThread");
			cameraThread.start();
			cameraHandler = new Handler(cameraThread.getLooper());
		}
	}

	private void stopCameraThread() {
		cameraThread.quitSafely();
		try {
			cameraThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cameraHandler = null;
	}

	private void openCamera() {
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(MainActivity.INSTANCE, new String[] { Manifest.permission.CAMERA }, 1);
			return;
		}

		CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
		try {
			for (String cameraId : manager.getCameraIdList()) {
				CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
				if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
					Log.d(TAG, cameraId);
					StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
					previewSize = map.getOutputSizes(SurfaceTexture.class)[0];

					// Find out if we need to swap dimension to get the preview size relative to sensor
					// coordinate.
					int displayRotation = MainActivity.INSTANCE.getWindowManager().getDefaultDisplay().getRotation();
					//noinspection ConstantConditions
					sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

					if (displayRotation == Configuration.ORIENTATION_PORTRAIT) {
						Size old = previewSize;
						previewSize = new Size(old.getHeight(), old.getWidth());
					}

					this.cameraId = cameraId;
					if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
						throw new RuntimeException("Time out waiting to lock camera opening.");
					}
					manager.openCamera(cameraId, stateCallback, cameraHandler);
					break;
				}
			}
		} catch (CameraAccessException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void closeCamera() {
		try {
			cameraOpenCloseLock.acquire();
			if (captureSession != null) {
				captureSession.close();
				captureSession = null;
			}

			if (cameraDevice != null) {
				cameraDevice.close();
				cameraDevice = null;
			}
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
		} finally {
			cameraOpenCloseLock.release();
		}
	}

	private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
		@Override
		public void onOpened(@NonNull CameraDevice camera) {
			Log.v(TAG, "onOpened(): " + camera.getId());
			cameraOpenCloseLock.release();
			cameraDevice = camera;
			startPreview();
		}

		@Override
		public void onDisconnected(@NonNull CameraDevice camera) {
			Log.v(TAG, "onDisconnected(): " + camera.getId());
			cameraOpenCloseLock.release();
			camera.close();
			cameraDevice = null;
		}

		@Override
		public void onError(@NonNull CameraDevice camera, int error) {
			Log.e(TAG, "onError(): " + camera.getId() + ", error: " + error);
			cameraOpenCloseLock.release();
			camera.close();
			cameraDevice = null;
		}
	};

	private void startPreview() {
		surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

		Surface surface = new Surface(surfaceTexture);
		try {
			captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
			captureRequestBuilder.addTarget(surface);

			cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
				@Override
				public void onConfigured(@NonNull CameraCaptureSession session) {
					Log.v(TAG, "onConfigured(): " + session);
					try {
						captureRequest = captureRequestBuilder.build();
						captureSession = session;
						captureSession.setRepeatingRequest(captureRequest, null, cameraHandler);
					} catch (CameraAccessException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onConfigureFailed(@NonNull CameraCaptureSession session) {
					Log.e(TAG, "onConfigureFailed(): " + session);
				}
			}, cameraHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	public int getOrientation(int rotation) {
		// Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
		// We have to take that into account and rotate JPEG properly.
		// For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
		// For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
		return (ORIENTATIONS.get(rotation) + sensorOrientation + 270) % 360;
	}

	public void update() {
		surfaceTexture.updateTexImage();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		start();
	}

	@Override
	public void onFrameAvailable(SurfaceTexture surfaceTexture) {
		isAvailable = true;
	}

	public Size getPreviewSize() {
		return previewSize;
	}

	public boolean isAvailable() {
		return isAvailable;
	}
}
