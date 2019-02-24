package com.petostr.p.engine;

import com.petostr.p.engine.entities.ImageEntity;
import com.petostr.p.engine.entities.ModeledEntity;

import java.util.ArrayList;
import java.util.List;

public class Scene {

	private List<ModeledEntity> modeledEntities = new ArrayList<>();
	private List<ImageEntity> imageEntities = new ArrayList<>();

	private Camera camera = new Camera();

	public void updateAll(float frameTime) {
		camera.update();

		for (ModeledEntity entity : modeledEntities) {
			entity.tick(frameTime);
			entity.updateModelMatrix();
		}

		for (ImageEntity entity : imageEntities) {
			entity.tick(frameTime);
			entity.updateModelMatrix();
		}
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Camera getCamera() {
		return camera;
	}

	public List<ModeledEntity> getModeledEntities() {
		return modeledEntities;
	}

	public List<ImageEntity> getImageEntities() {
		return imageEntities;
	}

	@SuppressWarnings("unused") // called via JNI
	public ModeledEntity[] getModeledEntitiesArray() {
		return getModeledEntities().toArray(new ModeledEntity[0]);
	}

	@SuppressWarnings("unused") // called via JNI
	public ImageEntity[] getImageEntitiesArray() {
		return getImageEntities().toArray(new ImageEntity[0]);
	}

	public void setDirectLightDir(float[] directLightDir) {
		if (directLightDir.length != 3) {
			throw new IllegalArgumentException();
		}

		set_dir_light_dir(directLightDir);
	}

	private native void set_dir_light_dir(float[] dir_light_dir);
}
