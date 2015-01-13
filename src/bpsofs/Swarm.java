/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bpsofs;

import java.util.ArrayList;
import java.util.Random;
//import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression;

/**
 *
 * @author xuebing
 */
public class Swarm {

    int ssize = -1;
    int dim = -1;
    int[][] pos;
    int[][] pbestpos;
    int[] gbestpos;
    double[] obj;
    double gbestobj;
    double[] pbestobj;
    Random rnd = RandomBing.Create();
    ArrayList xsample;
    ArrayList<Double> ysample;
    private Featureselection problem;

    public Swarm(int popsize, int d) {
        xsample = new ArrayList();
        ysample = new ArrayList();
        ssize = popsize;
        dim = d;
        pos = new int[popsize][d];
        pbestpos = new int[popsize][d];
        gbestpos = new int[d];
        obj = new double[popsize];
        pbestobj = new double[popsize];
        gbestobj = Double.POSITIVE_INFINITY;/* Max--Min */
        for (int i = 0; i < pos.length; i++) {
            for (int j = 0; j < pos[i].length; j++) {
                pos[i][j] = rnd.nextBoolean() ? 1 : 0;
            }
            pbestobj[i] = Double.POSITIVE_INFINITY; /* Max--Min */
        }
    }
    int[] refbench;

    public double fitness(int index) {
        double fit = 0;

        int[] position = pos[index];

        fit = getProblem().fitness(position);

        int[] x = new int[dim];
        System.arraycopy(pos[index], 0, x, 0, pos[index].length);

        xsample.add(x);
        ysample.add(fit);
        return fit;
    }

    public void evaluate() {
        for (int i = 0; i < pos.length; i++) {
            obj[i] = fitness(i);
            if (obj[i] < pbestobj[i]) {  /* Max--Min */
                pbestobj[i] = obj[i];
                System.arraycopy(pos[i], 0, pbestpos[i], 0, pos[i].length);
            }
            if (pbestobj[i] < gbestobj) {/* Max--Min */
                gbestobj = pbestobj[i];
                System.arraycopy(pbestpos[i], 0, gbestpos, 0, pbestpos[i].length);
            }
        }
    }

    public void updating() {
        double[] coff = null;
//        if (xsample.size() > dim) {
//            coff = doRegression(xsample, ysample);
//        }
        for (int i = 0; i < pos.length; i++) {
            updating(i, coff);
        }
    }
    double a1 = 0.3;
    double a2 = 0.65;
    double a3 = 0.0;

    public void updating(int index, double[] coff) {
        double avgcoff = -1;
        if (coff != null) {
            avgcoff = average(coff);
        }
        for (int i = 0; i < pos[index].length; i++) {
            double prob = 0.05;
            if (pos[index][i] != pbestpos[index][i]) {
                prob += a1;
            }
            if (pos[index][i] != gbestpos[i]) {
                prob += a2;
            }
            if (coff != null) {
                int coffref = (coff[i] > avgcoff) ? 1 : 0;
                if (pos[index][i] != coffref) {
                    prob += a3;
                }
            }
            if (rnd.nextDouble() < prob) {
                pos[index][i] = 1 - pos[index][i];
            }
        }
    }

    public void resetpos() {
        for (int i = 0; i < pos.length; i++) {
            for (int j = 0; j < pos[i].length; j++) {
                pos[i][j] = rnd.nextBoolean() ? 1 : 0;
            }
        }
    }

    double average(double[] x) {
        double avg = 0;
        for (int i = 0; i < x.length; i++) {
            avg += x[i];
        }
        avg /= x.length;
        return avg;
    }

    public String toString() {
        return "" + average(obj) + " -- " + average(pbestobj) + " -- " + gbestobj;
    }

//    public static double[] doRegression(ArrayList x, ArrayList<Double> y) {
//        int nCoff = ((int[]) x.get(0)).length;
//        double[][] xx = new double[(int) (x.size())][nCoff];
//        double[] yy = new double[(int) (x.size())];
//        for (int i = 0; i < x.size(); i++) {
//            for (int j = 0; j < nCoff; j++) {
//                xx[i][j] = ((int[]) x.get(i))[j];
//            }
//            yy[i] = y.get(i);
//        }
//        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
//        regression.setNoIntercept(true);
//        regression.newSampleData(yy, xx);
//        return regression.estimateRegressionParameters();
//    }

    /**
     * @return the problem
     */
    public Featureselection getProblem() {
        return problem;
    }

    /**
     * @param problem the problem to set
     */
    public void setProblem(Featureselection problem) {
        this.problem = problem;
    }
}
