package com.example.p.agreality;

import com.example.p.agreality.logics.LoadingLogic;
import com.example.p.engine.AGRenderer;

public class AGLogic implements SimpleLogic {

	private LogicState logicState;

	public AGLogic() {
		logicState = new LogicState();
		logicState.setNewLogic(LoadingLogic.class);
	}

	@Override
	public void init() {
		logicState.getCurrLogic().init();
	}

	@Override
	public void draw(AGRenderer renderer) {
		logicState.getCurrLogic().draw(renderer);
	}

	@Override
	public void onTouch(int actionIndex, float x, float y, int action) {
		logicState.getCurrLogic().onTouch(actionIndex, x, y, action);
	}
}
