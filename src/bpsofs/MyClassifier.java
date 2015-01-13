/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bpsofs;

import java.util.HashMap;
import java.util.Map;
import java.util.Random; 
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author xuebing
 */
public class MyClassifier {

    private weka.classifiers.Classifier classifier;
    private Random random;

    public MyClassifier(Random random) {
        this.random = random;
    }

    public void ClassifierKNN(int k) {
        System.out.println("Myclassifier:  new KNearestNeighbors  k=" + k);
        IBk kn = new IBk(k);
        setClassifier(kn);
//        System.out.println(kn.globalInfo() + "\n new LinearNNSearch();  EuclideanDistance;  \n \n ");
    }

    public void ClassifierJ48() {
        setClassifier(new J48());
        System.out.println("Myclassifier:  new J48()");
    }

    public void ClassifierRandomForest() {
        setClassifier(new RandomForest());
        System.out.println("Myclassifier:  new RandomForest()");
    }

    public void ClassifierLibSVM() {
        setClassifier((Classifier) new PolyKernel());
        System.out.println("Myclassifier:  new LibSVM()");
    }

    public void ClassifierNB() {
        setClassifier(new NaiveBayes());
        System.out.println("Myclassifier:  new NaiveBayes()");
    }

    /**
     * @return the classifier
     */
    public Classifier getClassifier() {
        return classifier;
    }

    /**
     * @param classifier the classifier to set
     */
    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public double fullclassify(Instances training, Instances testing) throws Exception {
        double[] features = new double[training.numAttributes()];
        for (int i = 0; i < training.numAttributes(); i++) {
            features[i] = 1.0;
        }
        double acc = 0.0;
        acc = classifyTPTN(training, testing);
        return acc;
    }

    /* accuracy is calculated by:  tp+tn/numClasses*(tp+tn+fp+fn). */
    public double classifyTPTN(Instances training, Instances testing) throws Exception {
        getClassifier().buildClassifier(training);
        Instances dataForClassification = testing;

        Map<Double, PerformanceMeasure> out = new HashMap<Double, PerformanceMeasure>();

        for (int c = 0; c < training.numClasses(); c++) {
            out.put(Double.valueOf(c), new PerformanceMeasure());
        }

        for (Instance instance : dataForClassification) {
            double prediction = getClassifier().classifyInstance(instance); //classifyInstance(instance) returns the index of a nominal class type (e.g.C,B,A are indexed 0,1,2)

//            System.out.println("prediction: " + prediction + " ----True: " + instance.classValue());

            if (prediction == instance.classValue()) {// prediction ==class
                for (Double o : out.keySet()) {
                    if (o.equals(instance.classValue())) {
                        out.get(o).tp++;
                    } else {
                        out.get(o).tn++;
                    }
                }
            } else {// prediction != class
                for (Double o : out.keySet()) {
                    /* prediction is positive class */
                    if (prediction == o) {
                        out.get(o).fp++;
                    } /* instance is positive class */ else if (o.equals(instance.classValue())) {
                        out.get(o).fn++;
                    } /* none is positive class */ else {
                        out.get(o).tn++;
                    }

                }
            }
        }
//        System.out.println("out====: " + out);  // this is checked, correct
        double tp = 0.0, tn = 0.0;
        double Accuracy = 0.0;
        for (Double o : out.keySet()) {
            tp += out.get(o).tp;
            tn += out.get(o).tn;
        }
        Accuracy = (tn + tp) / (double) (out.size() * dataForClassification.size());

//        System.out.println("dataForClassification.size()  " + dataForClassification.size());
//        System.out.println("tn " + tn + "    tp  " + tp + "   Accuracy  " + Accuracy);
        return Accuracy;
    }


    /* accuracy is calculated by:  correct/total. */
    public double classify(Instances training, Instances testing) throws Exception {

        getClassifier().buildClassifier(training);
        Instances dataForClassification = testing;

        int corr = 0;
        for (Instance instance : dataForClassification) {
            double prediction = getClassifier().classifyInstance(instance); //classifyInstance(instance) returns the index of a nominal class type (e.g.C,B,A are indexed 0,1,2)
//            System.out.println("prediction: " + prediction+"----instance.classValue(): " + instance.classValue());

            if (prediction == instance.classValue()) {
                corr++;
                System.out.println("yes");
                System.out.println(instance);
            }
            else {
            	System.out.println("no");
            	System.out.println(instance);
            }
        }
        double accuracy = (double) corr / dataForClassification.size();

        return accuracy;
    }
}
/*Checking classifyTPTN, out */
//	34 instances
//prediction		0	----True		0
//prediction		0	----True		0
//prediction		0	----True		0
//prediction		0	----True		0
//prediction		0	----True		0
//prediction		0	----True		0
//prediction		0	----True		0
//prediction		0	----True		0
//prediction		0	----True		0
//prediction		0	----True		0
//prediction		0	----True		0
//prediction		0	----True		0
//prediction		0	----True		0
//prediction		0	----True		0
//prediction		1	----True		1
//prediction		1	----True		1
//prediction		1	----True		1
//prediction		1	----True		1
//prediction		1	----True		1
//prediction		1	----True		1
//prediction		3	----True		3
//prediction		3	----True		3
//prediction		3	----True		3
//prediction		3	----True		3
//prediction		3	----True		3
//prediction		4	----True		4
//prediction		5	----True		5
//prediction		5	----True		5
//prediction		5	----True		5
//prediction		6	----True		6
//prediction		5	----True		6
//prediction		6	----True		6
//prediction		3	----True		2
//prediction		2	----True		2
//
//	TP	FP	TN	FN
//0	14	0	20	0
//1	6	0	28	0
//2	1	0	32	1
//3	5	1	28	0
//4	1	0	33	0
//5	3	1	30	0
//6	2	0	30	1
//
// out====: {0.0=[TP=14.0, FP=0.0, TN=20.0, FN=0.0], 3.0=[TP=5.0, FP=1.0, TN=28.0, FN=0.0], 6.0=[TP=2.0, FP=0.0, TN=31.0, FN=1.0],
//         2.0=[TP=1.0, FP=0.0, TN=32.0, FN=1.0], 1.0=[TP=6.0, FP=0.0, TN=28.0, FN=0.0],
//         4.0=[TP=1.0, FP=0.0, TN=33.0, FN=0.0], 5.0=[TP=3.0, FP=1.0, TN=30.0, FN=0.0]}

