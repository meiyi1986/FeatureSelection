package com.yimei.crossover;

import java.util.List;
import java.util.Random;

import com.yimei.individual.Individual;

public abstract class Crossover {

	protected Random rnd;
	
	public Crossover(Random rnd) {
		this.rnd = rnd;
	}
	
	// generate one offspring
	abstract public Individual execute(List<Individual> parents);
	
	// generate two offsprings
	abstract public List<Individual> execute2(List<Individual> parents);
}
