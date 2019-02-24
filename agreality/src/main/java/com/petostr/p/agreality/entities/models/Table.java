package com.petostr.p.agreality.entities.models;

import com.petostr.p.agreality.Config;
import com.petostr.p.agreality.CustomScene;
import com.petostr.p.engine.entities.ModeledEntity;

public class Table extends ModeledEntity {

	public Table(CustomScene scene) {
		super(scene, Config.MODEL_TABLE);
	}

}
