/**
 * AdaptiveQualityBasedClustering.java
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
 * Copyright (c) 2007, Thomas Abeel
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
import net.sf.javaml.utils.GammaFunction;

/**
 * 
 * This class implements the Adaptive Quality-based Clustering Algorithm, based
 * on the implementation in MATLAB by De Smet et al., ESAT - SCD (SISTA),
 * K.U.Leuven, Belgium.
 * 
 * @author Andreas De Rijcke
 * @author Thomas Abeel
 */

public class AdaptiveQualityBasedClustering implements Clusterer {
    private class AQBCem {

    	private int maxIter = 50;
    	
    	// dimension - 2
    	private double dimD;

    	// Pc
    	private double pc;

    	// optimized Pc
    	private double pcOp;

    	// pb
    	private double pb;

    	// optimized Pb
    	private double pbOp;

    	private double sD;

    	private double sD1;

    	private double sm;

    	private double variance;

    	// optimized variance
    	private double varianceOp;

    	// p(r|C)
    	private Vector<Double> prc = new Vector<Double>();

    	// p(r|B)
    	private Vector<Double> prb = new Vector<Double>();

    	// p(r|C)*Pc
    	private Vector<Double> prcpc = new Vector<Double>();

    	// p(r|B)*Pb
    	private Vector<Double> prbpb = new Vector<Double>();

    	// p(r)
    	private Vector<Double> pr = new Vector<Double>();

    	// p(C|r)
    	private Vector<Double> pcr = new Vector<Double>();

    	// all distances between cluster centroid and Instance < rk_prelim
    	private Vector<Double> clusterDist = new Vector<Double>();

    	private DistanceMeasure dm = new EuclideanDistance();

    	private GammaFunction gammaF = new GammaFunction();

    	// calculates first variance ( = sigma^ 2) estimate for a number of
    	// instances (checked)
    	public double var(Vector<Instance> cluster, double dimD, int instanceLength) {
    		double var;
    		double sum = 0;
    		for (int i = 0; i < cluster.size(); i++) {
    			// sum of all distances
    			for (int j = 0; j < instanceLength; j++) {
    				double r = cluster.get(i).getValue(j);
    				sum += r * r;
    			}
    		}
    		var = (1 / dimD) * (sum / cluster.size());
    		return var;
    	}

    	// calculates optimized variance (checked)
    	public double varOp(Vector<Instance> cluster, Vector<Double> pcr,
    			double dimD, double sm, int instanceLength) {
    		double varOp;
    		double sum = 0;
    		for (int i = 0; i < cluster.size(); i++) {
    			// sum of all distances
    			for (int j = 0; j < instanceLength; j++) {
    				double r = cluster.get(i).getValue(j);
    				sum += (r * r) * pcr.get(i);
    			}
    		}
    		varOp = (1 / dimD) * (sum / sm);
    		return varOp;
    	}

    	// calculates p(r|C) (checked)
    	public Vector<Double> prc(double var, Vector<Double> clusterDist,
    			double sD, double dimD) {
    		Vector<Double> prc = new Vector<Double>();
    		for (int i = 0; i < clusterDist.size(); i++) {
    			double r = clusterDist.get(i);
    			if (var == 0){
    				prc.add(0.0);
    			}
    			else if (r == 0) {
    				prc.add(1.0);
    			} else {
    				double temp = sD
    						* (1 / Math.pow(2 * Math.PI * var, (dimD / 2)))
    						* Math.pow(r, (dimD - 1))
    						* Math.exp((-r * r) / (2 * var));
    				prc.add(temp);
    			}
    		}
    		return prc;
    	}

    	// calculates p(r|B) (checked)
    	public Vector<Double> prb(double var, Vector<Double> clusterDist,
    			double sD, double sD1, double dimD) {
    		Vector<Double> prb = new Vector<Double>();
    		for (int i = 0; i < clusterDist.size(); i++) {
    			double r = clusterDist.get(i);
    			double temp = (sD / (sD1 * Math.pow(r, dimD)) * Math.pow(r,
    					dimD - 1));
    			prb.add(temp);
    		}
    		return prb;
    	}

