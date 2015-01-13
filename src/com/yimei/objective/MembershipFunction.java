package com.yimei.objective;

import java.util.ArrayList;
import java.util.List;

import com.yimei.core.GlobalVariables;
import com.yimei.utils.Utilities;

import weka.core.Instance;
import weka.core.Instances;

/** 
 * Given an occurrence (x1,...,xn) of the random vector (X1,...,Xn),  the membership function
 * calculates its membership in the given sampling instances. A special case is the empirical
 * probability, in which the membership function equals 1 for the instances with the same vector
 * values, and 0 otherwise.
 * 
 * @author Administrator
 *
 */

public class MembershipFunction {

	String name;
	
	public MembershipFunction(String name) {
		this.name = name;
	}
	
	public double empiricalProbability(List<Double> distances) {
		
		double ep = 0.0;
		for (int i = 0; i < distances.size(); i++) {
			if (distances.get(i) < GlobalVariables.errorThreshold) {
				ep ++;
			}
		}
		
		return ep;
	}
	
	
	
	
	public double execute(List<Double> occurrence, List<Integer> indexes, Instances data) {
		// compute the distances
		List<Double> distances = new ArrayList<Double>();
		
		for (Instance instance : data) {
			
			List<Double> featureVec = new ArrayList<Double>();
			for (int i = 0; i < indexes.size(); i++) {
				featureVec.add(new Double(instance.value(indexes.get(i))));
			}
			
			// calculate the distance
			double distance = Utilities.distanceBetweenVectors(occurrence, featureVec);
			distances.add(new Double(distance));
		}
		
		if (name.equals("empiricalProbability")) {
			return empiricalProbability(distances);
		}
		

		System.err.println("The method " + name + "is unknown!");
		
		return -1;
	}
	
	public double executeWithClass(List<Double> occurrence, List<Integer> indexes, Instances data) {
		// compute the distances
		List<Double> distances = new ArrayList<Double>();
		
		for (Instance instance : data) {
			
			// check if the class of this instance is the same as the that of the occurrence
			if (instance.value(instance.numAttributes()-1) != occurrence.get(occurrence.size()-1)) {
				continue;
			}
			
			List<Double> featureVec = new ArrayList<Double>();
			for (int i = 0; i < indexes.size(); i++) {
				featureVec.add(new Double(instance.value(indexes.get(i))));
			}
			
			// calculate the distance
			double distance = Utilities.distanceBetweenVectors(occurrence.subList(0, occurrence.size()-1), featureVec);
			distances.add(new Double(distance));
		}
		
		if (name.equals("empiricalProbability")) {
			return empiricalProbability(distances);
		}
		
		System.err.println("The method " + name + "is unknown!");
		return -1;
	}
}
