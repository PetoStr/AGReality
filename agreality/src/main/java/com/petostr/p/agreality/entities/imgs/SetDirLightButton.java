package com.petostr.p.agreality.entities.imgs;

import com.petostr.p.agreality.Config;
import com.petostr.p.agreality.CustomScene;
import com.petostr.p.engine.entities.ImageEntity;

import org.joml.Vector3f;

public class SetDirLightButton extends ImageEntity {

	public SetDirLightButton(CustomScene scene) {
		super(scene, Config.IMAGE_LIGHT_BULB_DIR);
	}

	@Override
	public void tick(float frameTime) {
		if (isSelected()) {
			float[] vmatrix = getScene().getCamera().getViewMatrix();
			Vector3f dir = new Vector3f(vmatrix[2], vmatrix[6], vmatrix[10]);
			dir.normalize();

			float[] dirArr = { dir.x, dir.y, dir.z };

			getScene().setDirectLightDir(dirArr);
		}
	}

}