    	// calculates p(r|X) * Px (checked)
    	public Vector<Double> prxpx(Vector<Double> prx, double px) {
    		Vector<Double> prxpx = new Vector<Double>();
    		for (int i = 0; i < prx.size(); i++) {
    			double temp = prx.get(i) * px;
    			prxpx.add(temp);
    		}
    		return prxpx;
    	}

    	// calculates p(r) (checked)
    	public Vector<Double> pr(Vector<Double> prcpc, Vector<Double> prbpb) {
    		Vector<Double> pr = new Vector<Double>();
    		for (int i = 0; i < prcpc.size(); i++) {
    			double temp = prcpc.get(i) + prbpb.get(i);
    			pr.add(temp);
    		}
    		return pr;
    	}

    	// calculates P(C|r) (checked)
    	public Vector<Double> pcr(Vector<Double> prcpc, Vector<Double> pr) {
    		Vector<Double> pcr = new Vector<Double>();
    		for (int i = 0; i < prcpc.size(); i++) {
    			double temp = prcpc.get(i) / pr.get(i);
    			pcr.add(temp);
    		}
    		return pcr;
    	}

    	// calculates sD (checked)
    	public double sD(double dimD) {
    		double sD = Math.pow(2 * Math.PI, (dimD / 2)) / gammaF.gamma(dimD / 2);
    		return sD;
    	}

    	// calculates sm
    	public double sm(Vector<Double> pcr) {
    		double sm = 0;
    		for (int i = 0; i < pcr.size(); i++) {
    			sm += pcr.get(i);
    		}
    		return sm;
    	}

    	// main algorithm
    	public double em(Vector<Instance> dataset, Vector<Instance> cluster,
    			Instance ck, double rk_prelim, double dimension, int instanceLength,
    			Vector<Double> varianceEst) {
    		dimD = dimension - 2;
    		// for each instances in cluster: calculate distance to ck
    		clusterDist.clear();
    		for (int i = 0; i < cluster.size(); i++) {
    			double distance = dm.calculateDistance(cluster.get(i), ck);
    			clusterDist.add(distance);
    		}
    		// first estimate for pc, pb
    		double clusterSize = cluster.size();
    		pc = clusterSize / dataset.size();
    		pb = 1 - pc;
    		// variance = var(cluster, clusterDist, dimD);
    		variance = var(cluster, dimD, instanceLength);
    		//System.out.println("EM : dataSize " + dataset.size() + " clusterSize "+ cluster.size());
    		//System.out.println("EM : estimate pc " + pc + " estimate pb " + pb+ " estimate variance " + variance);
    		sD = sD(dimD);
    		sD1 = sD(dimD + 1);
    		for (int i = 0; i < maxIter; i++) {
    			prc = prc(variance, clusterDist, sD, dimD);
    			prb = prb(variance, clusterDist, sD, sD1, dimD);
    			prcpc = prxpx(prc, pc);
    			prbpb = prxpx(prb, pb);
    			pr = pr(prcpc, prbpb);
    			pcr = pcr(prcpc, pr);
    			// sm = sm(pcr);
    			sm = pcr.size();
    			//System.out.println("EM : sm " + sm);
    			if (sm == 0 || sm == Double.POSITIVE_INFINITY
    					|| sm == Double.NEGATIVE_INFINITY) {
    				System.out.println("SM value not valid.");
    				varianceEst = null;
    				return 0;
    			}
    			varianceOp = varOp(cluster, pcr, dimD, sm, instanceLength);
    			//System.out.println("EM : varianceOp " + varianceOp);
    			pcOp = sm / dataset.size();
    			if (pcOp >= 1) {
    				pc = 1;
    				varianceEst.add(variance);
    				return pc;
    			} else if (pbOp == 1) {
    				pc = 0;
    				varianceEst.add(variance);
    				return pc;
    			}
    			pbOp = 1 - pcOp;
    			//System.out.println("EM : pcOp " + pcOp + " pbOp " + pbOp);
    			/*
    			 * if ( Math.abs(varianceOp - variance) < cdif & Math.abs(pcOp-pc)<
    			 * cdif){ System.out.println("No or incorrect convergence.");
    			 * varianceEst=null; return 0; }
    			 */
    			pc = pcOp;
    			pb = pbOp;
    			variance = varianceOp;
    			//System.out.println("EM : end iteration " + i);
    		}
    		
    		
    		varianceEst.add(variance);
    		//System.out.println("EM : --- end EM ---");
    		return pc;
    	}
    }
    
    
	// user defined parameters
	private int minInstances = 2;

