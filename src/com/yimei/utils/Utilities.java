package com.yimei.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.Random;

import weka.core.Instance;

import com.yimei.core.GlobalVariables;

public class Utilities {
	
	public static double getInstanceClass(Instance instance) {
		return instance.value(instance.numAttributes()-1);
	}
	
	// Euclidean distance between instances
	public static double distanceBetweenVectors(List<Double> vec1, List<Double> vec2) {
		
		double distance = 0.0;
		
		for (int i = 0; i < vec1.size(); i++) {
			
			distance += Math.pow(vec1.get(i) - vec2.get(i), 2.0);
		}
		
		distance = Math.sqrt(distance);
		
		return distance;
	}
	
	public static double normalize(double value, double min, double max) {
		if (value < min - GlobalVariables.errorThreshold || value > max + GlobalVariables.errorThreshold) {
			System.err.println("The value " + value + " is out of [" + min + ", " + max + "]");
		}
		
		if (min > max - GlobalVariables.errorThreshold) {
			return 0.0;
		}
		
		double norm = (value - min) / (max - min);
		
		return norm;
	}

	// min inclusive, max exclusive
	public static int randInt(int min, int max) {
		Random rnd = new Random();
		return rnd.nextInt(max - min) + min;
	}
	
	// min inclusive, max exclusive
		public static int randInt(int min, int max, Random rnd) {
			return rnd.nextInt(max - min) + min;
		}
	
	// min inclusive, max exclusive
	public static double randDouble(double min, double max) {
		Random rnd = new Random();
		return rnd.nextDouble() * (max - min) + min;
	}
	
	// min inclusive, max exclusive
	public static double randDouble(double min, double max, Random rnd) {
		return rnd.nextDouble() * (max - min) + min;
	}
	
	
	// check if an integer is even or odd
	public static boolean isEven(int n) {
		return ((n & 1) == 0);
	}
	
	/** Get CPU time in nanoseconds. */
    public static long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()
                ? bean.getCurrentThreadCpuTime() : 0L;
    }
}
