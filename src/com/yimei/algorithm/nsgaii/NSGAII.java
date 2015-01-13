package com.yimei.algorithm.nsgaii;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.core.Instances;
import bpsofs.HelpDataset;

import com.yimei.crossover.Crossover;
import com.yimei.crossover.SinglePointCrossover;
import com.yimei.individual.Individual;
import com.yimei.initialization.Initialization;
import com.yimei.initialization.RandSetInit;
import com.yimei.mutation.Mutation;
import com.yimei.mutation.SingleFlipMutation;
import com.yimei.objective.MembershipEntropy;
import com.yimei.objective.entropy.EntropyMethod;
import com.yimei.objective.entropy.ShannonEntropy;
import com.yimei.problem.Problem;
import com.yimei.selection.BinaryTournamentSelection;
import com.yimei.selection.RandomSelection;
import com.yimei.selection.Selection;

public class NSGAII {

	// the problem
	private Problem problem;
	private EntropyMethod method;
	
	// the parameters
	private int popsize;
	private int maxEvaluations;
	
	public NSGAII(Problem problem, EntropyMethod method) {
		this.problem = problem;
		this.method = method;
	}

	
	public void setPopsize(int popsize) {
		this.popsize = popsize;
	}
	
	public void setMaxEvaluations(int maxFE) {
		this.maxEvaluations = maxFE;
	}
	
	public List<Individual> execute() {
		// set random seed
		long seed = 0;
		Random rnd = new Random(seed);
		
		// initialize population
		List<Individual> population = new ArrayList<Individual>();
		int evaluations = 0;
		
		for (int i = 0; i < popsize; i++) {
			Initialization init = new RandSetInit(rnd, 1);
			Individual tmpIndi = init.execute(problem);
			tmpIndi.evaluate(problem, method);
			evaluations ++;
			
//			tmpIndi.printMe();
			
			population.add(tmpIndi);
		}
		
		Sorting sorting = new Sorting(problem, popsize);
		sorting.execute(population);
		
//		for (Individual indi : population) {
//			indi.printMe();
//		}
		
		// search process
		while (evaluations < maxEvaluations) {
			
			for (int i = 0; i < (popsize / 2); i++) {
				if (evaluations < maxEvaluations) {
					// obtain parents
					Selection btSel = new BinaryTournamentSelection(rnd, 2);
					List<Individual> parents = btSel.execute(population);
					
//					System.out.println("before");		
//					System.out.println("parent1");
//					parents.get(0).printMe();
//					System.out.println("parent2");
//					parents.get(1).printMe();
					
					// create two offsprings by crossover
					Crossover spXOver = new SinglePointCrossover(rnd);
					List<Individual> xoffsprings = spXOver.execute2(parents);
					
//					System.out.println("crossover generated");
//					xoffsprings.get(0).printMe();
//					xoffsprings.get(1).printMe();
					
					Mutation singleFlip = new SingleFlipMutation(rnd);
					Individual moffspring1 = singleFlip.execute(xoffsprings.get(0));
					Individual moffspring2 = singleFlip.execute(xoffsprings.get(1));
					
//					System.out.println("mutation generated");
//					moffspring1.printMe();
//					moffspring2.printMe();
					
					moffspring1.evaluate(problem, method);
					moffspring2.evaluate(problem, method);
					
					population.add(moffspring1);
					population.add(moffspring2);
					
//					System.out.println("after");
//					System.out.println("parent1");
//					parents.get(0).printMe();
//					System.out.println("parent2");
//					parents.get(1).printMe();
			
					evaluations += 2;
				}
			}
			
			// non-dominated sorting for population update
			Sorting ndsort = new Sorting(problem, popsize);
			ndsort.execute(population);
			
//			System.out.println("size = " + population.size() + "/" + popsize);
			
			
		}
		
//		System.out.println("population:");
//		for (Individual indi : population) {
//			indi.printMe();
//		}
		
		return population;
	}
	
	
	public static void main(String[] args) throws IOException, Exception {
		String dataDir = "Data/Filter/";
		
		/*data */
        Instances[] trainTest = HelpDataset.readDataTrTe(dataDir + "zoo", false);
        Instances training = trainTest[0];
        Instances testing = trainTest[1];
        
        // create the problem
        Problem trainProb = new Problem(training);
        trainProb.printMe();
		
//		// Print header and instances.
//		System.out.println("\nDataset:\n");
//		System.out.println(testing);
        
        NSGAII nsga2 = new NSGAII(trainProb, new ShannonEntropy());
        nsga2.setPopsize(30);
        nsga2.setMaxEvaluations(1000);
        
        nsga2.execute();
	}
}
