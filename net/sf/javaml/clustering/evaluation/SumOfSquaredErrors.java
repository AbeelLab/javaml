/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.DistanceMeasure;

/**
 * I_3 from the Zhao 2001 paper
 * 
 * TODO uitleg
 * 
 * @author Andreas De Rijcke
 */
public class SumOfSquaredErrors implements ClusterEvaluation {
    public SumOfSquaredErrors(DistanceMeasure dm) {
        this.dm = dm;
    }

    private DistanceMeasure dm;

    public double score(Dataset[] datas) {
        double sum = 0;
        for (int i = 0; i < datas.length; i++) {
            double tmpSum = 0;
            for (int j = 0; j < datas[i].size(); j++) {
                for (int k = 0; k < datas[i].size(); k++) {
                    double error = dm.measure(datas[i].instance(j), datas[i].instance(k));
                    tmpSum += error * error;
                }

            }
            sum += tmpSum / datas[i].size();
        }
        return sum;
    }

    public boolean compareScore(double score1, double score2) {
        // TODO solve bug: score is NaN when clusters with 0 instances
    	// should be minimized
        return score2 < score1;
    }

}
