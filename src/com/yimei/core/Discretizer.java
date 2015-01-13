/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.yimei.core;

import bpsofs.HelpDataset;
import weka.core.Instances;

/**
 *
 * @author Administrator
 */
public class Discretizer {
    
    private static final int numOfIntervals = 100; // the number of intervals for discretization
     
    public static Instances discretize(Instances data) {
        Instances discData = new Instances(data);
        for (int i = 0; i < data.numAttributes(); i++) {
            if (data.attribute(i).isNumeric()) {
                double max = Double.MIN_VALUE;
                double min = Double.MAX_VALUE;
                
                for (int j = 0; j < data.size(); j++) {
                    double value = data.instance(j).value(i);
                    if (value > max) {
                        max = value;
                    }
                    if (value < min) {
                        min = value;
                    }
                }
                
                double interval = (max-min)/numOfIntervals;

                for (int j = 0; j < data.size(); j++) {
                    long discValue = Math.round((data.instance(j).value(i)-min)/interval);
                    discData.instance(j).setValue(i, discValue);
                }
            }
        }
        
        return discData;
    }
    
    public static void main(String[] args) throws Exception {
        /*data */
        Instances[] trainTest = HelpDataset.readDataTrTe("wine", true);
        Instances train = trainTest[0];
        Instances test = trainTest[1];
        
        Instances training = Discretizer.discretize(train);
        Instances testing = Discretizer.discretize(test);
        
//        for (int i = 0; i < training.size(); i++) {
//            for (int j = 0; j < training.numAttributes(); j++) {
//                System.out.print(training.instance(i).value(j) + ", ");
//            }
//            System.out.println("");
//        }
//        
//        for (int i = 0; i < training.numAttributes(); i++) {
//            System.out.println("attribute " + i + " with type " + training.attribute(i).type());
//        }
    }
}
