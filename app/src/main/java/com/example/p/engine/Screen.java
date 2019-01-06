package com.example.p.engine;

public final class Screen {

	private static int width;
	private static int height;

	public static int getWidth() {
		return width;
	}

	public static void setWidth(int width) {
		Screen.width = width;
	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int height) {
		Screen.height = height;
	}

	public static native float[] get_pmatrix();

}
