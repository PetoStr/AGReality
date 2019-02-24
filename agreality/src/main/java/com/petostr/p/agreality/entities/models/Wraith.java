package com.petostr.p.agreality.entities.models;

import com.petostr.p.agreality.Config;
import com.petostr.p.agreality.CustomScene;
import com.petostr.p.engine.entities.ModeledEntity;

import org.joml.Vector3f;

public class Wraith extends ModeledEntity {

	private long startTime;

	private boolean isFloating;

	private float originY;

	private float diffY;

	private float totalTime;

	public Wraith(CustomScene scene) {
		super(scene, Config.MODEL_WRAITH);

		setScale(new Vector3f(Config.WRAITH_SCALE));
		startTime = System.nanoTime();
		originY = getPosition().y;
		isFloating = true;
	}

	@Override
	public void tick(float frameTime) {
		if (!isSelected()) {
			if (isFloating) {
				diffY = (float) Math.sin(totalTime * Config.FLOATING_SPEED) / 2.0f;
				getPosition().y = originY + diffY;
				totalTime = (float) ((totalTime + frameTime) % (2.0d * Math.PI));
			} else {
				long currTime = System.nanoTime();
				if (currTime - startTime >= Config.RELEASE_WAIT_TIME_NS) {
					isFloating = true;
					originY = getPosition().y - diffY;
				}
			}
		} else {
			isFloating = false;
			startTime = System.nanoTime();
		}
	}

	@Override
	public void setPosition(Vector3f position) {
		super.setPosition(position);

		originY = getPosition().y;
	}

}
