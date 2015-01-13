package com.yimei.individual;

import java.util.Comparator;

public class condEntropyComparator implements Comparator<Individual> {

	@Override
	public int compare(Individual indi1, Individual indi2) {
		if (indi1.getCondEntropy() < indi2.getCondEntropy()) {
			return -1;
		}
		else if (indi1.getCondEntropy() > indi2.getCondEntropy()) {
			return 1;
		}
		
		return 0;
	}

}
