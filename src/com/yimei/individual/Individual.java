package com.yimei.individual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yimei.core.GlobalVariables;
import com.yimei.objective.MembershipEntropy;
import com.yimei.objective.entropy.EntropyMethod;
import com.yimei.objective.entropy.ShannonEntropy;
import com.yimei.problem.Problem;
import com.yimei.utils.Utilities;

public class Individual implements Comparable<Individual> {

	List<Boolean> bitString;
	List<Integer> set;
	
	private double condEntropy;
	private double entropy;
	
	public int rank; // for nondominated sorting
	public double normCondEntropy; // for crowding distance
	public double normEntropy; // for crowding distance
	public double crowdingDistance; // for nondominated sorting
	
	public Individual(int n) {
		// initialized as an empty set
		bitString = new ArrayList<Boolean>(Collections.nCopies(n, false));
		set = new ArrayList<Integer>();
	}
	
	public Individual(Individual indi) {
		this.bitString = new ArrayList<Boolean>(indi.getBitString());
		this.set = new ArrayList<Integer>(indi.getSet());
		this.condEntropy = indi.getCondEntropy();
		this.entropy = indi.getEntropy();
	}
	
	public List<Boolean> getBitString() { return bitString; }
	public List<Integer> getSet() { return set; }
	public double getCondEntropy() { return condEntropy; }
	public double getEntropy() { return entropy; }
	
	public void setBitString(List<Boolean> bs) { this.bitString = bs; }
	public void setSet(List<Integer> set) { this.set = set; }
	
	public void bitStringToSet() {
		set.clear();
		for (int i = 0; i < bitString.size(); i++) {
			if (bitString.get(i)) {
				set.add(new Integer(i));
			}
		}
	}
	
	public void setToBitString() {
		for (int i = 0; i < bitString.size(); i++) {
			bitString.set(i, false);
		}
		
		for (int k : set) {
			bitString.set(k, true);
		}
	}
	
	public void addFeature(int k) {
		
		if (bitString.get(k)) {
			System.err.println("The feature to be added is already selected!");
			System.exit(0);
		}
		
		bitString.set(k, true);
		set.add(new Integer(k));
	}
	
	public void removeFeature(int k) {
		if (!bitString.get(k)) {
			System.err.println("The feature to be removed is already absent!");
		}
		
		bitString.set(k, false);
		set.remove(new Integer(k));
	}
	
	public void evaluate(Problem problem, EntropyMethod method) {
		// compute the entropies
		computeEntropies(problem, method);
	}
	
	public void computeEntropies(Problem problem, EntropyMethod method) {
		
		List<Integer> jointIdxes = new ArrayList<Integer>(set);
		// add the class label
		jointIdxes.add(new Integer(problem.getNumOfFeatures()));
		
		// calculate the joint entropy
		entropy = method.entropy(set, problem);
		
		double je = method.entropy(jointIdxes, problem);
		
		// calculate the conditional entropy
		condEntropy = je - entropy;
	}
	
	public void normalizeCondEntropy(double min, double max) {
		normCondEntropy = Utilities.normalize(condEntropy, min, max);
	}
	
	public void normalizeEntropy(double min, double max) {
		normEntropy = Utilities.normalize(entropy, min, max);
	}
	
	public void normalize(Problem problem) {
		normalizeCondEntropy(problem.getMinCondEntropy(), problem.getMaxCondEntropy());
		normalizeEntropy(problem.getMinEntropy(), problem.getMaxEntropy());
	}
	
	
	public boolean equals(Individual cmpIndi) {
		if (Math.abs(this.condEntropy - cmpIndi.getCondEntropy()) < GlobalVariables.errorThreshold
				&& Math.abs(this.entropy - cmpIndi.getEntropy()) < GlobalVariables.errorThreshold
				&& this.set.size() == cmpIndi.getSet().size()) {
			return true;
		}
		
		return false;
	}
	
	
	public int dominanceRelation(Individual cmpIndi) {
		if (this.condEntropy < cmpIndi.getCondEntropy() - GlobalVariables.errorThreshold) {
			if (this.entropy < cmpIndi.getEntropy() + GlobalVariables.errorThreshold) {
				return -1;
			}
			else {
				return 0;
			}
		}
		else if (this.condEntropy < cmpIndi.getCondEntropy() + GlobalVariables.errorThreshold) {
			if (this.entropy < cmpIndi.getEntropy() - GlobalVariables.errorThreshold) {
				return -1;
			}
			else if (this.entropy < cmpIndi.getEntropy() + GlobalVariables.errorThreshold) {
				return 0;
			}
			else {
				return 1;
			}
		}
		else {
			if (this.entropy < cmpIndi.getEntropy() - GlobalVariables.errorThreshold) {
				return 0;
			}
			else {
				return 1;
			}
		}
	}
	
	// primary objective: conditional entropy; secondary objective: entropy
	public boolean isBetterThan(Individual cmpIndi) {
		if (this.condEntropy < cmpIndi.getCondEntropy() - GlobalVariables.errorThreshold) {
			return true;
		}
		else if (this.condEntropy < cmpIndi.getCondEntropy() + GlobalVariables.errorThreshold) {
			if (this.entropy < cmpIndi.getEntropy() - GlobalVariables.errorThreshold) {
				return true;
			}
		}
		
		return false;
	}
	
	public int compareTo(Individual tmpIndi) {
		if (this.rank < tmpIndi.rank) {
			return -1;
		}
		else if (this.rank == tmpIndi.rank) {
			return 0;
		}
		else {
			return 1;
		}
	}
 	
	public void printMe() {
		System.out.print("Features: ");
//		System.out.println(bitString);
		System.out.println(set);
		System.out.println("cond entropy = " + condEntropy + ", entropy = " + entropy + ", rank = " + rank);
	}
}
