/**
 * Ant.java, 21-nov-2006
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
 * Copyright (c) 2006, Andreas De Rijcke
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.clustering;

import java.util.Vector;
import java.util.Random;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * 
 * This class implements an Ant Based Clustering Algorithm based on some ideas
 * from papers by Handl et al. and Schockaert et al.
 * 
 * The distance measure should be normalized in the interval [0,1].
 * 
 * @param iterations
 *            number of iterations of main loop
 * @param maxFailMoves
 *            maximum number of moves 'ants' are allowed to make, when failing
 *            to drop an instance in an existing cluster. when maxFailMoves
 *            reached, instance will be dropped in a new cluster
 * 
 * @author Andreas De Rijcke
 * 
 */

public class Ant implements Clusterer {
	Vector<Instance> tower = new Vector<Instance>();

	Vector<Instance> tower1 = new Vector<Instance>();

	Vector<Vector<Instance>> clusters = new Vector<Vector<Instance>>();

	private Instance leastSim;

	private Instance carried;

	private Random rg = new Random(System.currentTimeMillis());

	private int iterations;

	private double maxDis;

	private double actMoves;

	private double failMovesDrop;
	
	private double failMovesPick;

	private double failMovesGlobal;

	private double maxFailMoves;

	private int randomTower;

	private double alfa;

	private double nFunction;

	private double probPick;

	private double probDrop;

	private double randomProb;

	private DistanceMeasure dm = new EuclideanDistance();

	public Ant() {
		this(100, 10);
	}

	public Ant(int iterations, int maxFailMoves) {
		this.iterations = iterations;
		this.maxFailMoves = maxFailMoves;
	}

	

	// methode: calculate alfa (scaling param)
	public double alfa(double alfa, double failMoves, double actMoves) {
		double rateFail = failMoves / actMoves;
		if (rateFail > 0.99) {
			alfa += 0.01;
			if (alfa > 1) {
				alfa = 1;
			}
		}
		if (rateFail <= 0.99) {
			alfa -= 0.01;
			if (alfa < 0.01) {
				alfa = 0.01;
			}
		}
		// System.out.println("alfa: " + alfa);
		return alfa;
	}

	// methode: calculate max distance between instances
	public double maxDis(Dataset data) {
		Instance min = data.getMinimumInstance();
		Instance max = data.getMaximumInstance();
		double maxDis = dm.calculateDistance(min, max);
		// System.out.println("max dist: " + maxDis);
		return maxDis;
	}

	// methode: calculate neighborhood function
	public double nFunction(double alfa, double maxDis, Instance x,
			Vector<Instance> tower) {
		// System.out.println("alfa: " + alfa);
		double nFunction = 0;
		for (int j = 0; j < tower.size(); j++) {
			Instance tmp = tower.get(j);
			double distance = dm.calculateDistance(x, tmp);
			double relativeDistance = distance/maxDis;
			// System.out.println("relativeDistance: "+relativeDistance);
			double tmpNFunction =  1 - (relativeDistance / alfa);
			// System.out.println("temp neighborhood function: "+tmpNFunction);
			nFunction += tmpNFunction;
		}
		// System.out.println("total neighborhood function: "+nFunction);
		
		nFunction = Math.max(nFunction, 0);
		// double towerSize = tower.size();
		// System.out.println("towerSize: " + towerSize);
		// nFunction /= towerSize;
		return nFunction;
	}

	// methode: calculate probability for picking-up an instance
	public double probPick(Instance instance, double nFunction) {
		double kPlus = 0.1;
		double probPick;
		/**
		 * if (nFunction<=1){ probPick=1.0; } else{ probPick = 1 /
		 * (nFunction*nFunction); }
		 */
		System.out.println("nfuction: "+nFunction);
		double tmp = (kPlus / (kPlus + nFunction));
		System.out.println("tmp: "+tmp);
		probPick = tmp*tmp;
		System.out.println("probPick: "+probPick);
		return probPick;
	}

	// X. alternative methode for picking-up an instance of a tower
	//X.1 calculates mean instance of given tower
	public Instance mean(Vector<Instance> tower, int instanceLength) {
		Instance in;
		float[] sumVector = new float[instanceLength];
		for (int i = 0; i < tower.size(); i++) {
			in = tower.get(i);
			for (int j = 0; j < instanceLength; j++) {
				sumVector[j] += in.getValue(j);
			}
		}
		for (int j = 0; j < instanceLength; j++) {
			sumVector[j] /= tower.size();
		}
		Instance mean = new SimpleInstance(sumVector);
		return mean;
	}
	
