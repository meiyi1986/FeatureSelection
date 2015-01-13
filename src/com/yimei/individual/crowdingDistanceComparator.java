package com.yimei.individual;

import java.util.Comparator;

public class crowdingDistanceComparator implements Comparator<Individual> {

	@Override
	public int compare(Individual indi1, Individual indi2) {
		if (indi1.crowdingDistance < indi2.crowdingDistance) {
			return 1;
		}
		else if (indi1.crowdingDistance > indi2.crowdingDistance) {
			return -1;
		}
		
		return 0;
	}
}
