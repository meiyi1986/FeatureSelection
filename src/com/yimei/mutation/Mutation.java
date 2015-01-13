package com.yimei.mutation;

import java.util.Random;

import com.yimei.individual.Individual;

public abstract class Mutation {

	protected Random rnd;
	
	public Mutation(Random rnd) {
		this.rnd = rnd;
	}
	
	abstract public Individual execute(Individual indi);
}
