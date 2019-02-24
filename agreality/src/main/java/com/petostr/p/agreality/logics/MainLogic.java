package com.petostr.p.agreality.logics;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Size;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.petostr.p.agreality.Config;
import com.petostr.p.agreality.CustomScene;
import com.petostr.p.agreality.entities.imgs.SimpleButton;
import com.petostr.p.agreality.entities.imgs.control.MovementButton;
import com.petostr.p.agreality.entities.imgs.control.RotationButton;
import com.petostr.p.agreality.entities.imgs.SetDirLightButton;
import com.petostr.p.agreality.entities.models.Earth;
import com.petostr.p.agreality.entities.models.OfficeChair;
import com.petostr.p.agreality.entities.models.Room;
import com.petostr.p.agreality.entities.models.Table;
import com.petostr.p.agreality.entities.models.Wraith;
import com.petostr.p.engine.util.TouchPicker;
import com.petostr.p.engine.entities.NullImageEntity;
import com.petostr.p.engine.entities.NullModelEntity;
import com.petostr.p.engine.AGRenderer;
import com.petostr.p.engine.App;
import com.petostr.p.engine.Screen;
import com.petostr.p.engine.entities.ImageEntity;
import com.petostr.p.engine.entities.ModeledEntity;
import com.petostr.p.engine.hardware.Camera2Manager;
import com.petostr.p.engine.hardware.DeviceMovement;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

	private boolean isDebug;

	public MainLogic(LogicState logicState) {
		super(logicState);
		ApplicationInfo appInfo = App.getContext().getApplicationInfo();
		isDebug = (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
	}

	private void initRotGui() {
		float size = Screen.getWidth() / 8.0f;

		Vector2f rMid = new Vector2f(Screen.getWidth() - 5 * size / 3, 5 * size / 3);
		float speed = Config.ROTATION_SPEED;

		ImageEntity rotatePX =
				new RotationButton(scene, Config.IMAGE_RPX, new Vector3f(speed, 0.0f, 0.0f));
		rotatePX.setPosition(new Vector3f(rMid.x - size / 2, rMid.y + size / 2, 0.0f));
		rotatePX.setWidth(size);
		rotatePX.setHeight(size);
		scene.getImageEntities().add(rotatePX);
		controlImgs.add(rotatePX);

		ImageEntity rotateNX =
				new RotationButton(scene, Config.IMAGE_RNX, new Vector3f(-speed, 0.0f, 0.0f));
		rotateNX.setPosition(new Vector3f(rMid.x - size / 2, rMid.y - 3 * size / 2, 0.0f));
		rotateNX.setWidth(size);
		rotateNX.setHeight(size);
		scene.getImageEntities().add(rotateNX);
		controlImgs.add(rotateNX);

		ImageEntity rotatePY =
				new RotationButton(scene, Config.IMAGE_RPY, new Vector3f(0.0f, speed, 0.0f));
		rotatePY.setPosition(new Vector3f(rMid.x - 3 * size / 2, rMid.y - size / 2, 0.0f));
		rotatePY.setWidth(size);
		rotatePY.setHeight(size);
		scene.getImageEntities().add(rotatePY);
		controlImgs.add(rotatePY);

		ImageEntity rotateNY =
				new RotationButton(scene, Config.IMAGE_RNY, new Vector3f(0.0f, -speed, 0.0f));
		rotateNY.setPosition(new Vector3f(rMid.x + size / 2, rMid.y - size / 2, 0.0f));
		rotateNY.setWidth(size);
		rotateNY.setHeight(size);
		scene.getImageEntities().add(rotateNY);
		controlImgs.add(rotateNY);

		ImageEntity rotatePZ =
				new RotationButton(scene, Config.IMAGE_RPZ, new Vector3f(0.0f, 0.0f, speed));
		rotatePZ.setPosition(new Vector3f(rMid.x + size / 2, rMid.y - 3 * size / 2, 0.0f));
		rotatePZ.setWidth(size);
		rotatePZ.setHeight(size);
		scene.getImageEntities().add(rotatePZ);
		controlImgs.add(rotatePZ);

		ImageEntity rotateNZ =
				new RotationButton(scene, Config.IMAGE_RNZ, new Vector3f(0.0f, 0.0f, -speed));
		rotateNZ.setPosition(new Vector3f(rMid.x - 3 * size / 2, rMid.y - 3 * size / 2, 0.0f));
		rotateNZ.setWidth(size);
		rotateNZ.setHeight(size);
		scene.getImageEntities().add(rotateNZ);
		controlImgs.add(rotateNZ);
	}

	private void initMovGui() {
		float size = Screen.getWidth() / 8.0f;

		Vector2f rMid = new Vector2f(5 * size / 3, 5 * size / 3);
		float speed = Config.MOVE_SPEED;

		ImageEntity movePY =
				new MovementButton(scene, Config.IMAGE_MUP, new Vector3f(0.0f, speed, 0.0f));
		movePY.setPosition(new Vector3f(rMid.x + size / 2, rMid.y - 3 * size / 2, 0.0f));
		movePY.setWidth(size);
		movePY.setHeight(size);
		scene.getImageEntities().add(movePY);
		controlImgs.add(movePY);

		ImageEntity moveNY =
				new MovementButton(scene, Config.IMAGE_MDOWN, new Vector3f(0.0f, -speed, 0.0f));
		moveNY.setPosition(new Vector3f(rMid.x - 3 * size / 2, rMid.y - 3 * size / 2, 0.0f));
		moveNY.setWidth(size);
		moveNY.setHeight(size);
		scene.getImageEntities().add(moveNY);
		controlImgs.add(moveNY);

		ImageEntity movePX =
				new MovementButton(scene, Config.IMAGE_MLEFT, new Vector3f(speed, 0.0f, 0.0f));
		movePX.setPosition(new Vector3f(rMid.x - 3 * size / 2, rMid.y - size / 2, 0.0f));
		movePX.setWidth(size);
		movePX.setHeight(size);
		scene.getImageEntities().add(movePX);
		controlImgs.add(movePX);

		ImageEntity moveNX =
				new MovementButton(scene, Config.IMAGE_MRIGHT, new Vector3f(-speed, 0.0f, 0.0f));
		moveNX.setPosition(new Vector3f(rMid.x + size / 2, rMid.y - size / 2, 0.0f));
		moveNX.setWidth(size);
		moveNX.setHeight(size);
		scene.getImageEntities().add(moveNX);
		controlImgs.add(moveNX);

		ImageEntity movePZ =
				new MovementButton(scene, Config.IMAGE_MFRONT, new Vector3f(0.0f, 0.0f, speed));
		movePZ.setPosition(new Vector3f(rMid.x - size / 2, rMid.y + size / 2, 0.0f));
		movePZ.setWidth(size);
		movePZ.setHeight(size);
		scene.getImageEntities().add(movePZ);
		controlImgs.add(movePZ);

		ImageEntity moveNZ =
				new MovementButton(scene, Config.IMAGE_MBACK, new Vector3f(0.0f, 0.0f, -speed));
		moveNZ.setPosition(new Vector3f(rMid.x - size / 2, rMid.y - 3 * size / 2, 0.0f));
		moveNZ.setWidth(size);
		moveNZ.setHeight(size);
		scene.getImageEntities().add(moveNZ);
		controlImgs.add(moveNZ);
	}

	private void initControlGui() {
		initRotGui();
		initMovGui();

		float size = Screen.getWidth() / 8.0f;

		float posX = Screen.getWidth() - 5 * size / 3 + size / 2;
		float posY = Screen.getHeight() / 2 - size / 2;

		ImageEntity showList = new SimpleButton(scene, Config.IMAGE_PLUS);
		showList.setPosition(new Vector3f(posX, posY, 0.0f));
		showList.setWidth(size);
		showList.setHeight(size);
		scene.getImageEntities().add(showList);
		controlImgs.add(showList);

		showList.registerOnTouch(() -> {
			showListImgs(true);
			showControlImgs(false);
		});
	}

	private void initListElements() {
		float size = Screen.getWidth() / 8.0f;

		float posX = 5 * size / 3;
		float posY = 5 * size / 3;

		ImageEntity earthElement = new SimpleButton(scene, Config.IMAGE_EARTH);
		earthElement.setPosition(new Vector3f(posX, posY, 0.0f));
		earthElement.setWidth(size);
		earthElement.setHeight(size);
		earthElement.setVisible(false);
		scene.getImageEntities().add(earthElement);
		listImgs.add(earthElement);

		earthElement.registerOnTouch(() -> {
			ModeledEntity earth = new Earth(scene);
			Vector3f pos = getPositionInFrontOfCamera();
			earth.setPosition(pos);
			modeledEntitiesToAdd.add(earth);

			showControlImgs(true);
			showListImgs(false);
		});

		posX += 2.0f * size;

		ImageEntity wraithElement = new SimpleButton(scene, Config.IMAGE_WRAITH);
		wraithElement.setPosition(new Vector3f(posX, posY, 0.0f));
		wraithElement.setWidth(size);
		wraithElement.setHeight(size);
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

		posX += 2.0f * size;

		ImageEntity officeChairElement = new SimpleButton(scene, Config.IMAGE_OFFICE_CHAIR);
		officeChairElement.setPosition(new Vector3f(posX, posY, 0.0f));
		officeChairElement.setWidth(size);
		officeChairElement.setHeight(size);
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

		posY += 2.0f * size;

		ImageEntity tableElement = new SimpleButton(scene, Config.IMAGE_TABLE);
		tableElement.setPosition(new Vector3f(posX, posY, 0.0f));
		tableElement.setWidth(size);
		tableElement.setHeight(size);
		tableElement.setVisible(false);
		scene.getImageEntities().add(tableElement);
		listImgs.add(tableElement);

		tableElement.registerOnTouch(() -> {
			ModeledEntity table = new Table(scene);
			Vector3f pos = getPositionInFrontOfCamera();
			table.setPosition(pos);
			modeledEntitiesToAdd.add(table);

			showControlImgs(true);
			showListImgs(false);
		});

		posX -= 2.0f * size;

		ImageEntity roomElement = new SimpleButton(scene, Config.IMAGE_ROOM);
		roomElement.setPosition(new Vector3f(posX, posY, 0.0f));
		roomElement.setWidth(size);
		roomElement.setHeight(size);
		roomElement.setVisible(false);
		scene.getImageEntities().add(roomElement);
		listImgs.add(roomElement);

		roomElement.registerOnTouch(() -> {
			ModeledEntity table = new Room(scene);
			modeledEntitiesToAdd.add(table);

			showControlImgs(true);
			showListImgs(false);
		});
	}

	private void initListGui() {
		float size = Screen.getWidth() / 8.0f;

		float posX = Screen.getWidth() - 5 * size / 3 + size / 2;
		float posY = Screen.getHeight() / 2 - size / 2;

		ImageEntity hideList = new SimpleButton(scene, Config.IMAGE_X);
		hideList.setPosition(new Vector3f(posX, posY, 0.0f));
		hideList.setWidth(size);
		hideList.setHeight(size);
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

		float size = Screen.getWidth() / 8.0f;

		float posX = 5 * size / 3 - 3 * size / 2;
		float posY = Screen.getHeight() / 2 - size / 2;

		ImageEntity removeModel = new SimpleButton(scene, Config.IMAGE_REMOVE);
		removeModel.setPosition(new Vector3f(posX, posY, 0.0f));
		removeModel.setWidth(size);
		removeModel.setHeight(size);
		scene.getImageEntities().add(removeModel);

		removeModel.registerOnTouch(() ->
			modeledEntitiesToRemove.add(scene.getSelectedModeledEntity())
		);

		posX = Screen.getWidth() - posX - size;
		posY -= 2 * size;

		ImageEntity setDirLight = new SetDirLightButton(scene);
		setDirLight.setPosition(new Vector3f(posX, posY, 0.0f));
		setDirLight.setWidth(size);
		setDirLight.setHeight(size);
		scene.getImageEntities().add(setDirLight);
	}

	private void initScene() {
		scene = new CustomScene();
		scene.setSelectedModeledEntity(new NullModelEntity());
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

		return new Vector3f(camDir).mul(Config.DISPLACEMENT_FROM_CAMERA).add(camPos);
	}

	private float getCameraRotation() {
		WindowManager windowManager =
				(WindowManager) App.getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
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

		if (isDebug) {
			float h = Screen.getHeight();

			float[] angles = DeviceMovement.INSTANCE.getOrientationAngles();
			renderer.draw_text(String.valueOf((int) Math.toDegrees(angles[0])),
							   48.0f, h - 144.0f, 1.0f, textColor);
			renderer.draw_text(String.valueOf((int) Math.toDegrees(angles[1])),
							   48.0f, h - 096.0f, 1.0f, textColor);
			renderer.draw_text(String.valueOf((int) Math.toDegrees(angles[2])),
							   48.0f, h - 048.0f, 1.0f, textColor);

			float[] accels = DeviceMovement.INSTANCE.getPosition();
			renderer.draw_text(String.valueOf(accels[0]),
							   248.0f, h - 154.0f, 1.0f, textColor);
			renderer.draw_text(String.valueOf(accels[1]),
							   248.0f, h - 106.0f, 1.0f, textColor);
			renderer.draw_text(String.valueOf(accels[2]),
							   248.0f, h - 048.0f, 1.0f, textColor);

			float averageFps = 1.0f / frameTime;
			renderer.draw_text(String.format(Locale.US, "%.1f", averageFps),
							   48.0f, h - 240.0f, 1.0f, textColor);
		}

		double currTime = System.nanoTime();
		frameTime = (float) ((currTime - startTime) / 1e9);
		startTime = currTime;
	}

	@Override
	public void onTouch(int pointerId, float x, float y, int action) {
		switch (action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				ImageEntity e =
						TouchPicker.handleScreenTouchedEntity(scene.getImageEntities(), x, y);
				e.setSelected(true);

				if (e instanceof NullImageEntity) {
					ModeledEntity m =
							TouchPicker.handleWorldTouchedEntity(scene.getModeledEntities(),
																 scene.getCamera(), x, y);

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
