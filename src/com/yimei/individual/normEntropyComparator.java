package com.yimei.individual;

import java.util.Comparator;

public class normEntropyComparator implements Comparator<Individual> {
	
	@Override
	public int compare(Individual indi1, Individual indi2) {
		if (indi1.normEntropy < indi2.normEntropy) {
			return -1;
		}
		else if (indi1.normEntropy > indi2.normEntropy) {
			return 1;
		}
		
		return 0;
	}

}
