package com.example.p.engine;

public class Model {

	public static final Model WRAITH = new Model("models/Wraith_Raider_Starship.obj".toCharArray());
	public static final Model STALL = new Model("models/stall.obj".toCharArray());
	public static final Model NANOSUIT = new Model("models/nanosuit/nanosuit.obj".toCharArray());

	public final static int PATH_MAX_LEN = 63;

	private int id = -1;

	private char[] path;

	public Model(char[] path) {
		if (path.length > PATH_MAX_LEN) {
			throw new IllegalArgumentException("path is too long");
		}

		this.path = new char[path.length + 1];
		System.arraycopy(path, 0, this.path, 0, path.length);
		this.path[path.length] = '\0';
	}

	public int getId() {
		return id;
	}

	public char[] getPath() {
		return path;
	}

	public void setId(int id) {
		this.id = id;
	}

	public native void load();

}
