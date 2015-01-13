package com.yimei.algorithm.nsgaii;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.yimei.individual.Individual;
import com.yimei.individual.crowdingDistanceComparator;
import com.yimei.individual.normCondEntropyComparator;
import com.yimei.individual.normEntropyComparator;
import com.yimei.problem.Problem;

public class Sorting {
	
	private Problem problem;
	private int popsize;
	
	public Sorting(Problem problem, int popsize) {
		this.problem = problem;
		this.popsize = popsize;
	}

	public void execute(List<Individual> population) {
		
		// dominateMe[i] contains the number of solutions dominating i        
	    int [] dominateMe = new int[population.size()];

	    // iDominate[k] contains the list of solutions dominated by k
	    List<Integer> [] iDominate = new List[population.size()];

	    // front[i] contains the list of individuals belonging to the front i
	    List<Integer> [] front = new List[population.size()+1];
	        
	    // flagDominate is an auxiliar encodings.variable
	    int flagDominate;    

	    // Initialize the fronts 
	    for (int i = 0; i < front.length; i++)
		      front[i] = new LinkedList<Integer>();
	    
	 	//-> Fast non dominated sorting algorithm
	    // Contribution of Guillaume Jacquenot
	    for (int p = 0; p < population.size(); p++) {
		    // Initialize the list of individuals that i dominate and the number
		    // of individuals that dominate me
	    	iDominate[p] = new LinkedList<Integer>();
    		dominateMe[p] = 0;
	    }
		for (int p = 0; p < (population.size()-1); p++) {
			// For all q individuals , calculate if p dominates q or vice versa
			for (int q = p+1; q < population.size(); q++) {
				flagDominate = population.get(p).dominanceRelation(population.get(q));
		        if (flagDominate == -1) {
		        	iDominate[p].add(q);
		        	dominateMe[q] ++;
		        }
		        else if (flagDominate == 1) {
		        	iDominate[q].add(p);
		        	dominateMe[p] ++;
		        }
			}
			// If nobody dominates p, p belongs to the first front
		}
		for (int p = 0; p < population.size(); p++) {
			if (dominateMe[p] == 0) {
				front[0].add(p);
		        population.get(p).rank = 0;
			}
		}    
		    
	    //Obtain the rest of fronts
	    int numOfFronts = 0;
	    Iterator<Integer> it1, it2 ; // Iterators
	    while (front[numOfFronts].size() != 0) {
	    	numOfFronts ++;
	    	it1 = front[numOfFronts-1].iterator();
	    	while (it1.hasNext()) {
	    		it2 = iDominate[it1.next()].iterator();
	    		while (it2.hasNext()) {
	    			int index = it2.next();
	    			dominateMe[index] --;
	    			if (dominateMe[index] == 0) {
	    				front[numOfFronts].add(index);
	    				population.get(index).rank = numOfFronts;
	    			}
	    		}
	    	}
	    }
	    //<-
	    
	    
	    
	    List<List<Individual>> frontIndividuals = new ArrayList<List<Individual>>();
	    for (int j = 0; j < numOfFronts; j++) {
	    	frontIndividuals.add(new ArrayList<Individual>());
	    	it1 = front[j].iterator();
	    	while (it1.hasNext()) {
	    		frontIndividuals.get(j).add(population.get(it1.next()));
	    	}
    	}
	    
//	    for (int j = 0; j < numOfFronts; j++) {
//	    	System.out.println("front " + j);
//	    	for (Individual indi : frontIndividuals.get(j)) {
//	    		indi.printMe();
//	    	}
//	    }
	    
	    // crowding distance
	    int remain = popsize;
	    int index = 0;
	    List<Individual> currFrontIndividuals = null;
	    population.clear();

	    // Obtain the next front
	    currFrontIndividuals = frontIndividuals.get(index);
	    
	    while ((remain > 0) && (remain >= currFrontIndividuals.size())) {
	    	//Assign crowding distance to individuals
	    	crowdingDistanceAssignment(currFrontIndividuals);
	    	currFrontIndividuals.sort(new crowdingDistanceComparator());
	    	
//	    	for (Individual indi : currFrontIndividuals) {
//	    		indi.printMe();
//	    		System.out.println("crowding distance = " + indi.crowdingDistance);
//	    	}
	    	
	    	//Add the individuals of this front
	    	for (int k = 0; k < currFrontIndividuals.size(); k++) {
	    		population.add(currFrontIndividuals.get(k));
	    	} // for
	    	
	    	//Decrement remain
	    	remain = remain - currFrontIndividuals.size();
	    	
	    	//Obtain the next front
	    	index++;
	    	if (remain > 0) {
	    		currFrontIndividuals = frontIndividuals.get(index);
	    	} // if        
	    } // while
	    
	    // Remain is less than front(index).size, insert only the best one
	    if (remain > 0) {  // front contains individuals to insert                        
	    	crowdingDistanceAssignment(currFrontIndividuals);
	    	currFrontIndividuals.sort(new crowdingDistanceComparator());
	    	for (int k = 0; k < remain; k++) {
	    		population.add(currFrontIndividuals.get(k));
	    	} // for
	    	
	    	remain = 0;
	    } // if
		    
	}
	
	
	public void crowdingDistanceAssignment(List<Individual> frontIndividuals) {
		// get the normalized objective values
		for (Individual indi : frontIndividuals) {
			indi.normalize(problem);
		}
		
		int size = frontIndividuals.size();
		
		if (size == 0)
			return;
		
		if (size == 1) {
			frontIndividuals.get(0).crowdingDistance = Double.POSITIVE_INFINITY;
			return;
		} // if
		
		if (size == 2) {
			frontIndividuals.get(0).crowdingDistance = Double.POSITIVE_INFINITY;
			frontIndividuals.get(1).crowdingDistance = Double.POSITIVE_INFINITY;
			return;
		} // if 
		
		//Use a new SolutionSet to evite alter original solutionSet
	    List<Individual> tmpFront = new ArrayList<Individual>();
	    for (int i = 0; i < size; i++){
	    	tmpFront.add(frontIndividuals.get(i));
	    }

	    for (int i = 0; i < size; i++)
	    	tmpFront.get(i).crowdingDistance = 0.0;

	    double objetiveMaxn;
	    double objetiveMinn;
	    double distance;
	    
	    // sort the population by norm conditional entropy           
	    tmpFront.sort(new normCondEntropyComparator());
    	objetiveMinn = tmpFront.get(0).normCondEntropy;
    	objetiveMaxn = tmpFront.get(tmpFront.size()-1).normCondEntropy;
    	
    	//Set the crowding distance            
    	tmpFront.get(0).crowdingDistance = Double.POSITIVE_INFINITY;
    	tmpFront.get(size-1).crowdingDistance = Double.POSITIVE_INFINITY;
    	
    	for (int j = 1; j < size-1; j++) {
    		distance = tmpFront.get(j+1).normCondEntropy - tmpFront.get(j-1).normCondEntropy;
    		distance = distance / (objetiveMaxn - objetiveMinn);
    		tmpFront.get(j).crowdingDistance += distance;
    	} // for
    	
    	// sort the population by norm conditional entropy           
	    tmpFront.sort(new normEntropyComparator());
    	objetiveMinn = tmpFront.get(0).normEntropy;
    	objetiveMaxn = tmpFront.get(tmpFront.size()-1).normEntropy;
    	
    	//Set the crowding distance            
    	tmpFront.get(0).crowdingDistance = Double.POSITIVE_INFINITY;
    	tmpFront.get(size-1).crowdingDistance = Double.POSITIVE_INFINITY;
    	
    	for (int j = 1; j < size-1; j++) {
    		distance = tmpFront.get(j+1).normEntropy - tmpFront.get(j-1).normEntropy;
    		distance = distance / (objetiveMaxn - objetiveMinn);
    		tmpFront.get(j).crowdingDistance += distance;
    	} // for
	       
	} // crowdingDistanceAssing      
	
}
