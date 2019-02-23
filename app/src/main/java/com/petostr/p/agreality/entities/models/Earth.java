package com.petostr.p.agreality.entities.models;

import com.petostr.p.agreality.Config;
import com.petostr.p.agreality.CustomScene;
import com.petostr.p.engine.entities.ModeledEntity;

import org.joml.Vector3f;

public class Earth extends ModeledEntity  {

	private long startTime;

	private boolean isRotating;

	public Earth(CustomScene scene) {
		super(scene, Config.MODEL_EARTH);
		setScale(new Vector3f(Config.EARTH_SCALE));

		isRotating = true;
	}

	@Override
	public void tick(float frameTime) {
		if (!isSelected()) {
			if (isRotating) {
				getOrientation().rotateY(Config.EARTH_ROTATION_SPEED * frameTime);
			} else {
				long currTime = System.nanoTime();
				if (currTime - startTime >= Config.RELEASE_WAIT_TIME_NS) {
					isRotating = true;
				}
			}
		} else {
			isRotating = false;
			startTime = System.nanoTime();
		}
	}

}
