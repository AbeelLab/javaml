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


import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.BICScore; 


public class XMeans extends SimpleKMeans implements Clusterer  {
//	TODO delete following remark:	
//	code not yet complete!
	
	// min value for k.
	private int kMin;
	// max value for k.
	private int kMax;
	// dimension of an instance: 250 bp
	private int dataDimension = 250;
	// vector of the different variance values, belonging to the local clusters in step2, according to a certain k.
	private Vector<Double> localVariances = new Vector<Double>();
	// total variance of all data, mean value of all local variance values according to a certain k.
	private double totalVariance = 0;
	// sum of all local loglikes, according to a certain k. 
	private double totalLoglike = 0;
	//total BIC score calculated over all data, according to a certain k.
	private double totalBICScore = 0;
	// vector, which temporary holds all total BIC scores belonging to all k values.
	private Vector<Double> allTotalBicScores = new Vector<Double>();
	// vector, which temporary holds all centroids belonging to a certain k.
	private Vector<Instance> tempCentroids = new Vector<Instance>();
	// vector, which temporary holds all tempCentroids belonging to all k values.
	private Vector<Vector<Instance>> allTempCentroids = new Vector<Vector<Instance>>();
	// vector, which holds the final centroids belonging to the final clustering result.
	private Vector<Instance> finalCentroids = new Vector<Instance>();
	// number of clusters in step 1., value between kMin and kMax.

	// number of clusters resulting after step 2. for a certain k.
	private int tempClusters;
	// vector, wich holds all  tempClusters for each k value.
	private Vector<Integer> totalTempClusters = new Vector<Integer>();
	// number of cluster belonging the final clustering result.
	private int resultingNumberOfClusters;
	
	
	
