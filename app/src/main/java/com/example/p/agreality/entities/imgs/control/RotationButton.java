package com.example.p.agreality.entities.imgs.control;

import com.example.p.agreality.CustomScene;
import com.example.p.engine.entities.ImageEntity;
import com.example.p.engine.entities.ModeledEntity;

import org.joml.Vector3f;

public class RotationButton extends ImageEntity {

	Vector3f rotation;

	public RotationButton(CustomScene scene, String path, Vector3f rotation) {
		super(scene, path);

		this.rotation = rotation;
	}

	@Override
	public void tick(float frameTime) {
		if (isSelected()) {
			CustomScene scene = (CustomScene) getScene();
			ModeledEntity entity = scene.getSelectedModeledEntity();

			Vector3f d = new Vector3f(rotation).mul(frameTime);
			entity.getOrientation().rotateXYZ(d.x, d.y, d.z);
		}
	}
}
