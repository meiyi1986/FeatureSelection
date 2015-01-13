package com.yimei.objective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yimei.utils.Utilities;

import bpsofs.HelpDataset;
import weka.core.Instances;

public class MembershipEntropy {
	
	MembershipFunction mf;
	
	public MembershipEntropy(String mf) {
		this.mf = new MembershipFunction(mf);
	}

	public double entropy(List<Integer> indexes, Instances data) {
		
		// get all the occurrences
		int n = data.numInstances();
		Map<List<Double>, Double> occ = new HashMap<List<Double>, Double>();
		
		for (int i = 0; i < data.size(); i++) {
			List<Double> key = new ArrayList<Double>();
			for (int idx : indexes) {
				key.add(data.instance(i).value(idx));
			}
			
			if (!occ.containsKey(key)) {
				occ.put(key, 0.0);
			}
		}
		
		
//		for (Map.Entry<List<Double>, Double> entry : occ.entrySet()) {
//			System.out.println("key: " + entry.getKey() + ", value = " + entry.getValue());
//		}
		
		
		// get the membership function of each occurrence
		for (List<Double> key : occ.keySet()) {
			double membership = mf.execute(key, indexes, data);
			occ.put(key, membership);
		}
		
//		for (Map.Entry<List<Double>, Double> entry : occ.entrySet()) {
//			System.out.println("key: " + entry.getKey() + ", value = " + entry.getValue());
//		}
		
		double e = 0.0;
		for (Map.Entry<List<Double>, Double> entry : occ.entrySet()) {
			double p = (double) entry.getValue() / n;
			e += p * log2(p);
		}
      
		return -e;
	}
	
public double entropyWithClass(List<Integer> indexes, Instances data) {
		
		// get all the occurrences
		int n = data.numInstances();
		Map<List<Double>, Double> occ = new HashMap<List<Double>, Double>();
		
		for (int i = 0; i < data.size(); i++) {
			List<Double> key = new ArrayList<Double>();
			for (int idx : indexes) {
				key.add(data.instance(i).value(idx));
			}
			
			// add the class label
			double label = Utilities.getInstanceClass(data.instance(i));
			key.add(new Double(label));
			
			if (!occ.containsKey(key)) {
				occ.put(key, 0.0);
			}
		}
		
		
//		for (Map.Entry<List<Double>, Double> entry : occ.entrySet()) {
//			System.out.println("key: " + entry.getKey() + ", value = " + entry.getValue());
//		}
		
		
		// get the membership function of each occurrence
		for (List<Double> key : occ.keySet()) {
			double membership = mf.executeWithClass(key, indexes, data);
			occ.put(key, membership);
		}
		
//		for (Map.Entry<List<Double>, Double> entry : occ.entrySet()) {
//			System.out.println("key: " + entry.getKey() + ", value = " + entry.getValue());
//		}
		
		double e = 0.0;
		for (Map.Entry<List<Double>, Double> entry : occ.entrySet()) {
			double p = (double) entry.getValue() / n;
			e += p * log2(p);
		}
      
		return -e;
	}
	
	
	
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
		System.out.println(training);
		
		int dimension = training.numAttributes() - 1;
		
		long startCpuTimeNano = Utilities.getCpuTime();
		
		// calculate conditional entropy
		Integer[] idx = {dimension};
        Integer[] cidx = {4, 6, 9, 13};
        List<Integer> idxSet = new ArrayList<Integer>(Arrays.asList(idx));
        List<Integer> cidxSet = new ArrayList<Integer>(Arrays.asList(cidx));
        
        List<Integer> allIdxSet = new ArrayList<Integer>(idxSet);
        allIdxSet.addAll(cidxSet);

        MembershipEntropy me = new MembershipEntropy("empiricalProbability");
        double je = me.entropy(cidxSet, training);
        
        long estimatedCpuTime = (Utilities.getCpuTime() - startCpuTimeNano) / (1000 * 1000);
        
        System.out.println(allIdxSet);
        System.out.println("joint entropy = " + je);
        System.out.println("running time = " + estimatedCpuTime + "ms.");
        
        
        
        
        // classification verification
        
//        double mi = mutualInfo(idx, cidx, training);
//        System.out.println("mutual info = " + mi);
	}
}
