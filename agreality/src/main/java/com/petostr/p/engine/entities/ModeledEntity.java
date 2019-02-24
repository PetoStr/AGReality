package com.petostr.p.engine.entities;

import com.petostr.p.engine.Scene;

public class ModeledEntity extends Entity {

	public ModeledEntity(Scene scene, String path) {
		super(scene, new Resource(path, Resource.TYPE_MODEL));
	}

	@Override
	public void tick(float frameTime) {

	}
}
