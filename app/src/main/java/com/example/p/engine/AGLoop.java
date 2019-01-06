package com.example.p.engine;

public interface AGLoop {

	void init();

	void draw(AGRenderer renderer);

	void onTouch(int pointerId, float x, float y, int action);

}
