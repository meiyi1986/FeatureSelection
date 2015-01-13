package com.yimei.experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import weka.core.Instances;
import bpsofs.HelpDataset;

import com.yimei.algorithm.nsgaii.NSGAII;
import com.yimei.core.GlobalVariables;
import com.yimei.individual.Individual;
import com.yimei.objective.entropy.BayesEntropy;
import com.yimei.objective.entropy.EntropyMethod;
import com.yimei.problem.Problem;
import com.yimei.utils.Utilities;

public class Main {

	public static void main(String[] args) throws IOException, Exception {
		
		String dataDir = "Data/Filter/";
		String [] problems = {
//				"Chess",
//				"connect4",
				"dermatology",
				"lung",
				"lymph",
				"leddisplay",
				"mushroom",
				"soybean",
				"soybeanlarge",
				"spect",
				"splice",
				"waveform",
				"zoo", 
				};
		
		// set and create the result directory
		String resDir = "Data/Results/";
		File directory = new File(resDir);
		if (!directory.exists()) {
			try{
				directory.mkdirs();
		    } catch(SecurityException se){
			   //handle it
			}        
		}
		
		// solve each problem
		for (String currProb : problems) {
			
			/*data */
	        Instances[] trainTest = HelpDataset.readDataTrTe(dataDir + currProb, false);
	        Instances training = trainTest[0];
	        Instances testing = trainTest[1];
	        
	        int dimension = training.numAttributes() - 1;
	        
	        // create the problem
	        Problem trainProb = new Problem(training);
	        trainProb.selfNormalize();
	        trainProb.printMe();
	        
	        Problem testProb = new Problem(testing);
	        testProb.normalize(trainProb.getFeatureLowerBounds(), trainProb.getFeatureUpperBounds());
//	        testProb.printMe();
	        
//			// Print header and instances.
//			System.out.println("\nDataset:\n");
//			System.out.println(testing);
	        
	        // set the entropy method
//	        EntropyMethod method = new ShannonEntropy();
	        EntropyMethod method = new BayesEntropy(5.0);
	        
	        /** Start running the feature selection algorithm */
	        long startCpuTimeNano = Utilities.getCpuTime();
	        
	        NSGAII nsga2 = new NSGAII(trainProb, method);
	        nsga2.setPopsize(30);
	        nsga2.setMaxEvaluations(1000 * dimension);
	        List<Individual> finalPopulation = nsga2.execute();
	        
//	        GeneticAlgorithm ga = new GeneticAlgorithm(trainProb, method);
//	        ga.setPopsize(30);
//	        ga.setMaxEvaluations(1000 * dimension);
//	        List<Individual> finalPopulation = ga.execute();
	        
	        // select the individual with the minimal conditional entropy and number of features
	        Individual finalIndi = null;
	        double minCondEntropy = Double.MAX_VALUE;
	        int minNumOfFeatures = dimension;
	        for (Individual indi : finalPopulation) {
	        	if (indi.getCondEntropy() < minCondEntropy - GlobalVariables.errorThreshold) { // zero in fact
	        		finalIndi = indi;
	        		minCondEntropy = indi.getCondEntropy();
	        		minNumOfFeatures = indi.getSet().size();
	        	}
	        	else if (indi.getCondEntropy() < minCondEntropy + GlobalVariables.errorThreshold) {
	        		if (indi.getSet().size() < minNumOfFeatures) {
	        			finalIndi = indi;
	        			minNumOfFeatures = indi.getSet().size();
	        		}
	        	}
	        }
	        
//	        Individual finalIndi = finalPopulation.get(13);
	        
	        long estimatedCpuTime = (Utilities.getCpuTime() - startCpuTimeNano) / (1000 * 1000);
	        /** finish the algorithm */
	        
	        System.out.println("final individual");
	        finalIndi.printMe();
	        
	        finalIndi.evaluate(testProb, method);
	        
	        System.out.println("test cond entropy = " + finalIndi.getCondEntropy());
	        
	        System.out.println("running time = " + estimatedCpuTime + "ms.");
	        
	        /************************* Test phase ***********************/
	        
	        // transform list to array
	        int[] bitArray = new int[dimension];
	        for (int i = 0; i < dimension; i++) {
	        	if (finalIndi.getBitString().get(i)) {
	        		bitArray[i] = 1;
	        	}
	        }
	        
	        Instances newTrain = HelpDataset.removeFeatures(new Instances(training), bitArray);
	        Instances newTest = HelpDataset.removeFeatures(new Instances(testing), bitArray);

//	        System.out.println(newTrain);
	        
	        kNNClassifier classifier = new kNNClassifier(1);
	        
	        System.out.println("-------------------- training ----------------");
	        double trainAcc = classifier.accuracy(newTrain, newTrain);
	        System.out.println("---------------------- test -------------------");
	        double testAcc = classifier.accuracy(newTrain, newTest);

	        System.out.println("trainAcc " + trainAcc);
	        System.out.println("testAcc " + testAcc);

	        System.out.println("size = " + finalIndi.getSet().size());

	        try {
	            File file = new File(resDir + currProb + String.valueOf(Integer.valueOf(args[0]) + 1) + ".res");

	            BufferedWriter output = new BufferedWriter(new FileWriter(file));

	            output.write(finalIndi.getSet().size() + ", " + trainAcc + ", " + testAcc + ", " + estimatedCpuTime);
	            output.newLine();
	            for (int i = 0; i < finalIndi.getSet().size(); i++) {
	                output.write(finalIndi.getSet().get(i) + ", ");
	            }
	            output.newLine();
	            output.newLine();
	            output.write("size, trainAcc, testAcc, CPUTime");


	            output.close();
	        } catch (IOException e) {
	        }
		}
		
		
	}
}
