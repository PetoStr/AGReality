package com.example.p.engine.entities;

import com.example.p.engine.entities.ImageEntity;

public class NullImageEntity extends ImageEntity {

	public NullImageEntity() {
		super(null, null);
	}

	@Override
	public void tick(float frameTime) {

	}

}
