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
import java.lang.Math;
import java.util.Vector;

// TODO finish implementation

public class AdaptiveQualityBasedClustering implements Clusterer {

	private int numberOfClusters;

	private Vector<Instance> centroids;

	private DistanceMeasure dm = new EuclideanDistance();

	private NormalizeMean normMean = new filterDataset();

	// user defined parameters
	private int minInstances = 2;

	private double significanceLevel = 0.95;

	// internal tuning parameters
	private int maxIter = 50;

	private double div = 1 / 30;

	private double accurRad = 0.1;

	private double dimension;

	private double rk_prelim;

	private double rk;

	private double rad;

	private double deltarad;

	private Instance ck;

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
	public void buildClusterer(Dataset data) {
		// normalize dataset
		Dataset dataNorm = normMean.filterDataset(data);

		// calculate preliminary estimate of radius
		dimension = dataNorm.getInstance(0).size();
		rk_prelim = Math.sqrt((dimension - 1) / 2);

		// convert dataset of instances to vector of instances
		// ...
		Vector<Instance> all = new Vector<Instance>();
		// temporarily processing vector
		Vector<Instance> cluster = new Vector<Instance>();
		for (int i = 0; i < dataNorm.size(); i++) {
			Instance in = dataNorm.getInstance(i);
			all.add(in);
			cluster.add(in);
		}

		while(3 stop criteria){
					
		// step1: locate cluster center
		ck = mean(cluster);
		rad = maxDist(cluster, ck);
		deltarad = (rad - rk_prelim) * div;
		rad = rad - deltarad;
		cluster = newCluster(cluster, ck, rad);
		Instance newMean = mean(cluster);
		int iter = 0;
		while (iter <= maxIter && newMean != ck || rad > rk_prelim) {
			iter++;
			ck = newMean;
			if (rad > rk_prelim){
				rad = rad - deltarad;
			}
			cluster = newCluster(cluster, ck, rad);
			newMean = mean(cluster);
		}
		/**
		 * if(newMean != ck){ ck = null; }
		 */
		
		// step 2:recalculate radius
		// calculation of sigma and a prior prob pc or pb
		if( ck != null){
			double pc, pb;
			pb = 1-pc;
			double d = dimension - 2;
			double gamma = ;
			double sd = Math.pow(2*Math.PI, d/2)/ gamma;
			rk = ;
		}
		else{
			rk = 0;			
		}
		
		if(Math.abs(rk - rk_prelim)/rk_prelim < accurRad){
			cluster = newCluster(cluster,ck, rk);
			// remove cluster from data if valid cluster and calculate centroid
			if( cluster.size()>= minInstances){
				Instance centroid = mean(cluster);
				centroids.add(centroid);
				all.removeAll(cluster);
			}
			else{
				
				//????
			}
		}
		// update preliminary radius estimate with new estimate
		rk_prelim = rk;
		}
		
	}
	public int getNumberOfClusters() {
		return this.numberOfClusters;
	}

	public int predictCluster(Instance instance) {
		if (this.centroids == null)
			throw new RuntimeException(
					"The cluster should first be constructed");
		int tmpCluster = -1;
		double minDistance = Double.MAX_VALUE;
		for (int i = 0; i < this.centroids.size(); i++) {
			double dist = dm.calculateDistance(centroids.get(i), instance);
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
