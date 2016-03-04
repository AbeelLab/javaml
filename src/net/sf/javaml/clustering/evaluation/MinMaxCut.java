/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.DistanceMeasure;

/**
 * G_1 from the Zhao 2001 paper
 * 
 * TODO uitleg
 * 
 * @author Andreas De Rijcke
 */

public class MinMaxCut implements ClusterEvaluation {
    public MinMaxCut(DistanceMeasure dm) {
        this.dm = dm;
    }

    private DistanceMeasure dm;

    public double score(Dataset[] datas) {

        double sum = 0;
        for (int i = 0; i < datas.length; i++) {
            double tmpTop = 0;
            double tmp = 0;
            for (int j = 0; j < datas[i].size(); j++) {
                for (int k = 0; k < datas.length; k++) {
                    for (int p = 0; p < datas[k].size(); p++)
                        if (datas[i].instance(j) != datas[k].instance(p)) {
                            double error = dm.measure(datas[i].instance(j), datas[k].instance(p));
                            tmpTop += error;
                        }
                }
                for (int k = 0; k < datas[i].size(); k++) {
                    double error = dm.measure(datas[i].instance(j), datas[i].instance(k));
                    tmp += error;
                }
            }
            double tmpSum = tmpTop / tmp;
            sum += tmpSum;
        }
        return sum;
    }

    public boolean compareScore(double score1, double score2) {
        // should be minimized
        return score2 < score1;
    }
}
