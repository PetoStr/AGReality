package com.example.p.agreality.entities.models;

import com.example.p.agreality.Config;
import com.example.p.agreality.CustomScene;
import com.example.p.engine.entities.ModeledEntity;

import org.joml.Vector3f;

public class OfficeChair extends ModeledEntity {

	public OfficeChair(CustomScene scene) {
		super(scene, Config.MODEL_OFFICE_CHAIR);
		setScale(new Vector3f(Config.OFFICE_CHAIR_SCALE));
	}

}
