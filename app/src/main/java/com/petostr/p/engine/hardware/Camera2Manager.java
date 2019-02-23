package com.petostr.p.engine.hardware;

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
import android.util.Range;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.petostr.p.engine.App;

import java.util.Collections;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public enum Camera2Manager implements SurfaceTexture.OnFrameAvailableListener, AGSensor {

	INSTANCE;

	private static final String TAG = "Camera2Manager";

	private String cameraId;

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

	public void createSurfaceTexture(int textureId) {
		surfaceTexture = new SurfaceTexture(textureId);
		surfaceTexture.setOnFrameAvailableListener(this, cameraHandler);
	}

	@Override
	public void start() {
		if (surfaceTexture == null) {
			return;
		}

		startCameraThread();
		if (surfaceTexture != null && captureSession == null) {
			openCamera();
		}
	}

	@Override
	public void stop() {
		isAvailable = false;
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
		int cameraPermission =
				ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.CAMERA);
		if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
			return;
		}

		CameraManager manager =
				(CameraManager) App.getContext().getSystemService(Context.CAMERA_SERVICE);
		try {
			for (String cameraId : manager.getCameraIdList()) {
				CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
				Integer lens = characteristics.get(CameraCharacteristics.LENS_FACING);
				if (lens == null || lens != CameraCharacteristics.LENS_FACING_BACK) {
					continue;
				}

				Log.d(TAG, cameraId);
				StreamConfigurationMap map =
						characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
				if (map == null) {
					Log.e(TAG, "map is null");
					return;
				}
				previewSize = map.getOutputSizes(SurfaceTexture.class)[0];

				// Find out if we need to swap dimension to get the preview size relative to sensor
				// coordinate.
				WindowManager windowManager =
						(WindowManager) App.getContext().getSystemService(Context.WINDOW_SERVICE);
				Display display = windowManager.getDefaultDisplay();
				int displayRotation = display.getRotation();
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
		} catch (CameraAccessException | InterruptedException e) {
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

	private Range<Integer> getRange() {
		CameraManager cameraManager =
				(CameraManager) App.getContext().getSystemService(Context.CAMERA_SERVICE);
		CameraCharacteristics chars = null;

		try {
			chars = cameraManager.getCameraCharacteristics(cameraId);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}

		if (chars == null) {
			Log.e(TAG, "no camera characteristics found");
			return null;
		}

		Range<Integer>[] ranges =
				chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
		if (ranges == null) {
			Log.e(TAG, "no fps ranges found");
			return null;
		}

		Range<Integer> result = null;
		for (Range<Integer> range : ranges) {
			int upper = range.getUpper();

			if (upper >= 30) {
				if (result == null || upper < result.getUpper()) {
					result = range;
				}
			}
		}

		return result;
	}

	private void startPreview() {
		surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

		Surface surface = new Surface(surfaceTexture);
		try {
			captureRequestBuilder =
					cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
			captureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, getRange());
			captureRequestBuilder.set(CaptureRequest.CONTROL_AE_LOCK, false);
			captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
									  CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
			captureRequestBuilder.addTarget(surface);

			cameraDevice.createCaptureSession(Collections.singletonList(surface),
											  new CameraCaptureSession.StateCallback() {
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

	public void updateTexture() {
		surfaceTexture.updateTexImage();
	}

	public void eglContextDestroyed() {
		surfaceTexture = null;
		isAvailable = false;
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
