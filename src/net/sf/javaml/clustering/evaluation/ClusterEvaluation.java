/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;

/**
 * This interface provides a frame for all measure that can be used to evaluate
 * the quality of a clusterer.
 * 
 * @author Thomas Abeel
 * 
 */
public interface ClusterEvaluation {

    /**
     * Returns the score the current clusterer obtains on the dataset.
     * 
     * @param data
     *            the original data
     * @param d
     *            the dataset to test the clusterer on.
     * @return the score the clusterer obtained on this particular dataset
     */
    public double score(Dataset[] clusters);

    /**
     * Compares the two scores according to the criterion in the implementation.
     * Some score should be maxed, others should be minimized. This method
     * returns true if the second score is 'better' than the first score.
     * 
     * @param score1
     *            the first score
     * @param score2
     *            the second score
     * @return true if the second score is better than the first, false in all
     *         other cases
     */
    public boolean compareScore(double score1, double score2);
}
