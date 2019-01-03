package com.example.p.engine.entities;

import com.example.p.engine.entities.ModeledEntity;

public class NullModelEntity extends ModeledEntity {

	public NullModelEntity() {
		super(null, null);
	}

	@Override
	public void tick(float frameTime) {

	}

}
