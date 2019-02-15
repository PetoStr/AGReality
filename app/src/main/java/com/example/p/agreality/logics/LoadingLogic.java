package com.example.p.agreality.logics;

import android.util.Pair;

import com.example.p.agreality.Config;
import com.example.p.engine.AGRenderer;
import com.example.p.engine.MainActivity;
import com.example.p.engine.Scene;
import com.example.p.engine.entities.Entity;
import com.example.p.engine.entities.ImageEntity;
import com.example.p.engine.entities.ModeledEntity;
import com.example.p.engine.hardware.AGSensorManager;
import com.example.p.engine.hardware.Camera2Manager;
import com.example.p.engine.hardware.DeviceMovement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Queue;

public class LoadingLogic extends AbstractLogic {

	private Queue<Pair<Class<? extends Entity>, String>> entitiesToLoad = new ArrayDeque<>();
	private Pair<Class<? extends Entity>, String> entityToLoad;

	private int framesRendered;

	private static final float[] textColor = new float[] { 0.5f, 0.5f, 0.5f };

	public LoadingLogic(LogicState logicState) {
		super(logicState);

		fillLoadingQueue();
		entityToLoad = entitiesToLoad.poll();
	}

	private void fillLoadingQueue() {
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_GRASS));

		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_MUP));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_MDOWN));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_MLEFT));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_MRIGHT));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_MFRONT));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_MBACK));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_RPX));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_RNX));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_RPY));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_RNY));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_RPZ));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_RNZ));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_PLUS));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_X));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_REMOVE));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_EARTH));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_WRAITH));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_OFFICE_CHAIR));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_TABLE));
		entitiesToLoad.add(Pair.create(ImageEntity.class, Config.IMAGE_ROOM));

		entitiesToLoad.add(Pair.create(ModeledEntity.class, Config.MODEL_EARTH));
		entitiesToLoad.add(Pair.create(ModeledEntity.class, Config.MODEL_WRAITH));
		entitiesToLoad.add(Pair.create(ModeledEntity.class, Config.MODEL_OFFICE_CHAIR));
		entitiesToLoad.add(Pair.create(ModeledEntity.class, Config.MODEL_TABLE));
		entitiesToLoad.add(Pair.create(ModeledEntity.class, Config.MODEL_ROOM));
	}

	private static void load(Pair<Class<? extends Entity>, String> toLoad) {
		try {
			Constructor<? extends Entity> constructor = toLoad.first.getConstructor(Scene.class, String.class);
			constructor.newInstance(null, toLoad.second);
		} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void finish() {
		logicState.setNewLogic(MainLogic.class);
		logicState.getCurrLogic().init();
	}

	private void addSensors() {
		AGSensorManager sensorManager = MainActivity.getSensorManager();
		sensorManager.addAGSensor(Camera2Manager.INSTANCE);
		sensorManager.addAGSensor(DeviceMovement.INSTANCE);
		sensorManager.startSensors();
	}

	private void loadNextOrFinish() {
		load(entityToLoad);

		if (entitiesToLoad.size() == 0) {
			addSensors();
			finish();
			return;
		}

		entityToLoad = entitiesToLoad.poll();
	}

	@Override
	public void init() {

	}

	@Override
	public void update(float frameTime) {

	}

	@Override
	public void draw(AGRenderer renderer) {
		renderer.clear();

		String text = "loading " + entityToLoad.second;
		renderer.draw_text(text, 33.0f, 33.0f, 1.0f, textColor);

		framesRendered++;

		if (framesRendered == 3) {
			loadNextOrFinish();
			framesRendered = 0;
		}
	}

	@Override
	public void onTouch(int pointerId, float x, float y, int action) {

	}
}
