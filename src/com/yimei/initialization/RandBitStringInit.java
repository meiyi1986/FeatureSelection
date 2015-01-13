package com.yimei.initialization;

import java.util.Random;

import com.yimei.individual.Individual;
import com.yimei.problem.Problem;

public class RandBitStringInit extends Initialization {
	
	public RandBitStringInit(Random rnd) {
		super(rnd);
	}
	
	public Individual execute(Problem problem) {
		Individual indi = new Individual(problem.getNumOfFeatures());
		for (int i = 0; i < problem.getNumOfFeatures(); i++) {
			if (rnd.nextDouble() < 0.5) {
				indi.addFeature(i);
			}
		}
		indi.bitStringToSet();
		
		return indi;
	}

}
