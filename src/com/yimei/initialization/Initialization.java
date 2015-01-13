package com.yimei.initialization;

import java.util.Random;

import com.yimei.individual.Individual;
import com.yimei.problem.Problem;

public abstract class Initialization {
	
	protected Random rnd;
	
	public Initialization(Random rnd) {
		this.rnd = rnd;
	}

	abstract public Individual execute(Problem problem);
}
