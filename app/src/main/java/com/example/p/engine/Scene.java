package com.example.p.engine;

import com.example.p.engine.entities.ImageEntity;
import com.example.p.engine.entities.ModeledEntity;

import java.util.ArrayList;
import java.util.List;

public class Scene {

	private List<ModeledEntity> modeledEntities = new ArrayList<>();
	private List<ModeledEntity> modeledEntitiesToRemove = new ArrayList<>();

	private List<ImageEntity> imageEntities = new ArrayList<>();
	private List<ImageEntity> imageEntitiesToRemove = new ArrayList<>();

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

		if (modeledEntitiesToRemove.size() != 0) {
			modeledEntities.removeAll(modeledEntitiesToRemove);
			modeledEntitiesToRemove.clear();
		}

		if (imageEntitiesToRemove.size() != 0) {
			imageEntities.removeAll(imageEntitiesToRemove);
			imageEntitiesToRemove.clear();
		}
	}

	public void addModeledEntity(ModeledEntity entity) {
		modeledEntities.add(entity);
	}

	public void addImageEntity(ImageEntity entity) {
		imageEntities.add(entity);
	}

	public void removeModeledEntity(ModeledEntity entity) {
		modeledEntitiesToRemove.add(entity);
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

	public void setModeledEntities(List<ModeledEntity> modeledEntities) {
		this.modeledEntities = modeledEntities;
	}

	public List<ImageEntity> getImageEntities() {
		return imageEntities;
	}

	public void setImageEntities(List<ImageEntity> imageEntities) {
		this.imageEntities = imageEntities;
	}

	public ModeledEntity[] getModeledEntitiesArray() {
		return getModeledEntities().toArray(new ModeledEntity[0]);
	}

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
