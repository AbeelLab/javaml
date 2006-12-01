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
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

public class Ant implements Clusterer {
	Vector<Instance> tower = new Vector<Instance>();

	Vector<Instance> tower1 = new Vector<Instance>();

	Vector<Vector<Instance>> clusters = new Vector<Vector<Instance>>();

	private Instance leastSim;

	private Instance carried;

	private Instance[] centroids;

	private Random rg = new Random(System.currentTimeMillis());

	private int numberOfClusters;

	private int iterations;

	private double maxDis;

	private double actMoves;

	private double failMoves;

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

	// methode: search for least similar instance in tower
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
			double delta = dm.calculateDistance(x, tmp);
			delta /= maxDis;
			nFunction += 1 - (delta / alfa);
		}
		nFunction = Math.max(nFunction, 0);
		//double towerSize = tower.size();
		// System.out.println("towerSize: " + towerSize);
		// nFunction /= towerSize;
		return nFunction;
	}

	// methode: calculate probility for picking-up an instance
	public double probPick(Instance instance, double nFunction) {
		double kPlus = 0.1;
		double probPick;
		/**
		 * if (nFunction<=1){ probPick=1.0; } else{ probPick = 1 /
		 * (nFunction*nFunction); }
		 */
		probPick = (kPlus / (kPlus + nFunction))
				* (kPlus / (kPlus + nFunction));
		return probPick;
	}

	// methode: calculate probility for dropping an instance
	public double probDrop(Instance instance, double nFunction) {
		double kMin = 0.3;
		double probDrop;
		/**
		 * if (nFunction>=1){ probDrop=1.0; } else{ probDrop =
		 * nFunction*nFunction*nFunction*nFunction; }
		 */
		probDrop = (nFunction / (kMin + nFunction))
				* (nFunction / (kMin + nFunction));
		return probDrop;
	}

	// main
	public void buildClusterer(Dataset data) {
		if (data.size() == 0) {
			throw new RuntimeException("The dataset should not be empty");
		}
		// add all instances to a tower, add all towers to clusters.
		System.out.println("dataSize: " + data.size());
		for (int i = 0; i < data.size(); i++) {
			Vector<Instance> tmpTower = new Vector<Instance>();
			Instance in = data.getInstance(i);
			tmpTower.add(in);
			clusters.add(tmpTower);
		}

		// set initial parameters
		// set alfa to random value between 0 and 1.
		alfa = rg.nextDouble();
		System.out.println("alfa: " + alfa);
		actMoves = 0;
		failMoves = 0;
		failMovesGlobal = 0;
		tower.clear();
		maxDis = maxDis(data);

		// first, pick least similar instance from a random tower
		randomTower = rg.nextInt(clusters.size());
		tower = clusters.get(randomTower);
		carried = tower.get(0);// pickLeastSim(tower);
		tower.remove(0);
		if (tower.size() == 0) {
			clusters.remove(randomTower);
		}

		// main loop
		for (int i = 0; i < iterations; i++) {
			System.out.println("iterations: " + i);
			// move to random tower with carried instance
			while (carried != null) {
				// if number of moves reaches 100, recalculate alfa.
				if (actMoves >= 100) {
					alfa = alfa(alfa, failMovesGlobal, actMoves);
					actMoves = 0;
					failMovesGlobal = 0;
				}
				actMoves++;
				// System.out.println("actMoves while try to drop: " +
				// actMoves);
				randomTower = rg.nextInt(clusters.size());
				tower = clusters.get(randomTower);
				nFunction = nFunction(alfa, maxDis, carried, tower);
				probDrop = probDrop(carried, nFunction);
				randomProb = rg.nextDouble();
				// drop instance if random prob > probDrop
				if (randomProb <= probDrop) {
					tower.add(carried);
					carried = null;
					failMoves = 0;
				} else {
					failMoves++;
					failMovesGlobal++;

				}
				if (failMoves >= maxFailMoves) {
					System.out.println("failMoves >maxFailMoves");
					Vector<Instance> newTower = new Vector<Instance>();
					newTower.add(carried);
					clusters.add(newTower);
					failMoves = 0;
					carried = null;
				}
			}
			//System.out.println("-------instance dropped, carried: "+carried);
			// move to other random tower when no instance carried
			while (carried == null) {
				// if number of moves reaches 100, recalculate alfa.
				if (actMoves >= 100) {
					// System.out.println("old alfa: " + alfa);
					alfa = alfa(alfa, failMovesGlobal, actMoves);
					// System.out.println("new alfa: " + alfa);
					actMoves = 0;
					failMovesGlobal = 0;
				}
				actMoves++;
				// System.out.println("actMoves while try to pick: " +
				// actMoves);
				// System.out.println("ClusterSize voor random:" +
				// clusters.size());
				randomTower = rg.nextInt(clusters.size());
				tower = clusters.get(randomTower);
				// System.out.println("towerSize:" + tower.size());
				if (tower.size() == 1) {
					carried = tower.get(0);
					clusters.remove(randomTower);
				} else {
					leastSim = pickLeastSim(tower);
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
						tower.remove(indexLS);
					}
					if (randomProb > probPick) {
						failMovesGlobal++;
					}
				}
			}
			//System.out.println("-------instance picked, carried: " +carried);
		}
		// calculate centriods of each tower/cluster
		numberOfClusters = clusters.size();
		System.out.println("numberOfClusters: " + numberOfClusters);
		this.centroids = new Instance[numberOfClusters];
		for (int i = 0; i < clusters.size(); i++) {
			tower = clusters.get(i);
			System.out.println("towerSize: " + tower.size());
			int instanceLength = data.getInstance(0).size();
			float sum[] = new float[instanceLength];
			for (int j = 0; j < tower.size(); j++) {
				float tmp[] = tower.get(j).getArrayForm();
				for (int k = 0; k < instanceLength; k++) {
					sum[k] += tmp[k];
				}
			}
			for (int j = 0; j < instanceLength; j++) {
				sum[j] /= tower.size();
			}
			this.centroids[i] = new SimpleInstance(sum);
		}
		// System.out.println("centroids["+i+"]: " + centroids[i]);
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
		for (int i = 0; i < this.numberOfClusters; i++) {
			// System.out.println("INSTANCE:" +instance);
			// System.out.println("CENTROID: " +centroids[i]);
			// System.out.println("DM: "+dm);

			double dist = dm.calculateDistance(centroids[i], instance);
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

	/**
	 * This method is only intended for testing purposes.
	 * 
	 */
	public Instance[] getCentroids() {
		return this.centroids;
	}

}
