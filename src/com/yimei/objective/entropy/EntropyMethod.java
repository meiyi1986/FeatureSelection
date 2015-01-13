package com.yimei.objective.entropy;

import java.util.List;

import com.yimei.problem.Problem;

public abstract class EntropyMethod {

	abstract public double entropy(int index, Problem problem);
	
	abstract public double entropy(List<Integer> indexes, Problem problem);
	
	abstract public double condEntropy(int index, int condIndex, Problem problem);
	
	abstract public double condEntropy(List<Integer> indexes, List<Integer> condIndexes, Problem problem);
	
	
}
