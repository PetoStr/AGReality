package com.example.p.agreality.entities.models;

import com.example.p.agreality.Config;
import com.example.p.agreality.CustomScene;
import com.example.p.engine.entities.ModeledEntity;

public class Table extends ModeledEntity {

	public Table(CustomScene scene) {
		super(scene, Config.MODEL_TABLE);
	}

}