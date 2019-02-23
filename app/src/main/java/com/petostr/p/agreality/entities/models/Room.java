package com.petostr.p.agreality.entities.models;

import com.petostr.p.agreality.Config;
import com.petostr.p.agreality.CustomScene;
import com.petostr.p.engine.entities.ModeledEntity;

import org.joml.Vector3f;

public class Room extends ModeledEntity {

	public Room(CustomScene scene) {
		super(scene, Config.MODEL_ROOM);
		setScale(new Vector3f(Config.ROOM_SCALE));
	}

}
