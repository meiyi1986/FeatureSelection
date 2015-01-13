package com.yimei.mutation;

import java.util.Random;

import com.yimei.individual.Individual;
import com.yimei.utils.Utilities;

public class SingleFlipMutation extends Mutation {
	
	public SingleFlipMutation(Random rnd) {
		super(rnd);
	}
	
	public Individual execute(Individual parent) {
		Individual offspring = new Individual(parent);
		
		int flipBit;
		
		// random sample the flip bit
		if (parent.getSet().size() == 1) { // only one feature is selected, only adding features is allowed
			// cannot remove the only feature
			do {
				flipBit = Utilities.randInt(0, parent.getBitString().size(), rnd);
				} while (parent.getBitString().get(flipBit));
			// add this feature
			offspring.addFeature(flipBit);
		}
		else { // one can either add or remove features
			flipBit = Utilities.randInt(0, parent.getBitString().size(), rnd);
			if (parent.getBitString().get(flipBit)) {
				// remove this feature
				offspring.removeFeature(flipBit);
			}
			else {
				// add this feature
				offspring.addFeature(flipBit);
			}
		}
		
		return offspring;
	}
}
