package com.yimei.objective.entropy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yimei.problem.Problem;

import weka.core.Instances;

/***
 * The Bayes entropy is based on the posterior probabilities calculated by
 * Bayes formula. A prior density of Beta distribution B(alpha, beta) is defined.
 * 
 * @author yi
 *
 */

public class BayesEntropy extends EntropyMethod {
	
	private double alpha;
	
	public BayesEntropy(double alpha) {
		this.alpha = alpha;
	}

	@Override
	public double entropy(int index, Problem problem) {
		
		Instances data = problem.getInstances();
		
		// calculate the occurrences
		int n = 0;
		Map<Double, Integer> occ = new HashMap<Double, Integer>();
		for (int i = 0; i < data.size(); i++) {
			double key = data.instance(i).value(index);
			if (occ.containsKey(key)) {
				occ.put(key, occ.get(key)+1);
			}
			else {
				occ.put(key, 1);
			}
			++ n;
		}
		
//		int cardinality = problem.getAttributeCardinalities().get(index);
//		// the denominator
//		double sum = alpha * cardinality; // alpha + beta
		int cardinality = occ.size(); // assume all the occurrences have happened
		double sum = alpha * cardinality; // alpha + beta
		
		// calculate the entropy
		System.out.print("posterior probabilities: ");
		double e = 0.0;
		for (Map.Entry<Double, Integer> entry : occ.entrySet()) {
			double density = (double) entry.getValue();
			double postDensity = (alpha + density) / (sum + n);
			e += postDensity * log2(postDensity);
			
			System.out.print(postDensity + ", ");
		}
		// add for all the scenarios that did not occur
		double postDensity0 = alpha / (sum + n);
		e += (cardinality - occ.size()) * postDensity0 * log2(postDensity0);
		System.out.println("\n------------------------");
		
		return -e;
	}

	
	

	@Override
	public double entropy(List<Integer> indexes, Problem problem) {
				
		Instances data = problem.getInstances();
		
		// compute entropy
		int n = 0;
		Map<List<Double>, Integer> occ = new HashMap<List<Double>, Integer>();
		
		for (int i = 0; i < data.size(); i++) {
			List<Double> key = new ArrayList<Double>();
			for (int idx : indexes) {
				key.add(data.instance(i).value(idx));
			}
			
			if (occ.containsKey(key)) {
				occ.put(key, occ.get(key)+1);
			}
			else {
				occ.put(key, 1);
			}
			
			++ n;
		}
		
		// the denominator
//		int cardinality = 1;
//		for (int index : indexes) {
//			cardinality *= problem.getAttributeCardinalities().get(index);
//		}
//		double sum = alpha * cardinality; // alpha + beta
		int cardinality = occ.size(); // assume all the occurrences have happened
		double sum = alpha * cardinality; // alpha + beta
		
//		for (Map.Entry<List<Double>, Integer> entry : occ.entrySet()) {
//			System.out.println("key: " + entry.getKey() + ", value = " + entry.getValue());
//		}
		
		// calculate the entropy
//		System.out.print("posterior probabilities: ");
		double e = 0.0;
		for (Map.Entry<List<Double>, Integer> entry : occ.entrySet()) {
			double density = (double) entry.getValue();
			double postDensity = (alpha + density) / (sum + n);
			e += postDensity * log2(postDensity);
			
//			System.out.print(postDensity + ", ");
		}
		// add for all the scenarios that did not occur
		double postDensity0 = alpha / (sum + n);
		e += (cardinality - occ.size()) * postDensity0 * log2(postDensity0);
		if (e > 0) {
			System.out.println(occ);
			System.out.println("features " + indexes + ", card = " + cardinality + ", e = " + e);
			System.out.println("n = " + n + ", postd0 = " + postDensity0);
		}
//		System.out.println("\n------------------------");
      
		return -e;
	}

	@Override
	public double condEntropy(int index, int condIndex, Problem problem) {
		List<Integer> allIndexes = new ArrayList<Integer>();
		allIndexes.add(new Integer(index));
		allIndexes.add(new Integer(condIndex));
		
		double je1 = entropy(allIndexes, problem);
		double je2 = entropy(condIndex, problem);
		
		double ce = je1 - je2;
		
		return ce;
	}

	@Override
	public double condEntropy(List<Integer> indexes, List<Integer> condIndexes,
			Problem problem) {
		List<Integer> allIndexes = new ArrayList<Integer>(indexes);
		allIndexes.addAll(condIndexes);
		
		double je1 = entropy(allIndexes, problem);
		double je2 = entropy(condIndexes, problem);
		
		double ce = je1 - je2;
		
		return ce;
	}
	
	
	
	private static double log2(double a) {
		return Math.log(a) / Math.log(2);
	}
}
