package com.example.p.agreality.entities.models;

import com.example.p.agreality.Config;
import com.example.p.agreality.CustomScene;
import com.example.p.engine.Scene;
import com.example.p.engine.entities.ModeledEntity;

import org.joml.Vector3f;

public class Wraith extends ModeledEntity {

	private long startTime;

	private boolean floating;

	private float originY;

	private float diffY;

	private float totalTime;

	public Wraith(CustomScene scene) {
		super(scene, Config.MODEL_WRAITH);

		setScale(new Vector3f(0.03f));
		startTime = System.nanoTime();
		originY = getPosition().y;
		floating = true;
	}

	@Override
	public void tick(float frameTime) {
		if (!isSelected()) {
			if (!floating) {
				long currTime = System.nanoTime();
				if (currTime - startTime >= 1e9) {
					floating = true;
					originY = getPosition().y - diffY;
				}
			} else {
				diffY = (float) Math.sin(totalTime * Config.FLOATING_SPEED) / 2.0f;
				getPosition().y = originY + diffY;
				totalTime = (float) ((totalTime + frameTime) % (2.0d * Math.PI));
			}
		} else {
			floating = false;
			startTime = System.nanoTime();
		}
	}

	@Override
	public void setPosition(Vector3f position) {
		super.setPosition(position);

		originY = getPosition().y;
	}

}
