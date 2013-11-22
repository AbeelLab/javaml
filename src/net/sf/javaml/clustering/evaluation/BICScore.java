/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.utils.LogLikelihoodFunction;

/**
 * XXX DOC
 * 
 * @author Andreas De Rijcke
 * @author Thomas Abeel
 * 
 */

public class BICScore implements ClusterEvaluation {

	// number of free parameters K
	private double k=1;
	public BICScore(int numberOfFreeParameters){
		this.k=numberOfFreeParameters;
	}
	public double score(Dataset[] clusters) {
	
		
		// sampelsize N
		double datasize = 0;

		for (int i = 0; i < clusters.length; i++) {
			datasize += clusters[i].size();
		}
		LogLikelihoodFunction likelihood = new LogLikelihoodFunction();
		// loglikelihood log(L)
		double l = likelihood.loglikelihoodsum(clusters);
		// BIC score
		double bic = -2 * l + Math.log10(datasize) * k;
		return bic;
	}

	public boolean compareScore(double score1, double score2) {
		// should be minimzed.
		return score2 > score1;
	}
}
