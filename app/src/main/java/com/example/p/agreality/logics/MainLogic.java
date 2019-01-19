package com.example.p.agreality.logics;

import android.content.Context;
import android.util.Size;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.example.p.agreality.Config;
import com.example.p.agreality.CustomScene;
import com.example.p.agreality.entities.imgs.SimpleButton;
import com.example.p.agreality.entities.imgs.control.MovementButton;
import com.example.p.agreality.entities.imgs.control.RotationButton;
import com.example.p.agreality.entities.imgs.SetDirLightButton;
import com.example.p.agreality.entities.models.Nanosuit;
import com.example.p.agreality.entities.models.OfficeChair;
import com.example.p.agreality.entities.models.Wraith;
import com.example.p.engine.util.TouchPicker;
import com.example.p.engine.entities.NullImageEntity;
import com.example.p.engine.entities.NullModelEntity;
import com.example.p.engine.AGRenderer;
import com.example.p.engine.App;
import com.example.p.engine.Screen;
import com.example.p.engine.entities.ImageEntity;
import com.example.p.engine.entities.ModeledEntity;
import com.example.p.engine.hardware.Camera2Manager;
import com.example.p.engine.hardware.DeviceMovement;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class MainLogic extends AbstractLogic  {

	private static final float[] textColor = new float[] { 0.5f, 0.5f, 0.5f };

	private CustomScene scene;

	private List<ModeledEntity> modeledEntitiesToAdd = new ArrayList<>();
	private List<ModeledEntity> modeledEntitiesToRemove = new ArrayList<>();

	private List<ImageEntity> imgEntitiesToAdd = new ArrayList<>();

	private List<ImageEntity> controlImgs = new ArrayList<>();
	private List<ImageEntity> listImgs = new ArrayList<>();

	private double startTime;
	private float frameTime;

	public MainLogic(LogicState logicState) {
		super(logicState);
	}

	private void initRotGui() {
		float w = Screen.getWidth() / 8.0f;
		float h = w;

		Vector2f rMid = new Vector2f(Screen.getWidth() - 5 * w / 3, 5 * h / 3);
		float speed = Config.ROTATION_SPEED;

		ImageEntity rotatePY = new RotationButton(scene, Config.IMAGE_GRASS, new Vector3f(0.0f, speed, 0.0f));
		rotatePY.setPosition(new Vector3f(rMid.x - 3 * w / 2, rMid.y - h / 2, 0.0f));
		rotatePY.setWidth(w);
		rotatePY.setHeight(h);
		scene.getImageEntities().add(rotatePY);
		controlImgs.add(rotatePY);

		ImageEntity rotateNY = new RotationButton(scene, Config.IMAGE_GRASS, new Vector3f(0.0f, -speed, 0.0f));
		rotateNY.setPosition(new Vector3f(rMid.x + w / 2, rMid.y - h / 2, 0.0f));
		rotateNY.setWidth(w);
		rotateNY.setHeight(h);
		scene.getImageEntities().add(rotateNY);
		controlImgs.add(rotateNY);

		ImageEntity rotatePX = new RotationButton(scene, Config.IMAGE_GRASS, new Vector3f(speed, 0.0f, 0.0f));
		rotatePX.setPosition(new Vector3f(rMid.x - w / 2, rMid.y + h / 2, 0.0f));
		rotatePX.setWidth(w);
		rotatePX.setHeight(h);
		scene.getImageEntities().add(rotatePX);
		controlImgs.add(rotatePX);

		ImageEntity rotateNX = new RotationButton(scene, Config.IMAGE_GRASS, new Vector3f(-speed, 0.0f, 0.0f));
		rotateNX.setPosition(new Vector3f(rMid.x - w / 2, rMid.y - 3 * h / 2, 0.0f));
		rotateNX.setWidth(w);
		rotateNX.setHeight(h);
		scene.getImageEntities().add(rotateNX);
		controlImgs.add(rotateNX);

		ImageEntity rotatePZ = new RotationButton(scene, Config.IMAGE_GRASS, new Vector3f(0.0f, 0.0f, speed));
		rotatePZ.setPosition(new Vector3f(rMid.x + w / 2, rMid.y - 3 * h / 2, 0.0f));
		rotatePZ.setWidth(w);
		rotatePZ.setHeight(h);
		scene.getImageEntities().add(rotatePZ);
		controlImgs.add(rotatePZ);

		ImageEntity rotateNZ = new RotationButton(scene, Config.IMAGE_GRASS, new Vector3f(0.0f, 0.0f, -speed));
		rotateNZ.setPosition(new Vector3f(rMid.x - 3 * w / 2, rMid.y - 3 * h / 2, 0.0f));
		rotateNZ.setWidth(w);
		rotateNZ.setHeight(h);
		scene.getImageEntities().add(rotateNZ);
		controlImgs.add(rotateNZ);
	}

	private void initMovGui() {
		float w = Screen.getWidth() / 8.0f;
		float h = w;

		Vector2f rMid = new Vector2f(5 * w / 3, 5 * h / 3);
		float speed = Config.MOVE_SPEED;

		ImageEntity movePY = new MovementButton(scene, Config.IMAGE_MUP, new Vector3f(0.0f, speed, 0.0f));
		movePY.setPosition(new Vector3f(rMid.x + w / 2, rMid.y - 3 * h / 2, 0.0f));
		movePY.setWidth(w);
		movePY.setHeight(h);
		scene.getImageEntities().add(movePY);
		controlImgs.add(movePY);

		ImageEntity moveNY = new MovementButton(scene, Config.IMAGE_MDOWN, new Vector3f(0.0f, -speed, 0.0f));
		moveNY.setPosition(new Vector3f(rMid.x - 3 * w / 2, rMid.y - 3 * h / 2, 0.0f));
		moveNY.setWidth(w);
		moveNY.setHeight(h);
		scene.getImageEntities().add(moveNY);
		controlImgs.add(moveNY);

		ImageEntity movePX = new MovementButton(scene, Config.IMAGE_MLEFT, new Vector3f(speed, 0.0f, 0.0f));
		movePX.setPosition(new Vector3f(rMid.x - 3 * w / 2, rMid.y - h / 2, 0.0f));
		movePX.setWidth(w);
		movePX.setHeight(h);
		scene.getImageEntities().add(movePX);
		controlImgs.add(movePX);

		ImageEntity moveNX = new MovementButton(scene, Config.IMAGE_MRIGHT, new Vector3f(-speed, 0.0f, 0.0f));
		moveNX.setPosition(new Vector3f(rMid.x + w / 2, rMid.y - h / 2, 0.0f));
		moveNX.setWidth(w);
		moveNX.setHeight(h);
		scene.getImageEntities().add(moveNX);
		controlImgs.add(moveNX);

		ImageEntity movePZ = new MovementButton(scene, Config.IMAGE_MFRONT, new Vector3f(0.0f, 0.0f, speed));
		movePZ.setPosition(new Vector3f(rMid.x - w / 2, rMid.y + h / 2, 0.0f));
		movePZ.setWidth(w);
		movePZ.setHeight(h);
		scene.getImageEntities().add(movePZ);
		controlImgs.add(movePZ);

		ImageEntity moveNZ = new MovementButton(scene, Config.IMAGE_MBACK, new Vector3f(0.0f, 0.0f, -speed));
		moveNZ.setPosition(new Vector3f(rMid.x - w / 2, rMid.y - 3 * h / 2, 0.0f));
		moveNZ.setWidth(w);
		moveNZ.setHeight(h);
		scene.getImageEntities().add(moveNZ);
		controlImgs.add(moveNZ);
	}

	private void initControlGui() {
		initRotGui();
		initMovGui();

		float w = Screen.getWidth() / 8.0f;
		float h = w;

		float posX = Screen.getWidth() - 5 * w / 3 + w / 2;
		float posY = Screen.getHeight() / 2 - h / 2;

		ImageEntity showList = new SimpleButton(scene, Config.IMAGE_PLUS);
		showList.setPosition(new Vector3f(posX, posY, 0.0f));
		showList.setWidth(w);
		showList.setHeight(h);
		scene.getImageEntities().add(showList);
		controlImgs.add(showList);

		showList.registerOnTouch(() -> {
			showListImgs(true);
			showControlImgs(false);
		});
	}

	private void initListElements() {
		float w = Screen.getWidth() / 8.0f;
		float h = w;

		float posX = 5 * w / 3;
		float posY = 5 * h / 3;

		ImageEntity nanosuitElement = new SimpleButton(scene, Config.IMAGE_GRASS);
		nanosuitElement.setPosition(new Vector3f(posX, posY, 0.0f));
		nanosuitElement.setWidth(w);
		nanosuitElement.setHeight(h);
		nanosuitElement.setVisible(false);
		scene.getImageEntities().add(nanosuitElement);
		listImgs.add(nanosuitElement);

		nanosuitElement.registerOnTouch(() -> {
			ModeledEntity nanosuit = new Nanosuit(scene);
			Vector3f pos = getPositionInFrontOfCamera();
			nanosuit.setPosition(pos);
			modeledEntitiesToAdd.add(nanosuit);

			showControlImgs(true);
			showListImgs(false);
		});

		posX += 2.0f * w;

		ImageEntity wraithElement = new SimpleButton(scene, Config.IMAGE_GRASS);
		wraithElement.setPosition(new Vector3f(posX, posY, 0.0f));
		wraithElement.setWidth(w);
		wraithElement.setHeight(h);
		wraithElement.setVisible(false);
		scene.getImageEntities().add(wraithElement);
		listImgs.add(wraithElement);

		wraithElement.registerOnTouch(() -> {
			ModeledEntity wraith = new Wraith(scene);
			Vector3f pos = getPositionInFrontOfCamera();
			wraith.setPosition(pos);
			modeledEntitiesToAdd.add(wraith);

			showControlImgs(true);
			showListImgs(false);
		});

		posX += 2.0f * w;

		ImageEntity officeChairElement = new SimpleButton(scene, Config.IMAGE_GRASS);
		officeChairElement.setPosition(new Vector3f(posX, posY, 0.0f));
		officeChairElement.setWidth(w);
		officeChairElement.setHeight(h);
		officeChairElement.setVisible(false);
		scene.getImageEntities().add(officeChairElement);
		listImgs.add(officeChairElement);

		officeChairElement.registerOnTouch(() -> {
			ModeledEntity officeChair = new OfficeChair(scene);
			Vector3f pos = getPositionInFrontOfCamera();
			officeChair.setPosition(pos);
			modeledEntitiesToAdd.add(officeChair);

			showControlImgs(true);
			showListImgs(false);
		});
	}

	private void initListGui() {
		float w = Screen.getWidth() / 8.0f;
		float h = w;

		float posX = Screen.getWidth() - 5 * w / 3 + w / 2;
		float posY = Screen.getHeight() / 2 - h / 2;

		ImageEntity hideList = new SimpleButton(scene, Config.IMAGE_X);
		hideList.setPosition(new Vector3f(posX, posY, 0.0f));
		hideList.setWidth(w);
		hideList.setHeight(h);
		hideList.setVisible(false);
		scene.getImageEntities().add(hideList);
		listImgs.add(hideList);

		hideList.registerOnTouch(() -> {
			showControlImgs(true);
			showListImgs(false);
		});

		initListElements();
	}

	private void initGui() {
		initControlGui();
		initListGui();

		float w = Screen.getWidth() / 8.0f;
		float h = w;

		float posX = 5 * w / 3 - 3 * w / 2;
		float posY = Screen.getHeight() / 2 - h / 2;

		ImageEntity removeModel = new SimpleButton(scene, Config.IMAGE_REMOVE);
		removeModel.setPosition(new Vector3f(posX, posY, 0.0f));
		removeModel.setWidth(w);
		removeModel.setHeight(h);
		scene.getImageEntities().add(removeModel);

		removeModel.registerOnTouch(() -> {
			modeledEntitiesToRemove.add(scene.getSelectedModeledEntity());
		});

		posX = Screen.getWidth() - posX - w;
		posY -= 2 * h;

		ImageEntity setDirLight = new SetDirLightButton(scene);
		setDirLight.setPosition(new Vector3f(posX, posY, 0.0f));
		setDirLight.setWidth(w);
		setDirLight.setHeight(h);
		scene.getImageEntities().add(setDirLight);
	}

	private void initScene() {
		scene = new CustomScene();

		scene.setSelectedModeledEntity(new NullModelEntity());

		/*ModeledEntity nanosuit = new Nanosuit(scene);
		nanosuit.setPosition(new Vector3f(0.0f, -10.0f, 35.0f));
		addModeledEntity(nanosuit);

		ModeledEntity nanosuit3 = new Nanosuit(scene);
		nanosuit3.setPosition(new Vector3f(-3.0f, -10.0f, 45.0f));
		addModeledEntity(nanosuit3);

		ModeledEntity nanosuit2 = new Nanosuit(scene);
		nanosuit2.setPosition(new Vector3f(15.0f, -10.0f, 30.0f));
		addModeledEntity(nanosuit2);*/

		/*ModeledEntity wraith = new Wraith(scene);
		wraith.setPosition(new Vector3f(15.0f, -10.0f, 30.0f));
		addModeledEntity(wraith);*/

		//scene.getCamera().move(new Vector3f(0.0f, 0.0f, -30.0f));

		/*e = new Entity(new Vector3f(), new Vector3f(), new Vector3f(1.0f), sculpt);
		e.getOrientation().x -= 90.0f;
		e.getPosition().y -= 25.0f;
		e.getPosition().z = -73.0f;*/
		//scene.getEntities().add(e);

		//scene.getCamera().move(new Vector3f(0.0f, 0.0f, -10.0f));
	}

	private void showControlImgs(boolean show) {
		for (ImageEntity ie : controlImgs) {
			ie.setVisible(show);
		}
	}

	private void showListImgs(boolean show) {
		for (ImageEntity ie : listImgs) {
			ie.setVisible(show);
		}
	}

	private Vector3f getPositionInFrontOfCamera() {
		float[] vMatrix = scene.getCamera().getViewMatrix();
		Vector3f camDir = new Vector3f(-vMatrix[2], -vMatrix[6], -vMatrix[10]);
		Vector3f camPos = scene.getCamera().getPosition();

		return new Vector3f(camDir).mul(30.0f).add(camPos);
	}

	private float getCameraRotation() {
		Display display = ((WindowManager) App.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int dispRotation = display.getRotation();
		return (float) Math.toRadians(Camera2Manager.INSTANCE.getOrientation(dispRotation));
	}

	@Override
	public void init() {
		initScene();
		initGui();

		startTime = System.nanoTime();
	}

	@Override
	public void update(float frameTime) {
		scene.updateAll(frameTime);

		if (!modeledEntitiesToAdd.isEmpty()) {
			scene.getModeledEntities().addAll(modeledEntitiesToAdd);
			modeledEntitiesToAdd.clear();
		}

		if (!modeledEntitiesToRemove.isEmpty()) {
			scene.getModeledEntities().removeAll(modeledEntitiesToRemove);
			modeledEntitiesToRemove.clear();
		}

		if (!imgEntitiesToAdd.isEmpty()) {
			scene.getImageEntities().addAll(imgEntitiesToAdd);
			imgEntitiesToAdd.clear();
		}
	}

	@Override
	public void draw(AGRenderer renderer) {
		renderer.clear();

		if (Camera2Manager.INSTANCE.isAvailable()) {
			Camera2Manager.INSTANCE.updateTexture();

			float rotation = getCameraRotation();
			Size size = Camera2Manager.INSTANCE.getPreviewSize();

			renderer.draw_camera(rotation, size.getWidth(), size.getHeight());
		}

		renderer.draw(scene);

		float h = Screen.getHeight();

		float[] angles = DeviceMovement.INSTANCE.getOrientationAngles();
		renderer.draw_text(String.valueOf((int) Math.toDegrees(angles[0])), 48.0f, h - 144.0f, 1.0f, textColor);
		renderer.draw_text(String.valueOf((int) Math.toDegrees(angles[1])), 48.0f, h - 096.0f, 1.0f, textColor);
		renderer.draw_text(String.valueOf((int) Math.toDegrees(angles[2])), 48.0f, h - 048.0f, 1.0f, textColor);

		float[] accels = DeviceMovement.INSTANCE.getEarthAcceleration();
		renderer.draw_text(String.valueOf(accels[0]), 248.0f, h - 154.0f, 1.0f, textColor);
		renderer.draw_text(String.valueOf(accels[1]), 248.0f, h - 106.0f, 1.0f, textColor);
		renderer.draw_text(String.valueOf(accels[2]), 248.0f, h - 048.0f, 1.0f, textColor);

		float averageFps = 1.0f / frameTime;
		renderer.draw_text(String.format("%.1f", averageFps), 48.0f, h - 240.0f, 1.0f, textColor);

		double currTime = System.nanoTime();
		frameTime = (float) ((currTime - startTime) / 1e9);
		startTime = currTime;
	}

	@Override
	public void onTouch(int pointerId, float x, float y, int action) {
		switch (action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				ImageEntity e = TouchPicker.handleScreenTouchedEntity(scene.getImageEntities(), x, y);
				e.setSelected(true);

				if (e instanceof NullImageEntity) {
					ModeledEntity m = TouchPicker.handleWorldTouchedEntity(scene.getModeledEntities(), scene.getCamera(), x, y);

					scene.getSelectedModeledEntity().setSelected(false);
					scene.setSelectedModeledEntity(m);
					scene.getSelectedModeledEntity().setSelected(true);
				} else {
					scene.getSelectedImageEntities().put(pointerId, e);
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				ImageEntity e0 = scene.getSelectedImageEntities().get(pointerId);
				if (e0 != null) {
					e0.setSelected(false);
					scene.getSelectedImageEntities().remove(pointerId);
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				for (int i = 0; i < scene.getSelectedImageEntities().size(); i++) {
					ImageEntity img = scene.getSelectedImageEntities().valueAt(i);
					img.setSelected(false);
				}
				scene.getSelectedImageEntities().clear();
				break;
		}
	}

}
