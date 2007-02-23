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

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.filter.NormalizeMean;
import net.sf.javaml.optimization.ExpectationMaximization;
import java.lang.Math;
import java.util.Vector;

// TODO finish implementation

public class AdaptiveQualityBasedClustering implements Clusterer {

	// user defined parameters
	private int minInstances = 2;

	private double significanceLevel = 0.95;

	private int maxIterMain = 50;

	// internal tuning parameters

	private int maxIter = 50;

	private double div = 1 / 30;

	private double accurRad = 0.1;

	// other variables

	private double dimension;

	private double rk_prelim;

	private double rk;

	private double rad;

	private double deltarad;

	private double variance;

	private Instance ck;

	private Vector<Vector<Instance>> finalClusters;

	private DistanceMeasure dm = new EuclideanDistance();

	private NormalizeMean normMean = new NormalizeMean();

	private ExpectationMaximization em = new ExpectationMaximization();

	// calculates mean instance of given dataset/cluster
	public Instance mean(Vector<Instance> data) {
		Instance in = data.get(0);
		int instanceLength = in.size();
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

	// calulate max distance between cluster center ck and instances in the
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
		
		// normalize dataset
		Dataset dataNorm = normMean.filterDataset(data);

		// calculate preliminary estimate of radius
		dimension = dataNorm.getInstance(0).size();
		rk_prelim = Math.sqrt((dimension - 1) / 2);

		// convert dataset of instances to vector of instances
		Vector<Instance> all = new Vector<Instance>();
		// temporarily processing vector
		Vector<Instance> cluster = new Vector<Instance>();
		for (int i = 0; i < dataNorm.size(); i++) {
			Instance in = dataNorm.getInstance(i);
			all.add(in);
			cluster.add(in);
		}
		
		int iterator = 0;
		while (iterator <= maxIterMain & all != null) {

			// step1: locate cluster center
			ck = mean(cluster);
			rad = maxDist(cluster, ck);
			deltarad = (rad - rk_prelim) * div;
			rad = rad - deltarad;
			cluster = newCluster(cluster, ck, rad);
			Instance newMean = mean(cluster);
			int iter = 0;
			while ((iter < maxIter && newMean != ck) || rad > rk_prelim) {
				iter++;
				ck = newMean;
				if (rad > rk_prelim) {
					rad = rad - deltarad;
				}
				cluster = newCluster(cluster, ck, rad);
				newMean = mean(cluster);
			}
			if (iter >= maxIter && newMean != ck) {
				ck = null;
				System.out
						.println("Undefined cluster center or no convergence.");
				return null;
			} else {
				ck = newMean;
			}

			// step 2:recalculate radius

			// calculation of sigma and a prior prob pc and pb via EM
			// temporarily vector for variance calculation via EM
			Vector<Double> varianceEst = new Vector<Double>();
			double pc = em.em(cluster, ck, rk_prelim, dimension, varianceEst);
			double pb = 1 - pc;
			variance = varianceEst.get(0);
			if (pc == 0 & varianceEst == null) {
				System.out.println("EM algorithm did not converge.");
				return null;
			}
			// calculation new radius
			double dimD = dimension - 2;
			double sD = em.sD(dimD);
			double sD1 = em.sD(dimD + 1);
			double c1 = sD
					/ Math.pow((2 * Math.PI * variance * variance), dimD / 2);
			double c2 = sD / (sD1 * Math.pow(dimD + 1, dimD / 2));
			double c3 = 1 - (1 / significanceLevel);
			rk = Math.sqrt(2 * variance * variance
					* Math.log(pb * c2 / (pc * c1 * c3)));

			if (Math.abs(rk - rk_prelim) / rk_prelim < accurRad) {
				cluster = newCluster(cluster, ck, rk);
				// remove cluster from data if valid cluster and calculate
				// centroid
				if (cluster.size() >= minInstances) {
					finalClusters.add(cluster);
					all.removeAll(cluster);
					cluster.clear();
					cluster.addAll(all);
				} else {
					System.out.println("Cluster not valid.");
					return null;
				}
			} else {
				iterator++;
			}
			// update preliminary radius estimate with new estimate
			rk_prelim = rk;
		}
		Dataset[] output = new Dataset[finalClusters.size()];
		for(int i=0;i<finalClusters.size();i++){
			Vector<Instance> getCluster = new Vector<Instance>();
			getCluster = finalClusters.get(i);
			for (int j=0; j< getCluster.size();j++){
				output[i].addInstance(getCluster.get(j));
			}
		}
		return output;
	}
}
