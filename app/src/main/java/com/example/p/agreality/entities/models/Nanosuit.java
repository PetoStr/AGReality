package com.example.p.agreality.entities.models;

import com.example.p.agreality.Config;
import com.example.p.agreality.CustomScene;
import com.example.p.engine.Scene;
import com.example.p.engine.entities.ModeledEntity;

import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Nanosuit extends ModeledEntity {

	public Nanosuit(CustomScene scene) {
		super(scene, Config.MODEL_NANOSUIT);
	}

	@Override
	public void tick(float frameTime) {
		//getOrientation().y += 1.0f;
		//getOrientation().z += 1.0f;
	}

}
