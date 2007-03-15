package net.sf.javaml.utils;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.utils.GammaFunction;

public class LogLikelihoodFunction {
		// tuning parameters?? standard value:
		double alpha0 = 0.1, beta0 = 0.1, lambda0 = 0.1, mu0 = 0.0;

		// calculates likelihood of each instance in a given cluster	
		public double logLikelihoodFunction(double N, double sum, double sum2){
			double loglikelihood = 0 ;
			double lambda1 = lambda0 + N;
			double alpha1 = alpha0 + 0.5 * N;
			double beta1 = beta0 + 0.5* (sum2 - Math.pow(sum, 2) / N)+ lambda0 * Math.pow(sum - mu0 * N, 2)/ (2 * lambda1 * N);

			loglikelihood = -0.5 * N * Math.log(2 * Math.PI) + 0.5
					* Math.log(lambda0) + alpha0 * Math.log(beta0)
					- GammaFunction.logGamma(alpha0) + GammaFunction.logGamma(alpha1) - alpha1
					* Math.log(beta1) - 0.5 * Math.log(lambda1);
			return(loglikelihood);
		}


	public double logLikelihood(Dataset[] data){
		double count=0;
		double sum=0;
		double sum2=0;
		for(Integer row : RowSet)
		{
			for(Integer column : ColumnSet)
			{
				if(!Double.isNaN(data[row][column]))
				{
				count++;
				sum+=data[row][column];
				sum2+=data[row][column]*data[row][column];
				}
			}
		}
		this.loglikelihood=logLikelihoodFunction(count,sum,sum2);
		if(Double.isNaN(this.loglikelihood)){
			this.loglikelihood=0;
		}
		return(loglikelihood);
	}
}