	// X.2 calculate average (dis)similarity
	public double averageSim(Vector<Instance> tower, int instanceLength) {
		double averageSim = 0;
		//mean of the tower
		Instance towerMean = mean(tower,instanceLength );
		// average similatiry of the tower
		for (int i = 0; i < tower.size(); i++){
			double distance = dm.calculateDistance(towerMean, tower.get(i));
			averageSim += distance;
		}
		averageSim /= tower.size();
		return averageSim;
	}
	//X.3 find least similar instance in tower
	private Instance leastSim(Vector<Instance> tower, Instance mean, double averageSim) {
		Instance leastSim= null;
		double distance=0;
		for (int i = 0; i < tower.size(); i++) {
			Instance x = tower.get(i);
			System.out.println("instance: "+ x);
			distance = dm.calculateDistance(x, mean);
			System.out.println("distance: "+ distance);
			double maxDist = Double.MIN_VALUE;
			if (maxDist < distance) {
				maxDist = distance;
				leastSim = x;
				System.out.println("leastSim: "+ leastSim);
			}
		}
		if (distance <= averageSim){
			leastSim =null;
		}
		return leastSim;
	}

	
	// methode: calculate probability for dropping an instance
	public double probDrop(Instance instance, double nFunction) {
		double kMin = 0.3;
		double probDrop;
		/**
		 * if (nFunction>=1){ probDrop=1.0; } else{ probDrop =
		 * nFunction*nFunction*nFunction*nFunction; }
		 */
		System.out.println("nfuction: "+nFunction);
		double tmp =(nFunction / (kMin + nFunction));
		probDrop = tmp*tmp;
		System.out.println("tmp: "+tmp);
		System.out.println("probDrop: "+probDrop);
		return probDrop;
	}
	
//	 methode: search for least similar instance in tower
	public Instance pickLeastSim(Vector<Instance> tower) {
		double leastSim = 0;
		int leastSimIndex = 0;
		int i;
		double error;
		for (i = 0; i < tower.size(); i++) {
			Instance x = tower.get(i);
			error = 0;
			for (int j = 0; j < tower.size(); j++) {
				Instance y = tower.get(j);
				double newError = dm.calculateDistance(x, y);
				error += newError;
			}
			if (error > leastSim) {
				leastSim = error;
				leastSimIndex = i;
			}
		}
		Instance pickLeastSim = tower.get(leastSimIndex);
		return pickLeastSim;
	}

