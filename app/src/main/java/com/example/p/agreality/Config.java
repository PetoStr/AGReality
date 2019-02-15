package com.example.p.agreality;

public final class Config {

	public static final float MOVE_SPEED = 9.0f;
	public static final float ROTATION_SPEED = (float) (Math.PI / 2.0f);

	public static final long RELEASE_WAIT_TIME_NS = 1000000000;

	public static final float FLOATING_SPEED = 2.0f;

	public static final float EARTH_ROTATION_SPEED = (float) (Math.PI / 8.0f);
	public static final float EARTH_SCALE = 0.005f;

	public static final float WRAITH_SCALE = 0.03f;

	public static final float OFFICE_CHAIR_SCALE = 2.5f;

	public static final float ROOM_SCALE = 5.0f;

	public static final String MODELS_PATH = "models/";
	public static final String MODEL_WRAITH = MODELS_PATH + "wraith/Wraith_Raider_Starship.obj";
	public static final String MODEL_OFFICE_CHAIR = MODELS_PATH + "office_chair/office_chair.obj";
	public static final String MODEL_EARTH = MODELS_PATH + "earth/earth.obj"; // https://free3d.com/3d-model/earth-v1--590680.html
	public static final String MODEL_TABLE = MODELS_PATH + "table/Table.obj"; // https://free3d.com/3d-model/table-89907.html
	public static final String MODEL_ROOM = MODELS_PATH + "room/room.obj"; // https://free3d.com/3d-model/room-48457.html


	public static final String IMAGES_PATH = "imgs/";

	public static final String IMAGE_GRASS = IMAGES_PATH  + "grass.png";

	public static final String IMAGE_MUP = IMAGES_PATH + "mup.png";
	public static final String IMAGE_MDOWN = IMAGES_PATH + "mdown.png";
	public static final String IMAGE_MLEFT = IMAGES_PATH + "mleft.png";
	public static final String IMAGE_MRIGHT = IMAGES_PATH + "mright.png";
	public static final String IMAGE_MFRONT = IMAGES_PATH + "mforward.png";
	public static final String IMAGE_MBACK = IMAGES_PATH + "mbackward.png";

	public static final String IMAGE_RPX = IMAGES_PATH + "rpx.png";
	public static final String IMAGE_RNX = IMAGES_PATH + "rnx.png";
	public static final String IMAGE_RPY = IMAGES_PATH + "rpy.png";
	public static final String IMAGE_RNY = IMAGES_PATH + "rny.png";
	public static final String IMAGE_RPZ = IMAGES_PATH + "rpz.png";
	public static final String IMAGE_RNZ = IMAGES_PATH + "rnz.png";

	public static final String IMAGE_PLUS = IMAGES_PATH + "plus.png";
	public static final String IMAGE_X = IMAGES_PATH + "x.png";
	public static final String IMAGE_REMOVE = IMAGES_PATH + "remove.png";
	public static final String IMAGE_LIGHT_BULB_DIR = IMAGES_PATH + "lbulb_dir.png";

	public static final String IMAGE_EARTH = IMAGES_PATH + "earth.jpg";
	public static final String IMAGE_WRAITH = IMAGES_PATH + "wraith.png";
	public static final String IMAGE_OFFICE_CHAIR = IMAGES_PATH + "office_chair.png";
	public static final String IMAGE_TABLE = IMAGES_PATH + "table.png";
	public static final String IMAGE_ROOM = IMAGES_PATH + "room.png";

	private Config() {

	}

}
