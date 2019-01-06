package com.example.p.agreality.logics;

public abstract class AbstractLogic implements SimpleLogic {

	protected LogicState logicState;

	public AbstractLogic(LogicState logicState) {
		this.logicState = logicState;
	}

}
