package com.yimei.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.yimei.individual.Individual;
import com.yimei.utils.Utilities;

public class SinglePointCrossover extends Crossover {

	public SinglePointCrossover(Random rnd) {
		super(rnd);
	}
	
	@Override
	public Individual execute(List<Individual> parents) {
		
		// check whether there are two parents
		if (parents.size() != 2) {
			System.err.println("Must have two parents for crossover!");
		}
		
		Individual parent1 = parents.get(0);
		Individual parent2 = parents.get(1);
		
		int numOfFeatures = parent1.getBitString().size();
		
		// randomly sample the crossover bit
		int crossoverBit = Utilities.randInt(0, numOfFeatures-1, rnd);
		
		Individual offspring = new Individual(numOfFeatures);
		for (int i = 0; i <= crossoverBit; i++) {
			if (parent1.getBitString().get(i)) {
				offspring.addFeature(i);
			}
		}
		for (int i = crossoverBit + 1; i < numOfFeatures; i++) {
			if (parent2.getBitString().get(i)) {
				offspring.addFeature(i);
			}
		}
		
		return offspring;
	}

	@Override
	public List<Individual> execute2(List<Individual> parents) {
		
		// check whether there are two parents
		if (parents.size() != 2) {
			System.err.println("Must have two parents for crossover!");
		}
		
		Individual parent1 = parents.get(0);
		Individual parent2 = parents.get(1);
		
		int numOfFeatures = parent1.getBitString().size();
		
		// randomly sample the crossover bit
		int crossoverBit = Utilities.randInt(0, numOfFeatures-1, rnd);
		
		// the first offspring
		Individual offspring1 = new Individual(numOfFeatures);
		for (int i = 0; i <= crossoverBit; i++) {
			if (parent1.getBitString().get(i)) {
				offspring1.addFeature(i);
			}
		}
		for (int i = crossoverBit + 1; i < numOfFeatures; i++) {
			if (parent2.getBitString().get(i)) {
				offspring1.addFeature(i);
			}
		}
		
		// the second offspring
		Individual offspring2 = new Individual(numOfFeatures);
		for (int i = 0; i <= crossoverBit; i++) {
			if (parent2.getBitString().get(i)) {
				offspring2.addFeature(i);
			}
		}
		for (int i = crossoverBit + 1; i < numOfFeatures; i++) {
			if (parent1.getBitString().get(i)) {
				offspring2.addFeature(i);
			}
		}
		
		List<Individual> offsprings = new ArrayList<Individual>();
		offsprings.add(offspring1);
		offsprings.add(offspring2);
		
		return offsprings;
	}
}
