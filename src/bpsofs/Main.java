/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bpsofs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.yimei.objective.entropy.EntropyMethod;
import com.yimei.objective.entropy.ShannonEntropy;
import com.yimei.problem.Problem;

import weka.core.Instances;

/**
 *
 * @author xuebing
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {

        long seeder = 2 * Integer.valueOf(args[0]);
        RandomBing.Seeder.setSeed(seeder);
        System.out.println("RandomBing.Seeder.setSeed:  " + seeder);

        
		
		/*data */
        String dataDir = "Data/Filter/";
        Instances[] trainTest = HelpDataset.readDataTrTe(dataDir + "lymph", false);
        Instances training = trainTest[0];
        Instances testing = trainTest[1];

        int numFolds = 10;  // cross valisation
        Instances[] foldsTrain = HelpDataset.splitNFolds(training, numFolds);

        int dimension = training.numAttributes() - 1; // number of features

        /*classifier */
        MyClassifier myclassifier = new MyClassifier(new Random(1));
        myclassifier.ClassifierKNN(1);

        Featureselection problem = new Featureselection();

        System.out.println(" ");
        // TODO code application logic here
        Swarm s = new Swarm(50, dimension);

        s.setProblem(problem);
        s.getProblem().setMyclassifier(myclassifier);
        s.getProblem().setFoldsTrain(foldsTrain);


        /*Running PSO from here */
        long startCpuTimeNano = getCpuTime();
        for (int i = 0; i < 100; i++) {
            s.evaluate();
            s.updating();
            System.out.println(s);
        }
        System.out.println("");


        long estimatedTimeCPU = (getCpuTime() - startCpuTimeNano) / (1000 * 1000);
        
        // test selected features
        Integer[] idx = {dimension};
        Integer[] cidx = {2,4,6,9,13};
        
        List<Integer> idxSet = new ArrayList<Integer>(Arrays.asList(idx));
        List<Integer> cidxSet = new ArrayList<Integer>(Arrays.asList(cidx));
        
        List<Integer> allIdxSet = new ArrayList<Integer>(idxSet);
        allIdxSet.addAll(cidxSet);
        System.out.println(allIdxSet);
        
        Problem tprob = new Problem(training);
        EntropyMethod shannonEntropy = new ShannonEntropy();
        
        double je = shannonEntropy.entropy(allIdxSet, tprob);
        System.out.println("joint entropy = " + je);
        
        double ent = shannonEntropy.entropy(cidxSet, tprob);
        
        double ce = shannonEntropy.condEntropy(idxSet, cidxSet, tprob);
        System.out.println("cond entropy = " + ce + ", uncertainty = " + ent);
        
        
//        s.gbestpos = new int[dimension];
//        for (int i = 0; i < cidx.length; i++) {
//        	s.gbestpos[cidx[i]] = 1;
//        }

        // ******************Final Results***********************
        Instances newTrain = HelpDataset.removeFeatures(new Instances(training), s.gbestpos);
        Instances newTest = HelpDataset.removeFeatures(new Instances(testing), s.gbestpos);

        System.out.println(newTrain);
        
        System.out.println("-------------------- training ----------------");
        double trainAcc = myclassifier.classify(newTrain, newTrain);
        System.out.println("---------------------- test -------------------");
        double testAcc = myclassifier.classify(newTrain, newTest);

        System.out.println("fitness--" + s.getProblem().fitness(s.gbestpos));
        System.out.println("trainAcc " + trainAcc);
        System.out.println("testAcc " + testAcc);

        int size = HelpDataset.sizeSubset(s.gbestpos);
        System.out.println("size = " + size);

        System.out.println("CPU time: " + estimatedTimeCPU + " ms");

        try {
            File file = new File(String.valueOf(Integer.valueOf(args[0]) + 1) + "Run.txt");

            BufferedWriter output = new BufferedWriter(new FileWriter(file));

            output.write(size + "," + trainAcc + "," + testAcc + "," + estimatedTimeCPU);
            output.newLine();
            for (int i = 0; i < s.gbestpos.length; i++) {
                output.write(s.gbestpos[i] + ",");
            }
            output.newLine();
            output.newLine();
            output.write("size, trainAcc, testAcc, CPUTime");


            output.close();
        } catch (IOException e) {
        }

    }

    /** Get CPU time in nanoseconds. */
    public static long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()
                ? bean.getCurrentThreadCpuTime() : 0L;
    }
}
