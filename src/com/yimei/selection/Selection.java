package com.yimei.selection;

import java.util.List;
import java.util.Random;

import com.yimei.individual.Individual;

public abstract class Selection {

	protected Random rnd;
	
	public Selection(Random rnd) {
		this.rnd = rnd;
	}
	
	abstract public List<Individual> execute(List<Individual> population);
}