	private double significanceLevel = 0.95;

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

	private AQBCem em = new AQBCem();

	// calculates mean instance of given dataset/cluster
	private Instance mean(Vector<Instance> data, int instanceLength) {
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
		// stop criterion
		double initDatasizeStopCrit = data.size() * 0.2;
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

		int iterator = 1, endSign = 1, nonvalidcluster = 0;
		while (iterator < maxIterMain && endSign != 0) {

			// step1: locate cluster center
			cluster = newCluster(cluster, ck, rad);
			if (cluster.size() <= minInstances) {
				nonvalidcluster = 1;
				System.out.println("step 1: non valid cluster found ");
			} else {
				nonvalidcluster = 0;
			}
			if (nonvalidcluster == 0) {
				newMean = mean(cluster, instanceLength);
				rad = maxDist(cluster, newMean);
				// move cluster center to new mean
				ck = newMean;
				int iter = 0, stop = 0;
				while ((iter < maxIter && stop < 40) || rad > rk_prelim ) {
					iter++;
					if (rad > rk_prelim) {
						rad = rad - deltarad;
						//System.out.println("step 1: new rad " + rad);
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

			// temporarily vector for variance calculation via EM
			Vector<Double> varianceEst = new Vector<Double>();
			double pc = em.em(all, cluster, ck, rk_prelim, dimension, instanceLength,
					varianceEst);
			double pb = 1 - pc;
			variance = varianceEst.get(0);
			//System.out.println("step 2 : new var " + variance + " new pc " + pc+ " new pb " + pb);
			if (pc == 0 & varianceEst == null) {
				System.out.println("EM algorithm did not converge.");
				return null;
			}
			// calculation new radius
			double dimD = dimension - 2;
			double sD = em.sD(dimD);
			double sD1 = em.sD(dimD + 1);
			//System.out.println("step 2 : sD " + sD + " sD1 " + sD1);
			double c1 = sD
					/ Math.pow((2 * Math.PI * variance * variance), dimD / 2);
			double c2 = sD / (sD1 * Math.pow(dimD + 1, dimD / 2));
			double c3 = Math.abs(1 - (1 / significanceLevel));
			//System.out.println("step 2 : c1 " + c1 + " c2 " + c2 + " c3 " + c3);
			double tmp = Math.log(pb * c2 / (pc * c1 * c3));
			double tmp2 = 2 * variance * variance * tmp;
			//System.out.println("step 2 : tmp " + tmp + " tmp2 " + tmp2);
			if (tmp2 < 0) {
				System.out.println("step 2 : tmp2 kleiner dan 0");
				return null;
			}
			rk = Math.sqrt(tmp2);
			//System.out.println("step 2 : new rk " + rk);

			if (Math.abs((rk - rk_prelim) / rk_prelim) < accurRad) {
				cluster = newCluster(cluster, ck, rk);
				if (cluster.size() > minInstances) {
					Vector<Instance> finalCluster = new Vector<Instance>();
					finalCluster.addAll(cluster);
					finalClusters.add(finalCluster);
					all.removeAll(cluster);
					cluster.clear();
					cluster.addAll(all);
					if (cluster.size() <= initDatasizeStopCrit) {
						endSign = 0;
					}
					newMean = mean(cluster, instanceLength);
					ck = newMean;
					rad = maxDist(cluster, ck);

				} else {

					cluster.clear();
					cluster.addAll(all);
					newMean = mean(cluster, instanceLength);
					ck = newMean;
					rad = maxDist(cluster, ck);
				}
			}
			
			// update preliminary radius estimate with new estimate
			rk_prelim = rk;
			iterator++;
		}

		// write results to output
		Dataset[] output = new Dataset[finalClusters.size()];
		for (int i = 0; i < finalClusters.size(); i++) {
            output[i]=new SimpleDataset();
			Vector<Instance> getCluster = new Vector<Instance>();
			getCluster = finalClusters.get(i);
			for (int j = 0; j < getCluster.size(); j++) {
				output[i].addInstance(normMean.unfilterInstance(getCluster.get(j)));
			}
		}
		return output;
	}
}
