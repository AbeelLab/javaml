/**
 * %SVN.HEADER%
 */
/*
 Copyright (C) Anagha Joshi, as part of Chinese Clustering.
 */


package net.sf.javaml.utils;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.utils.GammaFunction;

// row equals one instance, with x values or columns

public class LogLikelihoodFunction {
	// tuning parameters?? standard value:
	double alpha0 = 0.1, beta0 = 0.1, lambda0 = 0.1, mu0 = 0.0;

	double count;

	double sum;

	double sum2;

	// likelihood of each column in a given cluster
	public double logLikelihoodFunction(double N, double sum, double sum2) {
		double loglikelihood = 0;
		double lambda1 = lambda0 + N;
		double alpha1 = alpha0 + 0.5 * N;
		double beta1 = beta0 + 0.5 * (sum2 - Math.pow(sum, 2) / N) + lambda0
				* Math.pow(sum - mu0 * N, 2) / (2 * lambda1 * N);

		loglikelihood = -0.5 * N * Math.log(2 * Math.PI) + 0.5
				* Math.log(lambda0) + alpha0 * Math.log(beta0)
				- GammaFunction.logGamma(alpha0)
				+ GammaFunction.logGamma(alpha1) - alpha1 * Math.log(beta1)
				- 0.5 * Math.log(lambda1);
		return (loglikelihood);
	}

	// likelihood of all instances in a given cluster
	public double logLikelihood(Dataset cluster) {
		double instanceLength = cluster.instance(0).size();
		this.count = instanceLength * cluster.size();
		sum = 0;
		sum2 = 0;

		for (int row = 0; row < cluster.size(); row++) {
			for (int column = 0; column < instanceLength; column++) {
				sum += cluster.instance(row).value(column);
				sum2 += cluster.instance(row).value(column)
						* cluster.instance(row).value(column);
			}
		}

		double loglikelihood = logLikelihoodFunction(count, sum, sum2);
		if (loglikelihood == Double.NEGATIVE_INFINITY
				|| loglikelihood == Double.POSITIVE_INFINITY) {
			loglikelihood = 0;
		}
		return (loglikelihood);
	}

	// sum of loglikelihood of each column
	public double logLikelihoodC(Dataset cluster) {
		double instanceLength = cluster.instance(0).size();
		double loglikelihood = 0;
		double countTotal = 0;
		double sumTotal = 0;
		double sum2Total = 0;
		for (int column = 0; column < instanceLength; column++) {
			double loglike = logLikelihood(cluster);
			countTotal += this.count;
			sumTotal += this.sum;
			sum2Total += this.sum2;
			loglikelihood += loglike;
		}
		return (loglikelihood);
	}

	// total likelihood of finding data for given partition
	public double loglikelihoodsum(Dataset[] clusters) {

		double likelihood = 0;

		for (int i = 0; i < clusters.length; i++) {

			likelihood += logLikelihoodC(clusters[i]);

		}
		return (likelihood);

	}

}
