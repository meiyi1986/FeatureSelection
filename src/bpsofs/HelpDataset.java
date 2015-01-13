/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bpsofs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.StratifiedRemoveFolds;
import weka.filters.unsupervised.attribute.Normalize;

/**
 *
 * @author xuebing
 */
public class HelpDataset {

    /**
     * Read data from file, and check if do normalisation.
     *
     * @param dataName indicates the data file usually the dataset name, boolean normalise shows whether doing normalisation.
     */
    public static Instances[] readDataTrTe(String dataName, boolean doNormalise) throws IOException, Exception {

//        BufferedReader reader = new BufferedReader(new FileReader("/u/students/xuebing/localxue/Java/weka-3-6-5/weka-3-6-5/data/" + "wine" + ".arff"));
        BufferedReader reader = new BufferedReader(new FileReader(dataName + ".arff"));
        Instances data = new Instances(reader);
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1); // Set the index of the class label
//            data.setClassIndex(0);
        }

        if (doNormalise) { // do normalisation;
            System.out.println("normalise attributes to [0, 1] except the class labels");
            Normalize norm = new Normalize();
            norm.setInputFormat(data);
            data = Filter.useFilter(data, norm);

        }
//        for (Instance ins : data) {
//            for (int i = 0; i < ins.numAttributes() - 1; i++) {
//                System.out.print(ins.value(i) + " ");
//            }
//            System.out.println("");
//        }
        return HelpDataset.splitTrTes(data);
    }

    /**
     * Set remove features/attributes.
     * @param features the features vectors including 1 and 0 indicating features being kept or removed.
     */
    public static Instances removeFeatures(Instances data, int[] features) {  /*Class label has to be last column*/
//        Instances newData = new Instances(data, data.numInstances());  // empty Set of instances, with same num of Attr
        Instances newData = new Instances(data);
        for (int i = features.length - 1; i > -1; i--) { // delete backward
            if (features[i] == 0.0) {
                newData.deleteAttributeAt(i);  //Deletes an attribute at the given position   * (0 to numAttributes() - 1). and not the class label
            }
        }
        return newData;
    }

    /**
     * This method split the data into 2/3 as training and 1/3 as test data;
     * @param Instances
     * StratifiedRemoveFolds approximately maintain the original class distribution:  Bing Checked
     * data.testCV  +  data.trainCV do NOT ensure the class distribution
     */
    public static Instances[] splitTrTes(Instances data) throws Exception {

//        Instances tem = new Instances(training);
        int numFold = 3;
        int idSelctFold = 2;


        StratifiedRemoveFolds splitFold = new StratifiedRemoveFolds();
        splitFold.setSeed(0);
        splitFold.setNumFolds(numFold);  //Sets the number of folds the dataset is split into
        splitFold.setFold(idSelctFold); // Selects a fold.--> @param fold the fold to be selected. from 1
        splitFold.setInputFormat(data);
        splitFold.setInvertSelection(true);
        Instances train = Filter.useFilter(data, splitFold);   // training


        /* It seems there is a bug, a new StratifiedRemoveFolds() has to RE-build
        with Same settings to ensure same split*/
        splitFold = new StratifiedRemoveFolds();
        splitFold.setSeed(0);
        splitFold.setNumFolds(numFold);  //Sets the number of folds the dataset is split into
        splitFold.setFold(idSelctFold); // Selects a fold.--> @param fold the fold to be selected.
        splitFold.setInputFormat(data);
        splitFold.setInvertSelection(false);
        Instances test = Filter.useFilter(data, splitFold); // testing

        System.out.println("Size of data = " + data.numInstances());
        System.out.println("Size of train = " + train.numInstances());
        System.out.println("Size of test = " + test.numInstances());

        return new Instances[]{train, test};
    }

    /**
     * This method split the data into N folds;
     * StratifiedRemoveFolds approximately maintain the original class distribution:  Bing Checked
     * data.testCV  +  data.trainCV do NOT ensure the class distribution
     */
    public static Instances[] splitNFolds(Instances data, int noFolds) throws Exception {

        Instances[] dataFolds = new Instances[noFolds];

        System.out.println("Size of data to split "+ noFolds + " folds is " + data.numInstances());

        for (int i = 1; i < noFolds + 1; i++) {
            StratifiedRemoveFolds splitFold = new StratifiedRemoveFolds();/* a new StratifiedRemoveFolds() has to RE-build             with Same settings to ensure same split*/
            splitFold = new StratifiedRemoveFolds();
            splitFold.setSeed(0);
            splitFold.setNumFolds(noFolds);  //Sets the number of folds the dataset is split into
            splitFold.setFold(i); // Selects a fold.--> @param fold the fold to be selected.
            splitFold.setInputFormat(data);
            splitFold.setInvertSelection(false);
            Instances test = Filter.useFilter(data, splitFold); // testing
            dataFolds[i - 1] = test;
        }

        return dataFolds;
    }

    /**
     * append or combine two Instances
     * @param two datasets to be combined together
     * Weka should have this method, but not, find later
     */
    public static Instances appendInstances(Instances ins1, Instances ins2) {
        if (ins1.numAttributes() != ins2.numAttributes()) {
            System.out.println("Two Instances should have the same number of attributes !!!");
        }

//        Instances trainTem = new Instances (ins1, 1);
        for (Instance ins : ins2) {
            ins1.add(ins);
        }
        return ins1;
    }

    /**
     * to scale the attributes to [0,1], standardization
     * @param baseline (or training) is to get max and min, data is to be standardized
     */
    public static Instances normalisation(Instances baseline, Instances data) {

        Instances stdData = new Instances(data, data.size());


        if (baseline.numAttributes() != data.numAttributes()) {
            System.out.println("Two Instances should have the same number of attributes !!!");
        }

        return data;
    }

    public static int sizeSubset(int[] features) {
        int size = 0;
        for (int j = 0; j < features.length; j++) {
            if (features[j] == 1.0) {
                size++;
            }
        }
        return size;
    }
}
