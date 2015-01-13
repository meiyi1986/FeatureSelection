package com.yimei.algorithm.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.yimei.algorithm.nsgaii.Sorting;
import com.yimei.crossover.Crossover;
import com.yimei.crossover.SinglePointCrossover;
import com.yimei.individual.Individual;
import com.yimei.individual.condEntropyComparator;
import com.yimei.initialization.Initialization;
import com.yimei.initialization.RandSetInit;
import com.yimei.mutation.Mutation;
import com.yimei.mutation.SingleFlipMutation;
import com.yimei.objective.entropy.EntropyMethod;
import com.yimei.problem.Problem;
import com.yimei.selection.BinaryTournamentSelection;
import com.yimei.selection.RandomSelection;
import com.yimei.selection.Selection;

// this is the single-objective genetic algorithm to optimize the conditional entropy

public class GeneticAlgorithm {
	// the problem
		private Problem problem;
		private EntropyMethod method;
		
		// the parameters
		private int popsize;
		private int maxEvaluations;
		
		public GeneticAlgorithm(Problem problem, EntropyMethod method) {
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
				
//				tmpIndi.printMe();
				
				population.add(tmpIndi);
			}
			
			// sort the population by conditional entropy
			population.sort(new condEntropyComparator());
			
//			for (int i = 0; i < population.size(); i++) {
//				System.out.print(i + ": ");
//				population.get(i).printMe();
//			}
			
			// search process
			while (evaluations < maxEvaluations) {
				
				for (int i = 0; i < (popsize / 2); i++) {
					if (evaluations < maxEvaluations) {
						// obtain parents
						Selection btSel = new RandomSelection(rnd, 2);
						List<Individual> parents = btSel.execute(population);
						
//						System.out.println("before");		
//						System.out.println("parent1");
//						parents.get(0).printMe();
//						System.out.println("parent2");
//						parents.get(1).printMe();
						
						// create two offsprings by crossover
						Crossover spXOver = new SinglePointCrossover(rnd);
						List<Individual> xoffsprings = spXOver.execute2(parents);
						
//						System.out.println("crossover generated");
//						xoffsprings.get(0).printMe();
//						xoffsprings.get(1).printMe();
						
						Mutation singleFlip = new SingleFlipMutation(rnd);
						Individual moffspring1 = singleFlip.execute(xoffsprings.get(0));
						Individual moffspring2 = singleFlip.execute(xoffsprings.get(1));
						
//						System.out.println("mutation generated");
//						moffspring1.printMe();
//						moffspring2.printMe();
						
						moffspring1.evaluate(problem, method);
						moffspring2.evaluate(problem, method);
						
						population.add(moffspring1);
						population.add(moffspring2);
						
//						System.out.println("after");
//						System.out.println("parent1");
//						parents.get(0).printMe();
//						System.out.println("parent2");
//						parents.get(1).printMe();
				
						evaluations += 2;
					}
				}
				
//				for (int i = 0; i < population.size(); i++) {
//					System.out.print(i + ": ");
//					population.get(i).printMe();
//				}
				
				// non-dominated sorting for population update
				population.sort(new condEntropyComparator());
				population = population.subList(0, popsize);
				
//				System.out.println("size = " + population.size() + "/" + popsize);
				
				
			}
			
//			System.out.println("population:");
//			for (Individual indi : population) {
//				indi.printMe();
//			}
			
			return population;
		}
}
