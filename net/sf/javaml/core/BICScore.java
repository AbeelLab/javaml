/**
 * BICScore.java, 31-okt-06
 *
 * This file is part of the Java Machine Learning API
 * 
 * php-agenda is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The Java Machine Learning API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning API; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006, Andreas De Rijcke
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */


package net.sf.javaml.core;

import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.DistanceMeasureFactory;

public class BICScore {
	
	public double varianceEstimate (Dataset data,Instance centroid,int number,int assignment[],int numberOfClusters ){
		int k = numberOfClusters;
		int r = data.size();
		double variance;
		double sum = 0;
		DistanceMeasure dm=DistanceMeasureFactory.getEuclideanDistanceMeasure();
		for (int i = 0; i < assignment.length; i++) {
			if (assignment[i] == number){
				sum += dm.calculateDistance(data.getInstance(i), centroid);
			}
		}
		variance = 1/(r-k)*sum;
		return variance;
	}
	
	
	public double logLikeliHood (Dataset data, double variance, int numberOfClusters,  int clusterSize, int dataDimension){
		double loglike;
		double v = variance;
		int k = numberOfClusters;
		int r = data.size();
		int c = clusterSize;
		int d = dataDimension;
		loglike = (-c/2)*(Math.log(2*Math.PI)+d*Math.log(v)) + c*(Math.log(c/r))-(c-k)/2;
		return loglike;
	}
	
	
	public double bicScore (Dataset data, double loglike, double variance, int numberOfClusters, int dataDimension){
		double bic;
		int r = data.size();
		int k = numberOfClusters;
		int d = dataDimension;
		double l = loglike;
		double v = variance;
		double p = (k-1)+d*k+v;
		bic = l-(p/2)*Math.log(r);
		return bic;
	}
	
	public int localClusterSize (int assignment[],int clusternumber){
		int clusterSize = 0;
		for (int i = 0; i < assignment.length; i++) {
			if (assignment[i] == clusternumber){
				clusterSize++;
			}
		}
	return clusterSize;
	}

}