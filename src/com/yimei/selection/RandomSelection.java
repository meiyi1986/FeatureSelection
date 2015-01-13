package com.yimei.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.yimei.individual.Individual;
import com.yimei.utils.Utilities;

public class RandomSelection extends Selection {
	
	private int numOfParents;
	
	public RandomSelection(Random rnd, int k) {
		super(rnd);
		numOfParents = k;
	}

	// select k parents
	public List<Individual> execute(List<Individual> population) {
		
		List<Integer> parentIdx = new ArrayList<Integer>();
		for (int i = 0; i < numOfParents; i++) {
			// randomly choose the kth parent
			boolean duplicated = true;
			while (duplicated) {
				duplicated = false;
				int r = Utilities.randInt(0, population.size(), rnd);
				if (parentIdx.contains(r)) {
					duplicated = true;
				}
				else {
					parentIdx.add(new Integer(r));
				}
			}
		}
		
//		System.out.println(parentIdx);
		
		List<Individual> parents = new ArrayList<Individual>();
		for (int i = 0; i < parentIdx.size(); i++) {
			parents.add(population.get(parentIdx.get(i)));
		}
		
		return parents;
	}
}
