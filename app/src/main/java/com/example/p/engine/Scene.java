package com.example.p.engine;

import java.util.ArrayList;
import java.util.List;

public class Scene {

	private List<Entity> entities = new ArrayList<>();

	public Camera getCamera() {
		return Camera.getInstance();
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public Entity[] getEntitiesArray() {
		return entities.toArray(new Entity[entities.size()]);
	}

}
