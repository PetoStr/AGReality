package com.example.p.engine.entities;

public class Resource {

	public static final int TYPE_MODEL = 0;
	public static final int TYPE_TEXTURE = 1;

	public static final int PATH_MAX_LEN = 63;

	private String path;

	private int type;

	private int id = -1;

	public Resource(String path, int type) {
		if (path != null && path.length() > PATH_MAX_LEN) {
			throw new IllegalArgumentException("path is too long");
		}

		this.path = path;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}
}