	public void buildClusterer(Dataset data) {
		if (data.size() == 0)
            throw new RuntimeException("The dataset should not be empty");
        if(kMin==0)
            throw new RuntimeException("There should be at least one cluster");
        
        for(int k = kMin; k == kMax; k++){
        	/** step 1. Improve parameters:
        	 * Apply k-means to initial dataset with k alternating from kMin to kMax
        	 */
	        //Clusterer km=new super(k,100);
        	this.numberOfClusters=k;
        	this.numberOfIterations=100;
        	super.buildClusterer(data);
        	
	        Dataset[]datas=new Dataset[k];
	        for(int i=0;i<k;i++){
	            datas[i]=new SimpleDataset();
	        }
	        for(int i=0;i<data.size();i++){
	            Instance in=data.getInstance(i);
	            datas[super.predictCluster(in)].addInstance(in);
	        }
	        /** Step 2. Improve Structure:
	         * Apply k-means with k = 2 to each cluster obtained in step 1 (parents)
	         * and calculate BIC score for parent (k=1) and both child clusters.   
	         * The situation with the highest BIC score is assumed as best.
	         */
	        for (int i = 0; i < k; i++){
	        	
	        	// 1. calculate BIC for parent cluster j.
	        	int initialDataSetSize = data.size();
	        	Instance centroidP = super.centroids[i];
	        	// 1.1 calculate variance estimate.
	        	double varianceP = BICScore.varianceEstimate(datas [i], centroidP, k, initialDataSetSize);
	        	// 1.2 calculate loglikelihood.
	        	double loglikeP = BICScore.logLikeliHood(datas[i], varianceP,k, initialDataSetSize, dataDimension);
	        	// 1.3 calculate bic score.
	        	double scoreP = BICScore.bicScore(datas[i], loglikeP, varianceP, k, dataDimension);
	        	
	        	// 2. apply K-Means with k = 2 on data assigned to centroid[i].
	        	SimpleKMeans kmLocal=new SimpleKMeans(2,100);
		        kmLocal.buildClusterer(datas[i]);
		        Dataset[]datasLocal=new Dataset[2];
		        for(int j = 0; j < 2; j++){
		            datasLocal[j]=new SimpleDataset();
		        }
		        for(int j = 0; j < datas[i].size(); j++){
		            Instance in=data.getInstance(j);
		            datasLocal[kmLocal.predictCluster(in)].addInstance(in);
		        }
		        // 3. calculate BIC for child clusters.
		        int parentDataSetSize = datas[i].size();
		        Instance centroidC1 = kmLocal.centroids[0];
		        Instance centroidC2 = kmLocal.centroids[1];
		        // 3.1 calculate variance estimate for both child clusters.
		        double variance1 = BICScore.varianceEstimate(datasLocal[0], centroidC1, 2, parentDataSetSize);
		        double variance2 = BICScore.varianceEstimate(datasLocal[1], centroidC2, 2, parentDataSetSize);	
		        // 3.2 calculate loglikelihood for both child clusters.
		        double loglike1 = BICScore.logLikeliHood (datasLocal[0], variance1, 2, parentDataSetSize, dataDimension);
		        double loglike2 = BICScore.logLikeliHood (datasLocal[1], variance2, 2, parentDataSetSize, dataDimension);
		        // 3.3 calculate over-all loglike for child clusters = sum both loglikes.
		        double loglikeC = loglike1 + loglike2;
		        // 3.4 calculate over-all variance for both child clusters = mean both variances.
		        double varianceC = variance1 + variance2;
		        // 3.4 calculate over-all bic score for child clusters.
		        double scoreC = BICScore.bicScore(datas[i], loglikeC, varianceC, k, dataDimension);
		        
		        // 4. save model with highest BIC score as final model.
		        if (scoreP>scoreC){
		        	// add loglike to overAllLoglike.
		        	totalLoglike += loglikeP;
		        	// save variance. 
		        	localVariances.add(varianceP);
		        	// save parent centroid as partial result for current k. 
		        	tempCentroids.add(centroidP);
		        	tempClusters++;
		        }
		        else{
		        	//add loglike to overAllLoglike. 
		        	totalLoglike += loglikeC;
		        	// save variance.
		        	localVariances.add(varianceC);
		        	// keep child centroids as partial result for current k.
		        	tempCentroids.add(centroidC1);
		        	tempCentroids.add(centroidC2);
		        	tempClusters += 2;
		        }
		        
			}
	        // save final number of clusters for current k.
	        totalTempClusters.add(tempClusters);
	        // save final resulting centroids for current k. 
	        allTempCentroids.add(tempCentroids);
	        // calculate over-all variance for the whole data set as mean of sub-variances for current k.
	        int lvs  = localVariances.size();
	        for(int i = 0; i < lvs; i++){
	        	totalVariance += localVariances.get(i);
	        }
	        totalVariance /= lvs;
	        // calculate over-all BIC score for current k add save.
	        totalBICScore = BICScore.bicScore(data, totalLoglike, totalVariance, k, dataDimension);
	        allTotalBicScores.add(totalBICScore);
	        
		}
        // for k with kMin < k < kMax:
        // find highest overallBICScore.
        int oabss  = allTotalBicScores.size();
        int index = 0;
        for (int i = 1; i < oabss; i++){
        	double largest = allTotalBicScores.get(0); 
        	if (allTotalBicScores.get(i)> largest){
        		largest = (allTotalBicScores.get(i));
        		index = i;
        	}
        }
        // find the belonging/ appropriate centroids.
        finalCentroids = allTempCentroids.get(index);
        resultingNumberOfClusters = totalTempClusters.get(index);
        
	}

	public int getNumberOfClusters() {
		return this.resultingNumberOfClusters;
	}

	
	public int predictCluster(Instance instance) {
		if (this.finalCentroids == null)
            throw new RuntimeException("The cluster should first be constructed");
        int tmpCluster = -1;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.resultingNumberOfClusters; i++) {
            double dist = dm.calculateDistance(finalCentroids.get(i), instance);
            if (dist < minDistance) {
                minDistance = dist;
                tmpCluster = i;
            }
        }
        return tmpCluster;
	}

	
	public double[] predictMembershipDistribution(Instance instance) {
        double[] tmp = new double[this.getNumberOfClusters()];
        tmp[this.predictCluster(instance)] = 1;
        return tmp;
    }
}