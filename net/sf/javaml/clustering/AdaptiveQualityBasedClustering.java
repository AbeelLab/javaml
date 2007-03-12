/**
 * AdaptiveQualityBasedClustering.java, 9-feb-2007
 *
 * This file is part of the Java Machine Learning API
 * 
 * The Java Machine Learning API is free software; you can redistribute it and/or modify
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
 * Copyright (c) 2007, Andreas De Rijcke
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.clustering;

import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.filter.NormalizeMean;
import net.sf.javaml.optimization.ExpectationMaximization;

/**
 * 
 * This class implements an Adaptive Quality-based Clustering Algorithm, based
 * on the implementation in MATLAB by De Smet et al., ESAT - SCD (SISTA),
 * K.U.Leuven, Belgium.
 * 
 * @author Andreas De Rijcke
 * 
 */

public class AdaptiveQualityBasedClustering implements Clusterer {

	// user defined parameters
	private int minInstances = 2;

	private int maxIterMain = 50;

	// internal tuning parameters

	private int maxIter = 50;

	private double div = 0.03333333333333;

	// 1 / 30;

	private double accurRad = 0.1;

	// other variables
	private int instanceLength;

	private double dimension;

	private double rk_prelim;

	private double rk;

	private double rad;

	private double deltarad;

	private double variance;

	private Instance ck;

	private Instance newMean;

	private Vector<Vector<Instance>> finalClusters = new Vector<Vector<Instance>>();

	private DistanceMeasure dm = new EuclideanDistance();

	private NormalizeMean normMean = new NormalizeMean();

	private ExpectationMaximization em = new ExpectationMaximization();

	// calculates mean instance of given dataset/cluster
	public Instance mean(Vector<Instance> data, int instanceLength) {
		Instance in;
		float[] sumVector = new float[instanceLength];
		for (int i = 0; i < data.size(); i++) {
			in = data.get(i);
			for (int j = 0; j < instanceLength; j++) {
				sumVector[j] += in.getValue(j);
			}
		}
		for (int j = 0; j < instanceLength; j++) {
			sumVector[j] /= data.size();
		}
		Instance mean = new SimpleInstance(sumVector);
		return mean;
	}

	// calculate max distance between cluster center ck and instances in the
	// cluster
	public double maxDist(Vector<Instance> data, Instance mean) {
		double maxDist = Double.MIN_VALUE;
		for (int i = 0; i < data.size(); i++) {
			Instance x = data.get(i);
			double distance = dm.calculateDistance(x, mean);
			if (maxDist < distance) {
				maxDist = distance;
			}
		}
		return maxDist;
	}

	// calculate instances in sphere with ck as center en rad as radius
	public Vector<Instance> newCluster(Vector<Instance> data, Instance mean,
			double rad) {
		Vector<Instance> newCluster = new Vector<Instance>();
		for (int i = 0; i < data.size(); i++) {
			Instance x = data.get(i);
			double distance = dm.calculateDistance(x, mean);
			if (distance < rad) {
				newCluster.add(x);
			}
		}
		return newCluster;
	}

