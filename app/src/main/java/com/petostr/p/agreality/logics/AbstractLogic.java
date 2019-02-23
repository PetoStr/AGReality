package com.petostr.p.agreality.logics;

abstract class AbstractLogic implements SimpleLogic {

	LogicState logicState;

	AbstractLogic(LogicState logicState) {
		this.logicState = logicState;
	}

}
