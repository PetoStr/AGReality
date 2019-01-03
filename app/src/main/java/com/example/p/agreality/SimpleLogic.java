package com.example.p.agreality;

import com.example.p.engine.AGRenderer;

public interface SimpleLogic {

	void init();

	void draw(AGRenderer renderer);

	void onTouch(int pointerId, float x, float y, int action);

}
