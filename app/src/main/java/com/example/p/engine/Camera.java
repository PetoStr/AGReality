package com.example.p.engine;

import android.opengl.Matrix;

import com.example.p.engine.hardware.DeviceMovement;

import org.joml.Vector3f;

public class Camera implements Movable {

	private Vector3f position = new Vector3f();
	private Vector3f orientation = new Vector3f();

	private float[] viewMatrix = new float[16];

	private void calculateViewMatrix() {
		float x = (float) (position.x + Math.sin(orientation.x) * Math.cos(orientation.y));
		float y = (float) (position.y + Math.sin(orientation.y));
		float z = (float) (position.z + Math.cos(orientation.x) * Math.cos(orientation.y));

		float[] eye = {
				position.x,
				position.y,
				position.z
		};

		float[] up = {
				(float) (Math.cos(orientation.x) * Math.sin(orientation.z)),
				(float) (Math.cos(orientation.z)),
				(float) (Math.sin(-orientation.x) * Math.sin(orientation.z))
		};

		Matrix.setLookAtM(viewMatrix, 0,
				eye[0], eye[1], eye[2],
				x, y, z,
				up[0], up[1], up[2]);
	}

	public void update() {
		float[] rotation = DeviceMovement.INSTANCE.getOrientationAngles();
		orientation.set(-rotation[0], -rotation[1], -rotation[2]);

		calculateViewMatrix();
	}

	@Override
	public void move(Vector3f d) {
		position.add(d);
	}

	@Override
	public void rotate(Vector3f d) {
		orientation.add(d);
	}

	public float[] getViewMatrix() {
		return viewMatrix;
	}

	public Vector3f getPosition() {
		return position;
	}

	@SuppressWarnings("unused") // called via JNI
	public float[] getPositionArray() {
		return new float[] { position.x, position.y, position.z };
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getOrientation() {
		return orientation;
	}

	public void setOrientation(Vector3f orientation) {
		this.orientation = orientation;
	}
}
