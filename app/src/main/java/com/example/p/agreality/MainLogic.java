package com.example.p.agreality;

import com.example.p.engine.AGRenderer;
import com.example.p.engine.Entity;
import com.example.p.engine.Model;
import com.example.p.engine.Scene;
import com.example.p.engine.hardware.Camera2Manager;
import com.example.p.engine.hardware.DeviceMovement;
import com.example.p.engine.util.math.Vector3f;

public class MainLogic implements SimpleLogic {

	private AGRenderer renderer;

	private Scene scene;

	private Entity nanosuit;
	private Entity e;

	private Model sculpt = new Model("models/sculpt.obj".toCharArray());
	private Model m = new Model("models/nanosuit/nanosuit.obj".toCharArray());

	public MainLogic(AGRenderer renderer) {
		this.renderer = renderer;
	}

	private void loadModels() {
		m.load();
		//sculpt.load();
		Camera2Manager.INSTANCE.start();
		DeviceMovement.INSTANCE.start();
	}

	private void initScene() {
		scene = new Scene();

		nanosuit = new Entity(
				new Vector3f(0.0f, -10.0f, 53.0f),
				new Vector3f(),
				new Vector3f(1.0f),
				m
		);
		scene.getEntities().add(nanosuit);

		e = new Entity(new Vector3f(), new Vector3f(), new Vector3f(1.0f), sculpt);
		e.getAngle().x -= 90.0f;
		e.getPosition().y -= 25.0f;
		e.getPosition().z = -73.0f;
		//scene.getEntities().add(e);

		//scene.getCamera().move(new Vector3f(0.0f, 0.0f, -10.0f));
	}

	@Override
	public void init() {
		loadModels();
		initScene();
	}

	@Override
	public void loop() {
		nanosuit.getAngle().y += 1.0f;

		renderer.clear();
		renderer.drawCamera();
		renderer.draw(scene);
	}
}
