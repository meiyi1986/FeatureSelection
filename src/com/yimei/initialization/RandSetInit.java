package com.yimei.initialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.yimei.individual.Individual;
import com.yimei.problem.Problem;

public class RandSetInit extends Initialization {
	
	private int setSize;
	
	public RandSetInit(Random rnd, int k) {
		super(rnd);
		setSize = k;
	}

	public Individual execute(Problem problem) {
		
		List<Integer> universal = new ArrayList<Integer>(problem.getFeatureUniversal());
		
		Collections.shuffle(universal, rnd);
		List<Integer> subset = new ArrayList<Integer>(universal.subList(0, setSize));
		
		Individual indi = new Individual(problem.getNumOfFeatures());
		indi.setSet(subset);
		indi.setToBitString();
		
		return indi;
	}
}
