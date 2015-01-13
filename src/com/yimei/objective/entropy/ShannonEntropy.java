package com.yimei.objective.entropy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yimei.problem.Problem;
import com.yimei.utils.Utilities;

import bpsofs.HelpDataset;
import weka.core.Instances;

public class ShannonEntropy extends EntropyMethod {
	
	public double entropy(int index, Problem problem) {
		
		Instances data = problem.getInstances();
		
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
		
		double e = 0.0;
		for (Map.Entry<Double, Integer> entry : occ.entrySet()) {
			double p = (double) entry.getValue() / n;
			e += p * log2(p);
		}
		
		return -e;
	}
	
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
		
//		for (Map.Entry<List<Double>, Integer> entry : occ.entrySet()) {
//			System.out.println("key: " + entry.getKey() + ", value = " + entry.getValue());
//		}
		
		double e = 0.0;
		for (Map.Entry<List<Double>, Integer> entry : occ.entrySet()) {
			double p = (double) entry.getValue() / n;
			e += p * log2(p);
		}
      
		return -e;
	}
	
	
public static List<Double> entropyAndConfidence(List<Integer> indexes, Instances data, int numClasses) {
		
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
		
//		for (Map.Entry<List<Double>, Integer> entry : occ.entrySet()) {
//			System.out.println("key: " + entry.getKey() + ", value = " + entry.getValue());
//		}
		
		List<Double> res = new ArrayList<Double>();
		
		double avgConfidence = 0.0;
		
		// the beta distribution parameters
		double alpha = 10.0;
		double sum = alpha*numClasses;
		
		
		double e = 0.0;
		for (Map.Entry<List<Double>, Integer> entry : occ.entrySet()) {
			double density = (double) entry.getValue();
			double p = density / n;
			e += p * log2(p);
			
			avgConfidence += (density + alpha) / (density + sum);
		}
		
		res.add(new Double(-e));
		
		res.add(new Double(avgConfidence / n));
		
		return res;
	}
	
	public double condEntropy(int index, int condIndex, Problem problem) {
		
		List<Integer> allIndexes = new ArrayList<Integer>();
		allIndexes.add(new Integer(index));
		allIndexes.add(new Integer(condIndex));
		
		double je1 = entropy(allIndexes, problem);
		double je2 = entropy(condIndex, problem);
		
		double ce = je1 - je2;
		
		return ce;
	}
	
	public double condEntropy(List<Integer> indexes, List<Integer> condIndexes, Problem problem) {
		List<Integer> allIndexes = new ArrayList<Integer>(indexes);
		allIndexes.addAll(condIndexes);
		
		double je1 = entropy(allIndexes, problem);
		double je2 = entropy(condIndexes, problem);
		
		double ce = je1 - je2;
		
		return ce;
	}
	
//	public double mutualInfo(int index1, int index2, Instances data) {
//		double entropy = entropy(index1, data);
//		double condEntropy = condEntropy(index1, index2, data);
//		double mi = entropy - condEntropy;
//		
//		System.out.println(entropy + " - " + condEntropy + " = " + mi);
//		
//		return mi;
//	}
//	
//	public double mutualInfo(List<Integer> indexes1, List<Integer> indexes2, Instances data) {
//		double entropy = entropy(indexes1, data);
//		double condEntropy = condEntropy(indexes1, indexes2, data);
//		double mi = entropy - condEntropy;
//		
//		System.out.println(entropy + " - " + condEntropy + " = " + mi);
//		
//		return mi;
//	}
//	
//	public double condMutualInfo(List<Integer> indexes1, List<Integer> indexes2, List<Integer> condIndexes, Instances data) {
//		// concatenate indexes2 and condIndexes
//		List<Integer> concatIndexes = new ArrayList<Integer>(indexes2);
//		concatIndexes.addAll(condIndexes);
//		
//		double ce1 = condEntropy(indexes1, condIndexes, data);
//		double ce2 = condEntropy(indexes1, concatIndexes, data);
//		
//		double cmi = ce1 - ce2;
//		
//		return cmi;
//	}
	
	
	
	
	
	
	
	
	private static double log2(double a) {
		return Math.log(a) / Math.log(2);
	}
	
	

	
	
	public static void main(String[] args) throws Exception {
		
		String dataDir = "Data/Filter/";
		
		/*data */
        Instances[] trainTest = HelpDataset.readDataTrTe(dataDir + "zoo", false);
        Instances training = trainTest[0];
        Instances testing = trainTest[1];
		
		// Print header and instances.
		System.out.println("\nDataset:\n");
		System.out.println(testing);
		
		int dimension = training.numAttributes() - 1;
		
		
		
		long startCpuTimeNano = Utilities.getCpuTime();
		
		// calculate conditional entropy
		Integer[] idx = {dimension};
        Integer[] cidx = {4, 6, 9, 13};
        List<Integer> idxSet = new ArrayList<Integer>(Arrays.asList(idx));
        List<Integer> cidxSet = new ArrayList<Integer>(Arrays.asList(cidx));
        
        List<Integer> allIdxSet = new ArrayList<Integer>(idxSet);
        allIdxSet.addAll(cidxSet);

//        double je = entropy(cidxSet, training);
        
        List<Double> ec = entropyAndConfidence(cidxSet, training, 2);
        double je = ec.get(0);
        double cf = ec.get(1);
        
        long estimatedCpuTime = (Utilities.getCpuTime() - startCpuTimeNano) / (1000 * 1000);
        
        System.out.println(allIdxSet);
        System.out.println("joint entropy = " + je);
        System.out.println("confidence = " + cf);
        System.out.println("running time = " + estimatedCpuTime + "ms.");
        
//        double ent = entropy(cidxSet, training);
//        
//        double ce = condEntropy(idxSet, cidxSet, training);
//        System.out.println("cond entropy = " + ce + ", uncertainty = " + ent);
        
        // classification verification
        
//        double mi = mutualInfo(idx, cidx, training);
//        System.out.println("mutual info = " + mi);
	}
}
