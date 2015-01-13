package com.yimei.problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.yimei.objective.entropy.EntropyMethod;
import com.yimei.objective.entropy.ShannonEntropy;
import com.yimei.utils.Utilities;

import weka.core.Instance;
import weka.core.Instances;

public class Problem {

	private Instances data;
	private int numOfFeatures;
	private int numOfInstances;
	private int numOfClasses;
	
	private List<Double> featureLowerBounds;
	private List<Double> featureUpperBounds;
	
	private List<Integer> featureUniversal;
	
	private List<Integer> attributeCardinalities; // how many different values the attribute can take
	
	private double minCondEntropy;
	private double maxCondEntropy;
	private double minEntropy;
	private double maxEntropy;
	
	public Problem(Instances data) {
		this.data = data;
		numOfFeatures = data.numAttributes() - 1;
		numOfInstances = data.size();
		numOfClasses = data.numClasses();
		
		computeAttributeCardinalities();
		
		featureUniversal = new ArrayList<Integer>();
		for (int i = 0; i < numOfFeatures; i++) {
			featureUniversal.add(new Integer(i));
		}
		
		List<Integer> universal = new ArrayList<Integer>(featureUniversal);
		List<Integer> label = new ArrayList<Integer>();
		label.add(new Integer(numOfFeatures));
		
		// calculate the bounds of the entropy
		EntropyMethod shannonEntropy = new ShannonEntropy();
		
		minCondEntropy = shannonEntropy.condEntropy(label, universal, this);
		maxCondEntropy = shannonEntropy.entropy(label, this);
		minEntropy = Double.MAX_VALUE;
		for (int i = 0; i < numOfFeatures; i++) {
			double tmp = shannonEntropy.entropy(i, this);
			if (tmp < minEntropy) {
				minEntropy = tmp;
			}
		}
		maxEntropy = shannonEntropy.entropy(universal, this);
	}
	
	public Instances getInstances() {
		return data;
	}
	
	public int getNumOfFeatures() {
		return numOfFeatures;
	}
	
	public int getNumOfInstances() {
		return numOfInstances;
	}
	
	public int getNumOfClasses() {
		return numOfClasses;
	}
	
	public List<Integer> getFeatureUniversal() {
		return featureUniversal;
	}
	
	public double getMinCondEntropy() {
		return minCondEntropy;
	}
	
	public double getMaxCondEntropy() {
		return maxCondEntropy;
	}
	
	public double getMinEntropy() {
		return minEntropy;
	}
	
	public double getMaxEntropy() {
		return maxEntropy;
	}
	
	public List<Double> getFeatureLowerBounds() {
		return featureLowerBounds;
	}
	
	public List<Double> getFeatureUpperBounds() {
		return featureUpperBounds;
	}
	
	public List<Integer> getAttributeCardinalities() {
		return attributeCardinalities;
	}
	
	public void computeAttributeCardinalities() {
		attributeCardinalities = new ArrayList<Integer>(Collections.nCopies(data.numAttributes(), 0));
		
		for (int i = 0; i < data.numAttributes(); i++) {
			for (int j1 = 0; j1 < numOfInstances; j1++) {
				boolean duplicated = false;
				for (int j2 = 0; j2 < j1; j2++) {
					if (data.instance(j1).value(i) == data.instance(j2).value(i)) {
						duplicated = true;
						break;
					}
				}
				
				if (!duplicated) {
					attributeCardinalities.set(i, attributeCardinalities.get(i) + 1);
				}
			}
		}
	}
	
	public void computeFeatureLowerBounds() {
		featureLowerBounds = new ArrayList<Double>();
		
		// initialize with the feature values of the first instance
		for (int i = 0; i < numOfFeatures; i++) {
			double featureValue = data.instance(0).value(i);
			featureLowerBounds.add(new Double(featureValue));
		}
		
		// update with the rest of the instances
		for (int n = 1; n < numOfInstances; n++) {
			for (int i = 0; i < numOfFeatures; i++) {
				double featureValue = data.instance(n).value(i);
				if (featureLowerBounds.get(i) > featureValue) {
					featureLowerBounds.set(i, featureValue);
				}
			}
		}
	}
	
	public void computeFeatureUpperBounds() {
		featureUpperBounds = new ArrayList<Double>();
		
		// initialize with the feature values of the first instance
		for (int i = 0; i < numOfFeatures; i++) {
			double featureValue = data.instance(0).value(i);
			featureUpperBounds.add(new Double(featureValue));
		}
		
		// update with the rest of the instances
		for (int n = 1; n < numOfInstances; n++) {
			for (int i = 0; i < numOfFeatures; i++) {
				double featureValue = data.instance(n).value(i);
				if (featureUpperBounds.get(i) < featureValue) {
					featureUpperBounds.set(i, featureValue);
				}
			}
		}
	}
	
	
	public void normalize(List<Double> lb, List<Double> ub) {
		if (lb.size() != numOfFeatures) {
			System.err.println("In Problem.java: Lower bound must equal to the number of features!");
		}
		
		if (ub.size() != numOfFeatures) {
			System.err.println("In Problem.java: Upper bound must equal to the number of features!");
		}
		
		// do normalization
		for (Instance instance: data) {
			for (int i = 0; i < numOfFeatures; i++) {
				double normFeatureValue = Utilities.normalize(instance.value(i), lb.get(i), ub.get(i));
				instance.setValue(i, normFeatureValue);
			}
		}
	}
	
	
	public void selfNormalize() {
		computeFeatureLowerBounds();
		computeFeatureUpperBounds();
		
		for (Instance instance: data) {
			for (int i = 0; i < numOfFeatures; i++) {
//				System.out.println("i = " + i + ", lb = " + featureLowerBounds.get(i) + ", ub = " + featureUpperBounds.get(i) + ", value = " + instance.value(i));
				
				double normFeatureValue = Utilities.normalize(instance.value(i), featureLowerBounds.get(i), featureUpperBounds.get(i));
				instance.setValue(i, normFeatureValue);
				
				}
		}
	}
	public void printMe() {
		System.out.println("----------------- printing problem ------------------");
		System.out.println(numOfFeatures + " features, " + numOfInstances + " instances, " + numOfClasses + " classes.");
		System.out.println("cond entropy = [" + minCondEntropy + ", " + maxCondEntropy + 
				"], entropy = [" + minEntropy + ", " + maxEntropy + "]");
		System.out.println("attribute cardinalities :" + attributeCardinalities);
		System.out.println("------------------ finish printing -------------------");
	}
}
