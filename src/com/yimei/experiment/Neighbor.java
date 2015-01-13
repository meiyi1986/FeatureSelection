package com.yimei.experiment;

import weka.core.Instance;

public class Neighbor {

	private Instance instance;
	private double distance;
	private final double error = 0.0000001;
	
	public Neighbor(Instance instance, double distance) {
		this.instance = instance;
		this.distance = distance;
	}
	
	public Instance getInstance() { return instance; }
	public double getDistance() { return distance; }
	
	public boolean isCloser(Neighbor neighbor) {
		return (this.distance < neighbor.getDistance() - error);
	}
	
	public boolean isFurther(Neighbor neighbor) {
		return (this.distance > neighbor.getDistance() + error);
	}
	
	
}
