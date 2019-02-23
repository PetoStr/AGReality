package com.petostr.p.agreality.logics;

import com.petostr.p.engine.AGRenderer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class LogicState {

	private SimpleLogic currLogic;

	public LogicState() {
		currLogic = new NullLogic();
	}

	public SimpleLogic getCurrLogic() {
		return currLogic;
	}

	public void setNewLogic(Class<? extends AbstractLogic> logic) {
		Constructor<? extends AbstractLogic> constructor;
		try {
			constructor = logic.getConstructor(LogicState.class);
			currLogic = constructor.newInstance(this);
		} catch (NoSuchMethodException
				| IllegalAccessException
				| InstantiationException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static class NullLogic implements SimpleLogic {
		@Override
		public void init() {

		}

		@Override
		public void update(float frameTime) {

		}

		@Override
		public void draw(AGRenderer renderer) {

		}

		@Override
		public void onTouch(int pointerId, float x, float y, int action) {

		}
	}

}
