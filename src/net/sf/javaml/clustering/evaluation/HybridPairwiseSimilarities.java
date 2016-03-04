/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;

/**
 * H_1 from the Zhao 2001 paper
 * 
 * TODO uitleg
 * 
 * @author Andreas De Rijcke
 */

public class HybridPairwiseSimilarities implements ClusterEvaluation {
    
    /**
     * XXX DOC
     */
    public double score(Dataset[] data) {
        ClusterEvaluation ceTop = new SumOfAveragePairwiseSimilarities();// I_1
        double sum = ceTop.score(data);
        ClusterEvaluation ce = new TraceScatterMatrix();// E_1
        sum /= ce.score(data);

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
