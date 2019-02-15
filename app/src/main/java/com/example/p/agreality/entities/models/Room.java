package com.example.p.agreality.entities.models;

import com.example.p.agreality.Config;
import com.example.p.agreality.CustomScene;
import com.example.p.engine.entities.ModeledEntity;

import org.joml.Vector3f;

public class Room extends ModeledEntity {

	public Room(CustomScene scene) {
		super(scene, Config.MODEL_ROOM);
		setScale(new Vector3f(Config.ROOM_SCALE));
	}

}
