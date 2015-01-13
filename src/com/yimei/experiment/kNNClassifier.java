package com.yimei.experiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yimei.objective.entropy.EntropyMethod;
import com.yimei.objective.entropy.ShannonEntropy;
import com.yimei.problem.Problem;

import bpsofs.HelpDataset;
import weka.core.Instance;
import weka.core.Instances;

public class kNNClassifier {
	private int k; // the number of neighbors in kNN
	
	public kNNClassifier(int k) throws Exception {
		this.k = k;
	}

	// the accuracy of the kNN classifier
	public double accuracy(Instances training, Instances testing) {
		int numOfFeatures = training.numAttributes() - 1;
		
		double accuracy = 0;
		// predict for each testing instance
		for (Instance instance : testing) {
			double trueLabel = instance.value(numOfFeatures);
			double predictedLabel = classifyInstance(instance, training);
			
			if (predictedLabel == trueLabel) {
				accuracy ++;
			}
			else {
				System.out.println("misclassified!");
				System.out.println(instance);
			}
		}
		
		accuracy /= testing.numInstances();
		
		return accuracy;
	}
	
	// classify an instance, returns the predicted class
	public double classifyInstance(Instance instance, Instances training) {
		int numOfFeatures = training.numAttributes() - 1;
		
		List<Neighbor> neighbors = new ArrayList<Neighbor>();
		
		// calculate the distance to each training instance
		for (Instance trainingInstance : training) {
			double dist = instanceDistance(instance, trainingInstance);
			Neighbor neighbor = new Neighbor(trainingInstance, dist);
			
			updateNeighbors(neighbor, neighbors);
		}
		
		System.out.print("\nthis instance: ");
		System.out.println(instance);
		
		for (Neighbor neighbor : neighbors) {
			System.out.print("best neighbor: ");
			System.out.println(neighbor.getInstance());
		}
		
		System.out.println("the best distance = " + neighbors.get(0).getDistance());
		
		// do statistics on the neighbors
		Map<Double, Integer> labelStat = new HashMap<Double, Integer>();
		for (Neighbor neighbor : neighbors) {
			double label = neighbor.getInstance().value(numOfFeatures);
			
			if (labelStat.containsKey(label)) {
				labelStat.put(label, labelStat.get(label) + 1);
			}
			else {
				labelStat.put(label, 1);
			}
		}
		
		// get the label that appears the most
		double finalLabel = -1;
		int mostTimes = 0;
		for (double label : labelStat.keySet()) {
			if (labelStat.get(label) > mostTimes) {
				finalLabel = label;
				mostTimes = labelStat.get(label);
			}
		}
		
		return finalLabel;
	}
	
	private void updateNeighbors(Neighbor newNeighbor, List<Neighbor> neighbors) {
		
		if (neighbors.isEmpty()) {
			neighbors.add(newNeighbor);
			return;
		}
		
		for (int i = 0; i < neighbors.size(); i++) {
			if (newNeighbor.isCloser(neighbors.get(i))) {
				neighbors.add(i, newNeighbor);
				break;
			}
		}
		
		if (neighbors.size() > k) {
			neighbors.remove(neighbors.size()-1);
		}
	}
	
	double instanceDistance(Instance instance1, Instance instance2) {
		int numOfFeatures = instance1.numAttributes() - 1;
		
		double distance = 0.0;
	    for(int i = 0; i < numOfFeatures; i++) {
	    	distance = distance + Math.pow((instance1.value(i)-instance2.value(i)), 2.0);
	    }
	    return Math.sqrt(distance);
	}
	
	
	public static void main(String[] args) throws IOException, Exception {
		
		/*data */
		String dataDir = "Data/Filter/";
		
        Instances[] trainTest = HelpDataset.readDataTrTe(dataDir + "lung", true);
        Instances training = trainTest[0];
        Instances testing = trainTest[1];
        
        int dimension = training.numAttributes() - 1; // number of features

        Integer[] idx = {dimension};
        Integer[] cidx = {9,47};
        
        List<Integer> idxSet = new ArrayList<Integer>(Arrays.asList(idx));
        List<Integer> cidxSet = new ArrayList<Integer>(Arrays.asList(cidx));
        
        List<Integer> allIdxSet = new ArrayList<Integer>(idxSet);
        allIdxSet.addAll(cidxSet);
        System.out.println(allIdxSet);
        
        // calculate shannon entropy
        EntropyMethod shannonEntropy = new ShannonEntropy();
        
        Problem tprob = new Problem(training);
                
        double je = shannonEntropy.entropy(allIdxSet, tprob);
        System.out.println("joint entropy = " + je);
        
        double ent = shannonEntropy.entropy(cidxSet, tprob);
        
        double ce = shannonEntropy.condEntropy(idxSet, cidxSet, tprob);
        System.out.println("cond entropy = " + ce + ", uncertainty = " + ent);
        
        // classify with 1-NN
        int[] selected = new int[dimension];
        for (int i = 0; i < cidx.length; i++) {
        	selected[cidx[i]] = 1;
        }
        
        System.out.println(Arrays.toString(selected));

        Instances newTrain = HelpDataset.removeFeatures(new Instances(training), selected);
        Instances newTest = HelpDataset.removeFeatures(new Instances(testing), selected);

//        // normalize training and testing
//        Normalize norm = new Normalize();
//        norm.setInputFormat(newTrain);
//        newTrain = Filter.useFilter(newTrain, norm);
//        norm.setInputFormat(newTest);
//        newTest = Filter.useFilter(newTest, norm);
        
//        System.out.println(newTrain);
        
        kNNClassifier classifier = new kNNClassifier(1);
        
        double trainAcc = classifier.accuracy(newTrain, newTrain);
        double testAcc = classifier.accuracy(newTrain, newTest);
        
        System.out.println("trainAcc " + trainAcc);
        System.out.println("testAcc " + testAcc);
	}
}
