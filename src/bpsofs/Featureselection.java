/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bpsofs;

import weka.core.Instances;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xuebing
 */
public class Featureselection {

    private MyClassifier _myclassifier;
    private Instances[] _foldsTrain;

    public Featureselection() {
    }

    public double fitness(int[] position) {
        int numfold = getFoldsTrain().length;  // numFold cross validation in training
        double fitness = 0;

        Instances[] foldsTem = new Instances[numfold];

        for (int f = 0; f < numfold; f++) {
            foldsTem[f] = HelpDataset.removeFeatures(new Instances(getFoldsTrain()[f]), position);
        }
        double accuracy = 0.0;

        /*getNumFols cross validation, return the average of accuracies;*/
        for (int f = 0; f < numfold; f++) { //numfold--fold cross validation
            Instances testTem = foldsTem[f];
            Instances trainTem = new Instances(foldsTem[f], 1);  // an empty Set of instances

            for (int j = 0; j < numfold; j++) {
                if (j != f) {
                    trainTem = HelpDataset.appendInstances(trainTem, foldsTem[j]);
                }
            }
            try {
                accuracy += getMyclassifier().classifyTPTN(trainTem, testTem);
            } catch (Exception ex) {
                Logger.getLogger(Featureselection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        double error = 1.0 - accuracy / (double) numfold;
//        fitness = error;
        int size = HelpDataset.sizeSubset(position);

        fitness = 0.9 * error + 0.1 * ((size + 0.0) / (double) foldsTem[0].numAttributes());


        return fitness;
    }

    /**
     * @return the _myclassifier
     */
    public MyClassifier getMyclassifier() {
        return _myclassifier;
    }

    /**
     * @param myclassifier the _myclassifier to set
     */
    public void setMyclassifier(MyClassifier myclassifier) {
        this._myclassifier = myclassifier;
    }

    /**
     * @return the _foldsTrain
     */
    public Instances[] getFoldsTrain() {
        return _foldsTrain;
    }

    /**
     * @param foldsTrain the _foldsTrain to set
     */
    public void setFoldsTrain(Instances[] foldsTrain) {
        this._foldsTrain = foldsTrain;
    }
}