	// main
	public Dataset[] executeClustering(Dataset data) {
		if (data.size() == 0) {
			throw new RuntimeException("The dataset should not be empty");
		}
		// add one instances to one tower, add all towers to clusters.
		System.out.println("dataSize: " + data.size());
		
		for (int i = 0; i < data.size(); i++) {
			Vector<Instance> tmpTower = new Vector<Instance>();
			Instance in = data.getInstance(i);
			tmpTower.add(in);
			clusters.add(tmpTower);
		}

		// set alfa to random value between 0 and 1.
		alfa = rg.nextDouble();
		System.out.println("alfa: " + alfa);
		// set initial parameters
		int instanceLength = data.getInstance(0).size();
		actMoves = 0;
		failMovesDrop = 0;
		failMovesPick = 0;
		failMovesGlobal = 0;
		tower.clear();
		maxDis = maxDis(data);


		// first, pick instance from a random tower
		randomTower = rg.nextInt(clusters.size());
		tower = clusters.get(randomTower);
		carried = tower.get(0);
		tower.remove(0);
		if (tower.size() == 0) {
			clusters.remove(randomTower);
		}

		// main loop
		int j = 0;
		int stopSign = 0;
		while (j < iterations && stopSign ==0) {
			j++;
			System.out.println("---------------------iterations: " + j);
			// move to random tower with carried instance
			while (carried != null  && stopSign ==0) {
				// if number of moves reaches 100, recalculate alfa.
				if (actMoves >= 100) {
					alfa = alfa(alfa, failMovesGlobal, actMoves);
					actMoves = 0;
					failMovesGlobal = 0;
				}
				randomTower = rg.nextInt(clusters.size());
				// get random tower
				tower = clusters.get(randomTower);
				actMoves++;
				nFunction = nFunction(alfa, maxDis, carried, tower);
				// calculate probability to drop instance
				probDrop = probDrop(carried, nFunction);
				// generate probability value
				randomProb = rg.nextDouble();
				System.out.println("randomProb: " + randomProb);
				// drop instance if random prob > probDrop
				if (randomProb <= probDrop) {
					tower.add(carried);
					carried = null;
					failMovesDrop = 0;
					System.out.println("succesfull drop");
				} else {
					System.out.println("failed to drop");
					failMovesDrop++;
					failMovesGlobal++;

				}
				if (failMovesDrop >= maxFailMoves) {
					System.out.println("failMoves >maxFailMoves, put instance in new tower");
					Vector<Instance> newTower = new Vector<Instance>();
					newTower.add(carried);
					clusters.add(newTower);
					failMovesDrop = 0;
					carried = null;
				}
			}
			
			// System.out.println("-------instance dropped, carried: "+carried);
			// move to other random tower when no instance carried
			while (carried == null  && stopSign ==0) {
				System.out.println("now try to pick new instance");
				// if number of moves reaches 100, recalculate alfa.
				if (actMoves >= 100) {
					// System.out.println("old alfa: " + alfa);
					alfa = alfa(alfa, failMovesGlobal, actMoves);
					// System.out.println("new alfa: " + alfa);
					actMoves = 0;
					failMovesGlobal = 0;
				}
				randomTower = rg.nextInt(clusters.size());
				tower = clusters.get(randomTower);
				System.out.println("new tower selected + "+tower);
				actMoves++;
				// System.out.println("towerSize:" + tower.size());
				if (tower.size() == 1 || tower.size() == 2) {
					carried = tower.get(0);
					System.out.println("carried: "+carried);
					System.out.println("succesfull pick");
					clusters.remove(randomTower);
					failMovesPick = 0;
					System.out.println("failMovesPick: "+failMovesPick);
				} else {
					System.out.println("towerSize:" + tower.size());
					Instance mean = mean(tower, instanceLength);
					System.out.println("tower mean:" + mean);
					double averageSim = averageSim(tower,instanceLength);
					System.out.println("averageSim: "+averageSim);
					Instance leastSim = leastSim(tower,mean, averageSim);
					System.out.println("leastSim: "+leastSim);
					int indexLS = tower.indexOf(leastSim);
					System.out.println("indexLS: "+indexLS);
					if ( leastSim != null){
						carried = leastSim;
						tower.remove(indexLS);
						System.out.println("carried: "+carried);
						System.out.println("succesfull pick");
					}
					else{
						System.out.println("failed to pick");
						failMovesPick++;
						failMovesGlobal++;
					}
					if (failMovesPick >= maxFailMoves*4) {
						System.out.println("failMovesPick: "+failMovesPick);
						System.out.println("failMovesPick >maxFailMoves, stop algorithm");
						stopSign =1;
					}
						
					/**leastSim = pickLeastSim(tower);
					int indexLS = tower.indexOf(leastSim);
					nFunction = nFunction(alfa, maxDis, leastSim, tower);
					probPick = probPick(leastSim, nFunction);
					randomProb = rg.nextDouble();
					// System.out.println("nFunction to pick: " + nFunction);
					// System.out.println("prob to pick: " + probPick);
					// pick instance if random prob <= probPick
					if (randomProb <= probPick) {
						// System.out.println("indexLS: " + indexLS);
						carried = leastSim;
						System.out.println("carried: "+carried);
						System.out.println("succesfull pick");
						tower.remove(indexLS);
						failMovesPick = 0;
						System.out.println("failMovesPick: "+failMovesPick);
					}
					else{
						System.out.println("failed to pick");
						failMovesPick++;
						failMovesGlobal++;
					}
					if (failMovesPick >= maxFailMoves*2) {
						System.out.println("failMovesPick: "+failMovesPick);
						System.out.println("failMovesPick >maxFailMoves, stop algorithm");
						stopSign =1;
					}
					/*
					 * if (randomProb > probPick) { failMovesGlobal++; }
					 */
				
				}
			}
			// System.out.println("-------instance picked, carried: " +carried);
		}
		Dataset[] output = new Dataset[clusters.size()];
		System.out.println("clusters.size()"+clusters.size());
		for (int i = 0; i < clusters.size(); i++) {
			output[i] = new SimpleDataset();
			Vector<Instance> getCluster = new Vector<Instance>();
			getCluster = clusters.get(i);
			System.out.println("cluster size: "+getCluster.size());
			for (int k = 0; k < getCluster.size(); k++) {
				output[i].addInstance(getCluster.get(k));
			}
		}
		return output;
	}

}
