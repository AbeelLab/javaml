/**
 * XMeansSourceCode.java, 31-okt-06
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
 * Copyright (c) 2006, Andreas De Rijcke, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */


package net.sf.javaml.clustering;

import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.core.BICScore; 
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.DistanceMeasureFactory;


public class XMeans implements Clusterer  {
		
	private int minNumClusters = 2;
	private int maxNumClusters = 4;
	private int numberOfClusters = 2;
	private int numberOfIterations = -1;
	private Random rg;
	private DistanceMeasure dm;
	private Instance[] centroids;
	private int[] assignment;
	private int dataDimension = 250;
	private double overAllBicScore = 0;
	
//TODO delete following remark:	
//code not yet complete!
	
/** step 1. Improver parameters:
 * Apply k-means to dataset with first (minimum) value for 'numberOfClusters'
*/
	
	
	public void buildClusterer(Dataset data) {
		if (data.size() == 0)
			throw new RuntimeException("The dataset should not be empty");
		if(numberOfClusters==0)
		    throw new RuntimeException("There should be at least one cluster");
		        // Place K points into the space represented by the objects that are
		        // being clustered. These points represent the initial group of
		        // centroids.
		    Instance min = data.getMinimumInstance();
		    Instance max = data.getMaximumInstance();
		    this.centroids = new Instance[numberOfClusters];
		    int instanceLength = data.getInstance(0).size();
		    for (int j = 0; j < numberOfClusters; j++) {
		    	double[] randomInstance = new double[instanceLength];
		    	for (int i = 0; i < instanceLength; i++) {
		    		double dist = Math.abs(max.getValue(i) - min.getValue(i));
		    		randomInstance[i] = (min.getValue(i) + rg.nextDouble() * dist);
		    	}
		        this.centroids[j] = new SimpleInstance(randomInstance);
		    }

		    int iterationCount = 0;
		    boolean centroidsChanged = true;
		    while (iterationCount < this.numberOfIterations && centroidsChanged) {
		    	iterationCount++;
		            // Assign each object to the group that has the closest centroid.
		        int[] assignment = new int[data.size()];
		        for (int i = 0; i < data.size(); i++) {
		        	int tmpCluster = -1;
		            double minDistance = Double.MAX_VALUE;
		            for (int j = 0; j < centroids.length; j++) {
		            	double dist = dm.calculateDistance(centroids[j], data.getInstance(i));
		                if (dist < minDistance) {
		                    minDistance = dist;
		                    tmpCluster = j;
		                }
		             }
		             assignment[i] = tmpCluster;
		        }
		            // When all objects have been assigned, recalculate the positions of
		            // the K centroids and start over.
		            // The new position of the centroid is the middle of the current
		            // cluster.
		         double[][] sumPosition = new double[this.numberOfClusters][instanceLength];
		         int[] countPosition = new int[this.numberOfClusters];
		         for (int i = 0; i < data.size(); i++) {
		        	 for (int j = 0; j < instanceLength; j++) {
		        		 double value=data.getInstance(i).getValue(j);
		                 sumPosition[assignment[i]][j] += value;
		                 countPosition[assignment[i]]++;
		             }
		         }
		         centroidsChanged = false;
		         for (int i = 0; i < this.numberOfClusters; i++) {
		        	 if (countPosition[i] > 0) {// when there are no instances
		                                            // associated with this centroid, it
		                                            // remains the same.
		        		 double[] tmp = new double[instanceLength];
		                 for (int j = 0; j < instanceLength; j++) {
		                	 tmp[j] = sumPosition[i][j] / countPosition[i];
		                 }
		                 Instance newCentroid = new SimpleInstance(tmp);
		                 if (dm.calculateDistance(newCentroid, centroids[i]) > 0.0001) {
		                        centroidsChanged = true;
		                        centroids[i] = newCentroid;
		                 }
		        	 }
		         }
		    }
	}

	public int getNumberOfClusters() {
		return this.numberOfClusters;
	}

	public int predictCluster(Instance instance) {
		//TODO implement
		return 0;
	}

	public double[] predictMembershipDistribution(Instance instance) {
		//TODO implement
		return null;
	}

/** Step 2. Improve Structure:
 * Apply k-means with K = 2 to each cluster (parents)obtained in step 1.
 * Calculate BIC score for parent (K=1) and child (K = 2) clusters.   
 * The situation with the highest BIC score is assumed as best.
 */
		
	public void localKMeans(Dataset data) {
		
		for (int j=0; j < numberOfClusters; j++){
			
			// calculate BIC for parent cluster
			Instance centroidp = centroids[j];
			int number = j;
			//TODO get assignment values from part1.
			int assignment[]; 
			// calculate variance estimate
			double variance = varianceEstimate(data, centroidp, number, assignment [], numberOfClusters);
			// calculate cluster size
			int clusterSizeP = localClusterSize (assignment[],number);	
			//calculate loglikelihood
			double loglikeP = logLikeliHood (data, variance,numberOfClusters, clusterSizeP, dataDimension);
			//calculate bic score
			double scoreP = bicScore (data, loglikeP, variance, numberOfClusters, dataDimension);

			
			// TODO implement K-Means on data assigned to centroid[i]with k = 2 (local K-means).
			
			
			//calculate BIC for child clusters
			// TODO rename some variables
			//TODO get assignment values from local K-means
			int assignment[];
//			calculate variance estimate for both child clusters (CC)
			double variance1 = varianceEstimate(data, centroids[0], 0, assignment [], 2);
			double variance2 = varianceEstimate(data, centroids[1], 1, assignment [], 2);
			// calculate cluster sizes for both child clusters
			int clusterSize1 = localClusterSize (assignment[], 0);
			int clusterSize2 = localClusterSize (assignment[], 1);		
			//calculate loglikelihood for both child clusters
			double loglike1 = logLikeliHood (data, variance1,numberOfClusters, clusterSize1, dataDimension);
			double loglike2 = logLikeliHood (data, variance2,numberOfClusters, clusterSize2, dataDimension);
			// calculate over-all loglike for child clusters = sum both loglikes
			double loglikeC = loglike1 + loglike2;
			//calculate over-all bic score for child clusters 
			double scoreC = bicScore (data, loglikeC, variance, numberOfClusters, dataDimension);
		
			
			if (scoreP>scoreC){
			// keep parent cluster as final result
//				TODO implement	
			}
			else{
			// keep child clusters as final result
//				TODO implement
			}
			}
		}
		
		
	}
		 
	  
	      
		