package com.petostr.p.engine.entities;

import com.petostr.p.engine.Scene;

public class ImageEntity extends Entity {

	private float opacity = 1.0f;

	public ImageEntity(Scene scene, String path) {
		super(scene, new Resource(path, Resource.TYPE_TEXTURE));
	}

	public void setWidth(float width) {
		getScale().x = width;
	}

	public void setHeight(float height) {
		getScale().y = height;
	}

	public float getWidth() {
		return getScale().x;
	}

	public float getHeight() {
		return getScale().y;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	@Override
	public void tick(float frameTime) {

	}
}
