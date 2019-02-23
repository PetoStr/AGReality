package com.petostr.p.agreality;

import com.petostr.p.agreality.logics.LoadingLogic;
import com.petostr.p.agreality.logics.LogicState;
import com.petostr.p.agreality.logics.SimpleLogic;
import com.petostr.p.engine.AGLoop;
import com.petostr.p.engine.AGRenderer;

public class MainLoop implements AGLoop {

	private final LogicState logicState;

	private long startTime;

	public MainLoop() {
		logicState = new LogicState();
		logicState.setNewLogic(LoadingLogic.class);

		startTime = System.nanoTime();
	}

	@Override
	public void init() {
		logicState.getCurrLogic().init();
	}

	@Override
	public void draw(AGRenderer renderer) {
		SimpleLogic logic = logicState.getCurrLogic();

		long currTime = System.nanoTime();
		float frameTime = (float) ((currTime - startTime) / 1e9);
		startTime = currTime;

		logic.update(frameTime);
		logic.draw(renderer);
	}

	@Override
	public void onTouch(int actionIndex, float x, float y, int action) {
		logicState.getCurrLogic().onTouch(actionIndex, x, y, action);
	}

}