package com.example.p.engine;

import android.opengl.Matrix;

import com.example.p.engine.util.math.Vector3f;

public class Entity implements Movable {

	private Vector3f position;
	private Vector3f scale;
	private Vector3f angle;

	private Model model;

	public Entity(Vector3f position, Vector3f angle, Vector3f scale, Model model) {
		this.position = position;
		this.angle = angle;
		this.scale = scale;

		this.model = model;
	}

	public float[] getModelMatrix() {
		float[] modelMatrix = new float[16];

		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, position.x, position.y, position.z);
		Matrix.rotateM(modelMatrix, 0, angle.x, 1, 0, 0);
		Matrix.rotateM(modelMatrix, 0, angle.y, 0, 1, 0);
		Matrix.rotateM(modelMatrix, 0, -angle.z, 0, 0, 1);
		Matrix.scaleM(modelMatrix, 0, scale.x, scale.y, scale.z);

		return modelMatrix;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

	public Vector3f getAngle() {
		return angle;
	}

	public void setAngle(Vector3f angle) {
		this.angle = angle;
	}

	public Model getModel() {
		return model;
	}

	@Override
	public void move(Vector3f d) {
		position.x += d.x;
		position.y += d.y;
		position.z += d.z;
	}
}
