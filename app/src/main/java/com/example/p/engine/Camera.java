package com.example.p.engine;

import android.opengl.Matrix;

import com.example.p.engine.hardware.DeviceMovement;
import com.example.p.engine.util.math.Vector3f;

public class Camera implements Movable {

	private Vector3f position = new Vector3f();
	private Vector3f angle = new Vector3f();

	private static Camera camera = new Camera();

	private Camera() {
	}

	public static Camera getInstance() {
		return camera;
	}

	public float[] getViewMatrix() {
		float[] viewMatrix = new float[16];

		DeviceMovement.INSTANCE.updateOrientationAndAccel();
		float[] orientation = DeviceMovement.INSTANCE.getOrientationAngles();

		float x = (float) (position.x + Math.sin(-orientation[0]) * Math.cos(-orientation[1]));
		float y = (float) (position.y + Math.sin(-orientation[1]));
		float z = (float) (position.z + Math.cos(-orientation[0]) * Math.cos(-orientation[1]));

		float[] eye = {
				position.x,
				position.y,
				position.z
		};

		//angle.x += 0.02f;

		float[] center = {
				(float) (position.x + Math.sin(angle.y) * Math.cos(angle.x)),
				(float) (position.y + Math.sin(angle.x)),
				(float) (position.z + Math.cos(angle.y) * Math.cos(angle.x))
		};
        /*float dist = (float) Math.sqrt(center[0] * center[0] + center[1] * center[1] + center[2] * center[2]);
        center[0] = center[0] / dist;
		center[1] = center[1] / dist;
		center[2] = center[2] / dist;*/

		float[] up = { 0.0f, 1.0f, 0.0f };

        /*Matrix.setLookAtM(viewMatrix,0,
                0.0f, 0.0f, 0.0f,
                center[0], center[1], center[2],
                up[0], up[1], up[2]);*/
		Matrix.setLookAtM(viewMatrix, 0,
				eye[0], eye[1], eye[2],
				x, y, z,
				up[0], up[1], up[2]);

		return viewMatrix;
	}

	public void reset() {
		this.position = new Vector3f();
		this.angle = new Vector3f();
	}

	@Override
	public void move(Vector3f d) {
		position.x += d.x;
		position.y += d.y;
		position.z += d.z;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getAngle() {
		return angle;
	}

	public void setAngle(Vector3f angle) {
		this.angle = angle;
	}
}
