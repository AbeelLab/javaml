/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;

/**
 * H_2 from the Zhao 2001 paper
 * 
 * TODO uitleg
 * 
 * @author Andreas De Rijcke 
 */

public class HybridCentroidSimilarity implements ClusterEvaluation {
    /**
     * XXX DOC
     */
    public double score(Dataset[] datas) {
        ClusterEvaluation ceTop = new SumOfCentroidSimilarities();// I_2
        double sum = ceTop.score(datas);
        ClusterEvaluation ce = new TraceScatterMatrix();// E_1
        sum /= ce.score(datas);

        return sum;
    }
    /**
     * XXX DOC
     */
    public boolean compareScore(double score1, double score2) {
        // should be maxed
        return score2 > score1;
    }
}