	// main
	public Dataset[] executeClustering(Dataset data) {
		instanceLength = data.getInstance(0).size();
		// normalize dataset
		Dataset dataNorm = normMean.filterDataset(data);
		// convert dataset of instances to vector of instances
		Vector<Instance> all = new Vector<Instance>();
		// temporarily processing vector
		Vector<Instance> cluster = new Vector<Instance>();
		for (int i = 0; i < dataNorm.size(); i++) {
			Instance in = dataNorm.getInstance(i);
			all.add(in);
			cluster.add(in);
		}

		// initiation

		// calculate preliminary estimate of radius
		dimension = dataNorm.getInstance(0).size();
		rk_prelim = Math.sqrt((dimension - 1) / 2);

		// initiate clustercenter, radius and calculated deltarad
		ck = mean(cluster, instanceLength);
		rad = maxDist(cluster, ck);
		deltarad = (rad - rk_prelim) * div;
		rad = rad - deltarad;
		cluster = newCluster(cluster, ck, rad);
		
		int iterator = 1, endSign = 0, nonvalidcluster = 0;
		while (iterator < maxIterMain && endSign == 0) {
			System.out.println("MAIN NEW ITERATION++");
			
			// step1: locate cluster center
			if (cluster.size() <= minInstances) {
				nonvalidcluster = 1;
			} else {
				nonvalidcluster = 0;
			}
			if (nonvalidcluster == 0) {
				newMean = mean(cluster, instanceLength);
				rad = maxDist(cluster, newMean);
				System.out.println("MAIN rad:" + rad);
				// move cluster center to new mean
				ck = newMean;
				int iter = 0, stop = 0;
				while ((iter < maxIter && stop < 40) || rad > rk_prelim) {	
					iter++;
					if (rad > rk_prelim) {
						rad = rad - deltarad;
					}
					cluster = newCluster(cluster, ck, rad);
					newMean = mean(cluster, instanceLength);
					double distCkNewMean = dm.calculateDistance(ck, newMean);
					if (distCkNewMean == 0) {
						stop++;
					} else {
						stop = 0;
					}
					ck = newMean;
					
				}
			}

			// step 2:recalculate radius: calculation of sigma and a prior prob
			// pc and pb via EM

			if (cluster.size() >= minInstances) {
				System.out.println("MAIN CLUSTERSIZE VOOR EM: " + cluster.size());
				System.out.println("MAIN RADIUS VOOR EM: " + rad);
				System.out.println("MAIN RK_PRELIM: " + rk_prelim);
				rk = em.em(all, cluster, ck, rk_prelim, dimension);
				System.out.println("MAIN rk EST: " + rk);
				System.out.println("MAIN DIFF RK & RK_PRELIM: "+ Math.abs((rk - rk_prelim) / rk_prelim));
				
				if (Math.abs((rk - rk_prelim) / rk_prelim) < accurRad) {
					System.out.println("- RK klein genoeg");
					cluster = newCluster(cluster, ck, rk);
					if (cluster.size() > minInstances) {
						Vector<Instance> finalCluster = new Vector<Instance>();
						finalCluster.addAll(cluster);
						finalClusters.add(finalCluster);
						System.out.println("- CLUSTER TO FINALCLUST, SIZE: "
								+ cluster.size() + ",FINALCLUSTSIZE: "
								+ finalClusters.size());
						all.removeAll(cluster);
						cluster.clear();
						cluster.addAll(all);
						System.out.println("- NEW CLUSTER SIZE "
								+ cluster.size());
						if (cluster.size() <= minInstances) {
							System.out
									.println("- STOP ALG, REST CLUSTER TOO SMALL"
											+ cluster.size());
							endSign = 1;
						}
						// update preliminary radius estimate with new estimate
						System.out.println("- rk_prelim UPDATED");
						rk_prelim = rk;
						/*// reset start rad to max rad of new cluster in progress
						newMean = mean(cluster, instanceLength);
						ck = newMean;*/
						iterator++;
						System.out.println("- ITERATOR++ 1");
					} else {
						System.out
								.println("- NON VALID CLUSTER, CLEAR CLUSTER AND RELOAD");
						cluster.clear();
						cluster.addAll(all);
						if (cluster.size() <= minInstances) {
							System.out
									.println("- STOP ALG, REST CLUSTER TOO SMALL"
											+ cluster.size());
							endSign = 1;
						}
						/*newMean = mean(cluster, instanceLength);
						ck = newMean;
						rad = maxDist(cluster, ck);*/
						iterator++;
						System.out.println("- ITERATOR++ 2");
					}
				}
				// if verschil rk & rk_prelim > accurad
				else if (rk !=0.0){
					System.out
					.println("-- verschil rk & rk_prelim nog te groot");
					// update preliminary radius estimate with new estimate
					rk_prelim = rk;
					System.out.println("-- rk_prelim UPDATED = rk: " + rk_prelim);
					System.out
					.println("-- old rad:" + rad );
					/*rad = maxDist(cluster, ck);
					System.out
					.println("-- new rad: " + rad);*/
					iterator++;
					System.out.println("-- ITERATOR++ 3");	
				}
				else{
					System.out.println("-- rk = 0, clear cluster and reload with all, reset ck and rad");
					cluster.clear();
					cluster.addAll(all);
					/*newMean = mean(cluster, instanceLength);
					ck = newMean;
					rad = maxDist(cluster, ck);*/
					iterator++;
					System.out.println("-- ITERATOR++ 4");	
				}

			}
			// if clustersize not valid ( kleiner dan 2)
			else {
				cluster.clear();
				cluster.addAll(all);
				iterator++;
				System.out.println("--- ITERATOR++ 5");
			}
		}

		// write results to output
		Dataset[] output = new Dataset[finalClusters.size()];
		for (int i = 0; i < finalClusters.size(); i++) {
			System.out.println("FINALCLUSTSIZE: " + finalClusters.size());
			output[i] = new SimpleDataset();
			Vector<Instance> getCluster = new Vector<Instance>();
			System.out.println("CLUSTERSIZE: " + getCluster.size());
			getCluster = finalClusters.get(i);
			for (int j = 0; j < getCluster.size(); j++) {
				output[i].addInstance(normMean.unfilterInstance(getCluster
						.get(j)));
			}
		}
		return output;
	}
}
