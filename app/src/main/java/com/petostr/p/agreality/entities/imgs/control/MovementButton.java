package com.petostr.p.agreality.entities.imgs.control;

import com.petostr.p.agreality.CustomScene;
import com.petostr.p.engine.entities.ImageEntity;
import com.petostr.p.engine.entities.ModeledEntity;

import org.joml.Vector3f;

public class MovementButton extends ImageEntity {

	private Vector3f move;

	public MovementButton(CustomScene scene, String path, Vector3f move) {
		super(scene, path);

		this.move = move;
	}

	@Override
	public void tick(float frameTime) {
		if (isSelected()) {
			CustomScene scene = (CustomScene) getScene();
			ModeledEntity entity = scene.getSelectedModeledEntity();

			Vector3f d = new Vector3f(move).mul(frameTime);
			entity.move(d);
		}
	}

}