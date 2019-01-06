package com.example.p.engine.entities;

import com.example.p.engine.Movable;
import com.example.p.engine.Scene;

import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class Entity implements Movable, TouchListener {

	protected Resource resource;

	private Vector3f position = new Vector3f();
	private Vector3f scale = new Vector3f(1.0f);
	private Quaternionf orientation = new Quaternionf();

	private boolean isSelected;

	private boolean isVisible = true;

	float[] modelMatrix = new float[16];

	private Scene scene;

	private Vector3f min = new Vector3f(-1.0f, -1.0f, -1.0f);
	private Vector3f max = new Vector3f(1.0f, 1.0f, 1.0f);
	private Vector3f center = new Vector3f(1.0f, 1.0f, 1.0f);

	private TouchListener touchListener;

	public Entity(Scene scene, Resource resource) {
		this.scene = scene;
		this.resource = resource;

		if (resource.getPath() != null) {
			load();
		}
	}

	public abstract void tick(float frameTime);

	@Override
	public void move(Vector3f d) {
		Vector3f rd = new Vector3f(d).rotate(orientation);
		position.add(rd);
	}

	@Override
	public void rotate(Vector3f d) {
		orientation.rotateX(d.x);
		orientation.rotateY(d.y);
		orientation.rotateZ(d.z);
	}

	@Override
	public void onTouch() {
		if (touchListener != null) {
			touchListener.onTouch();
		}
	}

	public void updateModelMatrix() {
		Matrix4f mMatrix = new Matrix4f().identity();
		mMatrix.translate(position);
		mMatrix.rotate(orientation);
		mMatrix.translate(-center.x, -center.y, -center.z);
		mMatrix.scale(scale);
		mMatrix.get(modelMatrix, 0);
	}

	public void registerOnTouch(TouchListener touchListener) {
		this.touchListener = touchListener;
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
		center.mul(this.scale);
	}

	public Quaternionf getOrientation() {
		return orientation;
	}

	public void setOrientation(Quaternionf orientation) {
		this.orientation = orientation;
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public Resource getResource() {
		return resource;
	}

	public AABBf getAabbf() {
		Vector4f min = new Vector4f(getMin(), 1.0f);
		Vector4f max = new Vector4f(getMax(), 1.0f);

		Matrix4f mMatrix = new Matrix4f().set(getModelMatrix());

		min.mul(mMatrix);
		max.mul(mMatrix);

		AABBf aabbf = new AABBf(min.x, min.y, min.z, max.x, max.y, max.z);
		aabbf.correctBounds();

		return aabbf;
	}

	public Vector3f getMin() {
		return min;
	}

	public void setMin(Vector3f min) {
		this.min = min;
	}

	public void setMin(float x, float y, float z) {
		this.min = new Vector3f(x, y, z);
	}

	public Vector3f getMax() {
		return max;
	}

	public void setMax(Vector3f max) {
		this.max = max;
	}

	public void setMax(float x, float y, float z) {
		this.max = new Vector3f(x, y, z);
	}

	public Vector3f getCenter() {
		return center;
	}

	public void setCenter(Vector3f center) {
		this.center = center;
	}

	public void setCenter(float x, float y, float z) {
		this.center = new Vector3f(x, y, z);
	}

	public boolean isSelected() {
		return isSelected;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public float[] getModelMatrix() {
		return modelMatrix;
	}

	private native void load();
}
