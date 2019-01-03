package com.example.p.agreality.logics;

import com.example.p.agreality.LogicState;
import com.example.p.agreality.SimpleLogic;

public abstract class AbstractLogic implements SimpleLogic {

	protected LogicState logicState;

	public AbstractLogic(LogicState logicState) {
		this.logicState = logicState;
	}

}
