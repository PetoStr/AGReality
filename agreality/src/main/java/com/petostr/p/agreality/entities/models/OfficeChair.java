package com.petostr.p.agreality.entities.models;

import com.petostr.p.agreality.Config;
import com.petostr.p.agreality.CustomScene;
import com.petostr.p.engine.entities.ModeledEntity;

import org.joml.Vector3f;

public class OfficeChair extends ModeledEntity {

	public OfficeChair(CustomScene scene) {
		super(scene, Config.MODEL_OFFICE_CHAIR);
		setScale(new Vector3f(Config.OFFICE_CHAIR_SCALE));
	}

}
