/**
 * ExpectationMaximization.java, 22-feb-2007
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

package net.sf.javaml.optimization;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.optimization.GammaFunction;
import java.lang.Math;
import java.util.Vector;

public class ExpectationMaximization {

	private int maxIter = 50;

	// convergence criterium
	private double cdif = 0.001;

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
	// instances checked
	public double var(Vector<Instance> cluster, double dimD) {
		if (cluster.size() == 0)
			return 0;
		double var;
		int instanceLenght = cluster.get(0).size();
		double sum = 0;
		for (int i = 0; i < cluster.size(); i++) {
			// sum of all distances
			for (int j = 0; j < instanceLenght; j++) {
				double r = cluster.get(i).getValue(j);
				sum += r * r;
			}
		}
		var = (1 / dimD) * (sum / cluster.size());
		return var;
	}

	// calculates optimized variance checked
	public double varOp(Vector<Instance> cluster, Vector<Double> pcr,
			double dimD, double sm) {
		double varOp;
		int instanceLenght = cluster.get(0).size();
		double sum = 0;
		for (int i = 0; i < cluster.size(); i++) {
			// sum of all distances
			for (int j = 0; j < instanceLenght; j++) {
				double r = cluster.get(i).getValue(j);
				sum += (r * r) * pcr.get(i);
			}
		}
		varOp = (1 / dimD) * (sum / sm);
		return varOp;
	}

	// calculates p(r|C) checked
	public Vector<Double> prc(double var, Vector<Double> clusterDist,
			double sD, double dimD) {
		Vector<Double> prc = new Vector<Double>();
		for (int i = 0; i < clusterDist.size(); i++) {
			double r = clusterDist.get(i);
			if (var == 0) {
				prc.add(0.0);
			} else if (r == 0) {
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

	// calculates p(r|B) checked, ev nog aan te passen
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

	// calculates p(r|X) * Px checked
	public Vector<Double> prxpx(Vector<Double> prx, double px) {
		Vector<Double> prxpx = new Vector<Double>();
		for (int i = 0; i < prx.size(); i++) {
			double temp = prx.get(i) * px;
			prxpx.add(temp);
		}
		return prxpx;
	}

	// calculates p(r) checked
	public Vector<Double> pr(Vector<Double> prcpc, Vector<Double> prbpb) {
		Vector<Double> pr = new Vector<Double>();
		for (int i = 0; i < prcpc.size(); i++) {
			double temp = prcpc.get(i) + prbpb.get(i);
			pr.add(temp);
		}
		return pr;
	}

	// calculates P(C|r) checked
	public Vector<Double> pcr(Vector<Double> prcpc, Vector<Double> pr) {
		Vector<Double> pcr = new Vector<Double>();
		for (int i = 0; i < prcpc.size(); i++) {
			double temp = prcpc.get(i) / pr.get(i);
			pcr.add(temp);
		}
		return pcr;
	}

	// calculates sD checked
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

	/**
	 * Calculates new estimates for variance and radius via Expectation
	 * Maximization algorithm
	 * 
	 * @param dataset
	 *            the current dataset which we want to cluster
	 * @param cluster
	 *            the cluster in progress
	 * @param ck
	 * @param rk_prelim
	 * @param dimension
	 * 
	 * @return new radius estimate
	 * 
	 */

	// main algorithm
	public double em(Vector<Instance> dataset,
			Vector<Instance> cluster, Instance ck, double rk_prelim,
			double dimension) {

		Vector<Double> estimates = new Vector<Double>();
		dimD = dimension - 2;
		// for each instances in cluster: calculate distance to ck
		clusterDist.clear();
		for (int i = 0; i < cluster.size(); i++) {
			double distance = dm.calculateDistance(cluster.get(i), ck);
			clusterDist.add(distance);
		}
		double rad = 0;
		
		// first estimate for pc, pb
		double clusterSize = cluster.size();
		pc = clusterSize / dataset.size();
		pb = 1 - pc;
		variance = var(cluster, dimD);
		sD = sD(dimD);
		sD1 = sD(dimD + 1);
		// calculate new estimates, and rad when convergence is reached
		int iter = 0, convergence = 0;
		while (iter < maxIter && convergence == 0) {
			prc = prc(variance, clusterDist, sD, dimD);
			prb = prb(variance, clusterDist, sD, sD1, dimD);
			prcpc = prxpx(prc, pc);
			prbpb = prxpx(prb, pb);
			pr = pr(prcpc, prbpb);
			pcr = pcr(prcpc, pr);
			sm = sm(pcr);
			//sm = pcr.size();
			if (sm == 0 || sm == Double.POSITIVE_INFINITY
					|| sm == Double.NEGATIVE_INFINITY) {
				System.out.println("---EM: SM value not valid.");
				estimates = null;
			}
			varianceOp = varOp(cluster, pcr, dimD, sm);
			pcOp = sm / dataset.size();
			if (pcOp >= 1) {
				pc = 1;
			} else if (pbOp >= 1) {
				pc = 0;
			}
			pbOp = 1 - pcOp;
			//System.out.println("VERSCHIL VAROP EN VAR: "+ Math.abs(varianceOp - variance));
			//System.out.println("VERSCHIL PCOP EN PC: " + Math.abs(pcOp - pc));
			if (Math.abs(varianceOp - variance) < cdif
					&& Math.abs(pcOp - pc) < cdif) {
				System.out.println("---EM: VERSCHIL VAROP EN VAR OK");
				pc = pcOp;
				pb = pbOp;
				variance = varianceOp;
				if (pc == 0 || pc == 0) {
					System.out.println("---EM: pc/pb: 0, No or incorrect convergence");
					return 0;
				}
				double cc = sD
						* (1 / Math.pow((2 * Math.PI * variance), (dimD / 2)));
				double cb = sD / (sD1 * Math.pow(rk_prelim, dimD));
				double lo = (0.95 / (1.0 - 0.95)) * ((pb * cb) * (pc * cc));
				System.out.println("---EM: cc: "+cc+", cb: "+cb+", lo: "+lo);
				if (lo < 0.0) {
					System.out.println("---EM: Not possible to calculate radius.");
					return 0;
				}
				double dis = -2 * variance * Math.log(lo);
				System.out.println("---EM: dis: "+dis);
				if (dis < 0) {
					System.out.println("---EM: Not possible to calculate radius.");
					return 0;
				}
				else{
				rad = Math.sqrt(dis);
				}
				convergence = 1;
			}
			pc = pcOp;
			pb = pbOp;
			variance = varianceOp;
			iter++;
		}
		System.out.println("---EM: rad: "+rad);
		return rad;
	}
}
