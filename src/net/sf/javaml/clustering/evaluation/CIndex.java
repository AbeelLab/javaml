/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;

/**
 * TODO uitleg
 * 
 * @author Andreas De Rijcke
 * 
 */

public class CIndex implements ClusterEvaluation {

    public CIndex(DistanceMeasure dm) {
        this.dm = dm;
    }

    private DistanceMeasure dm;

    public double score(Dataset[] clusters) {
        double dw = 0;
        double minDw = Double.MAX_VALUE, maxDw = Double.MIN_VALUE;

        // calculate intra cluster distances and sum of all.
        for (int i = 0; i < clusters.length; i++) {
        	for (int j = 0; j < clusters[i].size(); j++) {
            	Instance x = clusters[i].instance(j);
                for (int k = j + 1; k < clusters[i].size(); k++) {
                    Instance y = clusters[i].instance(k);
                    double distance = dm.measure(x, y);
                    dw += distance;
                    if (maxDw < distance) {
                        maxDw = distance;
                    }
                    if (minDw > distance) {
                        minDw = distance;
                    }
                }
            }
        }
        // calculate C Index
        double cIndex = (dw - minDw) / (maxDw - minDw);
        return cIndex;
    }

    public boolean compareScore(double score1, double score2) {
        // should be minimized ( smallest intra cluster distances)
        return score2 < score1;
    }
}
