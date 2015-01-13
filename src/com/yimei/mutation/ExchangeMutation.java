package com.yimei.mutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.yimei.individual.Individual;
import com.yimei.problem.Problem;

import weka.core.Instances;

public class ExchangeMutation extends Mutation {
	
	private int old;
	private int nu;
	
	public ExchangeMutation(Random rnd, int old, int nu) {
		super(rnd);
		this.old = old;
		this.nu = nu;
	}

	public Individual execute(Individual parent) {
		Individual offspring = new Individual(parent);
		
		if (parent.getBitString().get(nu)) {
			System.err.println("During exchange, the new feature is already selected!");
		}
		
		if (!parent.getBitString().get(old)) {
			System.err.println("During exchange, the old feature is not selected!");
		}
		
		offspring.removeFeature(old);
		offspring.addFeature(nu);
		
		return offspring;
	}
}
