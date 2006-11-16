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
 * Copyright (c) 2006, Andreas De Rijcke and Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

/**
 * X-means: extention of K-means
 * The main object of X-means is the division of clusters, found by a K-means run,
 * into two child clusters. These child clusters are preserved only if there BIC score
 * is higher than there parent cluster, wich should indicate they are a better result.
 * To optimize the final result, the initial K-means run is applied with different K values,
 * altering from a minimum to a maximum value, respectively kMin and kMax.
 * The number of clusters obtained as the final result, is not equal to the belonging K value
 * of the initial K-means run. In fact, the number of cluster is a value between kMin and 2xkMax.
 */
package net.sf.javaml.clustering;

import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;


/**
 * TODO marked for deletion
 * 
 * @author Thomas Abeel, Andreas De Rijcke
 *
 */
@Deprecated
public class XMeans extends SimpleKMeans implements Clusterer {
	// TODO delete following remark:
	// code not yet complete!
	// BICScore calculation not yet correct.

	// min value for k.
	private int kMin;

	// max value for k.
	private int kMax;

	// vector, which temporary holds all centroids belonging to a certain k.
	private Vector<Instance> tempCentroids = new Vector<Instance>();

	// vector, which holds the final centroids belonging to the final clustering
	// result.
	private Vector<Instance> finalCentroids = new Vector<Instance>();

	// number of cluster belonging the final clustering result.
	private int resultingNumberOfClusters;

	public XMeans(int kMin, int kMax) {
		this.kMax = kMax;
		this.kMin = kMin;
	}

