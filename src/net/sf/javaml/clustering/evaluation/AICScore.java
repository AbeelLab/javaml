/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.utils.*;

/**
 * XXX doc
 * 
 * @author Andreas De Rijcke
 * @author Thomas Abeel
 * 
 */
public class AICScore implements ClusterEvaluation {

	public double score(Dataset[] clusters) {
		// number of free parameters K
		double k = 1;
		LogLikelihoodFunction likelihood = new LogLikelihoodFunction();
		// loglikelihood log(L)
		double l = likelihood.loglikelihoodsum(clusters);
		// AIC score
		double aic = -2 * l + 2 * k;
		return aic;
	}

	public boolean compareScore(double score1, double score2) {
		// should be minimalized
		return Math.abs(score2) < Math.abs(score1);
	}

}