	public void buildClusterer(Dataset data) {
		// double finalBICScore = Double.NEGATIVE_INFINITY;
		// double finalBICScore = Double.MIN_VALUE;
		double cosSimFinal = 0;
		if (data.size() == 0)
			throw new RuntimeException("The dataset should not be empty");
		if (kMin == 0)
			throw new RuntimeException("There should be at least one cluster");
		//double dataDimension = data.getInstance(0).size();

		// step 1. Improve parameters: Apply k-means to initial dataset with
		// k alternating from kMin to kMax

		for (int k = kMin; k <= kMax; k++) {
			System.out.println("k = " + k);
			System.out.println("cosSimFinal = " + cosSimFinal);
			// number of clusters stored as final result for a certain k.
			// final number is stored in totalTempCluster.
			int tempClusters = 0;
			System.out.println("//reset temp variables to 0:");
			System.out.println("tempClusters = " + tempClusters);
			
			double cosSimTotal = 0;
			System.out.println("cosSimTotal = " + cosSimTotal);
			// sum of all local loglikes, according to a certain k.
			// double totalLoglike = 0;
			// System.out.println("totalLoglike = "+totalLoglike);
			// sum of all local variances for a certain k.
			// double localVariances = 0;
			// total variance of all data, mean value of all local variance
			// values
			// according to a certain k.
			// double totalVariance = 0;
			// System.out.println("totalVariance = "+totalVariance);
			// total BIC score calculated over all data, according to a certain
			// k.
			// double totalBICScore = 0;
			tempCentroids.clear();

			// Clusterer km=new super(k,100);
			this.numberOfClusters = k;
			this.numberOfIterations = 100;
			super.buildClusterer(data);
			Dataset[] datas = new Dataset[k];
			for (int i = 0; i < k; i++) {
				datas[i] = new SimpleDataset();
			}
			for (int i = 0; i < data.size(); i++) {
				Instance in = data.getInstance(i);
				datas[super.predictCluster(in)].addInstance(in);
			}

			// Step 2. Improve Structure: Apply k-means with k = 2 to each
			// cluster obtained in step 1 (parents) and calculate BIC score for
			// parent (k=1) and both child clusters. The situation with the
			// highest BIC score is assumed as best.

			for (int i = 0; i < k; i++) {
				System.out.println("//variables of parent cluster"+i+":");
//		/*		double cosSimP = 0;
//				cosSimP += CosSim.cosSim(datas[i],
//						super.centroids[i]);
//			*/	System.out.println("cosSimP cluster"+i+"= " + cosSimP);

				if (datas[i].size() != 0) {

					// 1. calculate BIC for parent cluster j.
					int initialDataSetSize = data.size();
					int clusterSize = datas[i].size();
					System.out.println("initialDataSetSize = "
							+ initialDataSetSize);
					System.out.println("clusterSize = " + clusterSize);
					// Instance centroidP = super.centroids[i];
					// 1.1 calculate variance estimate.
					// double varianceP = BICScore.varianceEstimate(datas[i],
					// centroidP, k);
					// 1.2 calculate loglikelihood.
					// double loglikeP = BICScore.logLikeliHood(datas[i],
					// varianceP, k, initialDataSetSize, dataDimension);
					// 1.3 calculate bic score.
					// double scoreP = BICScore.bicScore(datas[i], loglikeP,
					// varianceP, k, dataDimension);
					// System.out.println("variance parent = "+varianceP);
					// System.out.println("loglike parent = "+loglikeP);
					// System.out.println("BIC parent = "+scoreP);

					// 2. apply K-Means with k = 2 on data assigned to
					// centroid[i].
					SimpleKMeans kmLocal = new SimpleKMeans(2, 100);
					kmLocal.buildClusterer(datas[i]);
					Dataset[] datasLocal = new Dataset[2];
					for (int j = 0; j < 2; j++) {
						datasLocal[j] = new SimpleDataset();
					}
					for (int j = 0; j < datas[i].size(); j++) {
						Instance in = data.getInstance(j);
						datasLocal[kmLocal.predictCluster(in)].addInstance(in);
					}
					System.out.println("clustersize child1 = "
							+ datasLocal[0].size());
					System.out.println("clustersize child2 = "
							+ datasLocal[1].size());

					// 3. calculate BIC for child clusters.
					// int parentDataSetSize = datas[i].size();
					// double varianceC, loglike1, loglike2, loglikeC;
					// Instance centroidC1 = kmLocal.centroids[0], centroidC2 =
					// kmLocal.centroids[1];
					double cosSimC = 0;
					if (datasLocal[0].size() != 0 && datasLocal[1].size() != 0) {
//						for (int j = 0; j < 2; j++) {
//							cosSimC += CosSim.cosSim(
//									datasLocal[j], kmLocal.centroids[j]);
//						}
						System.out.println("cosSimC = " + cosSimC);

						/**
						 * //3.1 calculate variance estimate for both child
						 * clusters + // calculate over-all variance for both
						 * child clusters = // mean both variances. double
						 * variance1 = BICScore.varianceEstimate(datasLocal[0],
						 * centroidC1, 2); //double variance2 =
						 * BICScore.varianceEstimate(datasLocal[1], centroidC2,
						 * 2); varianceC = (variance1 + variance2)/2;
						 * System.out.println("variance child1 = "+variance1);
						 * System.out.println("variance child2 = "+variance2);
						 * System.out.println("variance childs = "+varianceC); //
						 * 3.2 calculate loglikelihood for both child clusters + //
						 * calculate over-all loglike for child clusters = sum //
						 * both loglikes. loglike1 =
						 * BICScore.logLikeliHood(datasLocal[0], variance1, k,
						 * parentDataSetSize, dataDimension); loglike2 =
						 * BICScore.logLikeliHood(datasLocal[1], variance2, k,
						 * parentDataSetSize, dataDimension); loglikeC =
						 * loglike1 + loglike2; System.out.println("loglike
						 * child1 = "+loglike1); System.out.println("loglike
						 * child2 = "+loglike2); System.out.println("loglike
						 * childs = "+loglikeC); // 3.5 calculate over-all bic
						 * score for child clusters. //double scoreC =
						 * BICScore.bicScore(datas[i], loglikeC, varianceC, k,
						 * dataDimension); System.out.println("BIC childs =
						 * "+scoreC);
						 * 
						 *  // 4. save model with highest BIC score as final
						 * model. if (scoreP > scoreC) { // add loglike to
						 * overAllLoglike. totalLoglike += loglikeP; // save
						 * variance. localVariances += varianceP; // save parent
						 * centroid as partial result for current k.
						 * tempCentroids.add(centroidP); tempClusters++;
						 * System.out.println("parent cluster saved"); } else if
						 * (scoreP < scoreC){ // add loglike to overAllLoglike.
						 * totalLoglike += loglikeC; // save variance.
						 * localVariances += varianceC; // keep child centroids
						 * as partial result for current k.
						 * tempCentroids.add(centroidC1);
						 * tempCentroids.add(centroidC2); tempClusters += 2;
						 * System.out.println("child clusters 1 and 2 saved"); }
						 */
//						if (cosSimP > cosSimC) {
//							tempCentroids.add(super.centroids[i]);
//							tempClusters++;
//							System.out.println("parent cluster saved");
//							cosSimTotal += cosSimP;
//						} else if (cosSimP <= cosSimC) {
//							tempCentroids.add(kmLocal.centroids[0]);
//							tempCentroids.add(kmLocal.centroids[1]);
//							tempClusters += 2;
//							System.out.println("child clusters 1 and 2 saved");
//							cosSimTotal += cosSimC;
//						}

					}
//					if (datasLocal[0].size() == 0) {
//						cosSimC += CosSim.cosSim(
//								datasLocal[1], kmLocal.centroids[1]);
//						System.out.println("cosSimC = " + cosSimC);
//						if (cosSimP > cosSimC) {
//							tempCentroids.add(super.centroids[i]);
//							tempClusters++;
//							System.out.println("parent cluster saved");
//							cosSimTotal += cosSimP;
//						} else if (cosSimP <= cosSimC) {
//							tempCentroids.add(kmLocal.centroids[1]);
//							tempClusters++;
//							System.out.println("child cluster 2 saved");
//							cosSimTotal += cosSimC;
//						}
//
//						/**
//						 * varianceC = BICScore.varianceEstimate(datasLocal[1],
//						 * centroidC2, 2); System.out.println("variance child2 =
//						 * "+varianceC); loglikeC =
//						 * BICScore.logLikeliHood(datasLocal[1], varianceC, k,
//						 * parentDataSetSize,dataDimension);
//						 * System.out.println("loglike child2 = "+loglikeC);
//						 * double scoreC = BICScore.bicScore(datas[i], loglikeC,
//						 * varianceC, k, dataDimension); System.out.println("BIC
//						 * child2 = "+scoreC); if (scoreP > scoreC) { // add
//						 * loglike to overAllLoglike. totalLoglike += loglikeP; //
//						 * save variance. localVariances += varianceP; // save
//						 * parent centroid as partial result for current k.
//						 * tempCentroids.add(centroidP); tempClusters++;
//						 * System.out.println("parent cluster saved"); } else if
//						 * (scoreP < scoreC){ // add loglike to overAllLoglike.
//						 * totalLoglike += loglikeC; // save variance.
//						 * localVariances += varianceC; // keep child centroids
//						 * as partial result for current k.
//						 * tempCentroids.add(centroidC2); tempClusters++;
//						 * System.out.println("child cluster 2 saved "); }
//						 */
//
//					}
//					if (datasLocal[1].size() == 0) {
//						cosSimC += CosSim.cosSim(
//								datasLocal[0], kmLocal.centroids[0]);
//						System.out.println("cosSimC = " + cosSimC);
//						if (cosSimP > cosSimC) {
//							tempCentroids.add(super.centroids[i]);
//							tempClusters++;
//							System.out.println("parent cluster saved");
//							cosSimTotal += cosSimP;
//						} else if (cosSimP <= cosSimC) {
//							tempCentroids.add(kmLocal.centroids[0]);
//							tempClusters++;
//							System.out.println("child cluster 1 saved");
//							cosSimTotal += cosSimC;
//						}
//						/**
//						 * varianceC = BICScore.varianceEstimate(datasLocal[0],
//						 * centroidC1, 2); System.out.println("variance child1 =
//						 * "+varianceC); loglikeC =
//						 * BICScore.logLikeliHood(datasLocal[0], varianceC, k,
//						 * parentDataSetSize,dataDimension);
//						 * System.out.println("loglike child1 = "+loglikeC);
//						 * double scoreC = BICScore.bicScore(datas[i], loglikeC,
//						 * varianceC, k, dataDimension); System.out.println("BIC
//						 * child1 = "+scoreC); if (scoreP > scoreC) { // add
//						 * loglike to overAllLoglike. totalLoglike += loglikeP; //
//						 * save variance. localVariances += varianceP; // save
//						 * parent centroid as partial result for current k.
//						 * tempCentroids.add(centroidP); tempClusters++;
//						 * System.out.println("parent cluster saved"); } else if
//						 * (scoreP < scoreC){ // add loglike to overAllLoglike.
//						 * totalLoglike += loglikeC; // save variance.
//						 * localVariances += varianceC; // keep child centroids
//						 * as partial result for current k.
//						 * tempCentroids.add(centroidC1); tempClusters++;
//						 * System.out.println("child cluster 1 saved"); }
//						 */
//
//					}
//					System.out.println("cosSimTotal = " + cosSimTotal);
//				}
//				else {
//					System.out.println("cluster"+i+" is leeg");
//				}
				
			}
			System.out.println("tempClusters = " + tempClusters);
			System.out.println(" ");
			if (cosSimTotal >= cosSimFinal) {
				cosSimFinal = cosSimTotal;
				System.out.println("cosSimFinal = " + cosSimFinal);
				resultingNumberOfClusters = tempClusters;
				System.out.println("resultingNumberOfClusters = "
						+ resultingNumberOfClusters);
				finalCentroids = tempCentroids;
				System.out.println("finalCentroids = " + finalCentroids.size());
			} else {
				System.out.println("cosSimFinal = " + cosSimFinal);
				System.out.println("resultingNumberOfClusters = "
						+ resultingNumberOfClusters);
				System.out.println("finalCentroids = " + finalCentroids.size());
				System.out.println(" ");
				System.out.println(" ");}}}
//			}

			/**
			 * System.out.println("localVariances = "+localVariances);
			 * totalVariance = localVariances / tempClusters;
			 * System.out.println("totalVariance = "+totalVariance);
			 * System.out.println("totalLoglike = "+totalLoglike); totalBICScore =
			 * BICScore.bicScore(data, totalLoglike, totalVariance, k,
			 * dataDimension); System.out.println("totalBICScore =
			 * "+totalBICScore); System.out.println(" "); System.out.println("
			 * ");
			 * 
			 * if (totalBICScore >= finalBICScore) { finalBICScore =
			 * totalBICScore; System.out.println("finalBICScore =
			 * "+finalBICScore); resultingNumberOfClusters = tempClusters;
			 * System.out.println("resultingNumberOfClusters =
			 * "+resultingNumberOfClusters); finalCentroids = tempCentroids;
			 * System.out.println("finalCentroids = "+finalCentroids.size()); }
			 * else{ System.out.println("finalBICScore = "+finalBICScore);
			 * System.out.println("resultingNumberOfClusters =
			 * "+resultingNumberOfClusters); System.out.println("finalCentroids =
			 * "+finalCentroids.size()); } System.out.println(" ");
			 * System.out.println(" ");
			 */
//		}
	}

	public int getNumberOfClusters() {
		return this.resultingNumberOfClusters;
	}

	public int predictCluster(Instance instance) {
		if (this.finalCentroids == null)
			throw new RuntimeException(
					"The cluster should first be constructed");
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