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

public class Ant implements Clusterer {
	/**
	 * 
	 * This class implements an Ant Based Clustering Algorithm based on some
	 * ideas from papers by Handl et al. and Schockaert et al.
	 * 
	 * The distance measure should be normalized in the interval [0,1].
	 * 
	 * @param iterations
	 *            number of iterations of main loop
	 * @param maxFailMovesDrop
	 *            maximum number of moves 'ants' are allowed to make, when
	 *            failing to drop a carried instance/heap on an existing heap.
	 *            when maxFailMovesDrop is reached, the carried instance/heap
	 *            will be dropped in a new heap.
	 * 
	 * @param maxFailMovesPick
	 *            maximum number of moves 'ants' are allowed to make, when
	 *            failing to pick a carried instance/heap from an existing heap.
	 *            when the heaps become more homogeneous, the probability to add
	 *            an heap will drop to 0 when the clusters are found and nothing will change anymore.
	 *            this parameter needs to be higher for bigger datasets.
	 *            when maxFailMovesPick is reached, the algorithm will stop.
	 * 
	 * @author Andreas De Rijcke
	 * 
	 */
	public Ant(int iterations, int maxFailMovesDrop, int maxFailMovesPick) {
		this.iterations = iterations;
		this.maxFailMovesDrop = maxFailMovesDrop;
		this.maxFailMovesPick = maxFailMovesPick;
	}

	/*
	 * schockaert's method
	 * 
	 */

	Vector<Instance> heap = new Vector<Instance>();

	Vector<Instance> heap1 = new Vector<Instance>();

	Vector<Instance> carried = new Vector<Instance>();

	Vector<Vector<Instance>> clusters = new Vector<Vector<Instance>>();

	private Random rg = new Random(System.currentTimeMillis());

	private DistanceMeasure dm = new EuclideanDistance();

	private int randomHeap, iterations, instanceLength;

	private double maxDist, randomProb;

	private double failMovesDrop, failMovesPick, failMovesGlobal,
			maxFailMovesDrop, maxFailMovesPick;

	// tuning parameters with standard values
	private double m1 = 5.0, m2 = 5.0;

	private double teta1 = 0.5, teta2 = 0.5, teta3 = 0.5;

	private double n1 = 10 /* for instance drop */, n2 = 20; /* for heap drop */

	// matrix representation of fig 4.1. [i][j], i = row, j = row element
	double[][] similarityRanges = { { 0, 0.125, 0.375, 0.625, 0.875, 1 },
			{ 4.0, 3.0, 2.0, 1.0, 0.0, 0.0 } };

	// matrix representation of table 4.1, values calculated on the basis of fig
	// 4.2.
	double[][] stimPickI = { { 0.5625, 0.0, 0.0, 0.0, 0.0 },
			{ 0.8125, 0.6875, 0.0, 0.0, 0.0 }, { 1, 0.9375, 0.8125, 0.0, 0.0 },
			{ 1.0, 1.0, 1.0, 0.9375, 0.0 }, { 1.0, 1.0, 1.0, 1.0, 1.0 }, };

	// matrix representation of table 4.2, values calculated on the basis of fig
	// 4.2.
	double[][] stimPickH = { { 1.0, 0.0, 0.0, 0.0, 0.0 },
			{ 0.5625, 0.9375, 0.0, 0.0, 0.0 },
			{ 0.3125, 0.4375, 0.8125, 0.0, 0.0 },
			{ 0.0625, 0.1875, 0.3125, 0.6875, 0.0 },
			{ 0.0625, 0.0625, 0.0625, 0.1875, 0.5625 }, };

	// matrix representation of table 4.3, values calculated on the basis of fig
	// 4.2.
	double[][] stimDrop = { { 0.6875, 0.8125, 0.9375, 1.0, 1.0 },
			{ 0.3125, 0.6875, 0.8125, 0.9375, 1.0 },
			{ 0.0625, 0.3125, 0.6875, 0.8125, 0.9375 },
			{ 0.0625, 0.0625, 0.3125, 0.6875, 0.8125 },
			{ 0.0625, 0.0625, 0.0625, 0.3125, 0.6875 }, };

	// methods
	// calculates max distance between all instances in dataset
	public double maxDist(Dataset data) {
		Instance min = data.getMinimumInstance();
		Instance max = data.getMaximumInstance();
		double maxDist = dm.calculateDistance(min, max);
		return maxDist;
	}

	/**
	 * calculates mean instance of a heap H
	 * 
	 * @param Vector
	 *            <Instance> heap
	 * @return mean instance
	 */
	public Instance meanH(Vector<Instance> heap) {
		Instance in;
		float[] sumVector = new float[instanceLength];
		for (int i = 0; i < heap.size(); i++) {
			in = heap.get(i);
			for (int j = 0; j < instanceLength; j++) {
				sumVector[j] += in.getValue(j);
			}
		}
		for (int j = 0; j < instanceLength; j++) {
			sumVector[j] /= heap.size();
		}
		Instance meanH = new SimpleInstance(sumVector);
		return meanH;
	}

	/**
	 * calculates similarity E between 2 instances [0,1](100% sim: E=1, 0% sim:
	 * E=0)
	 * 
	 * @param Instance,Instance
	 * @return double similarity
	 */
	public double similarity(Instance instance1, Instance instance2) {
		double similarity = 1 - (dm.calculateDistance(instance1, instance2) / maxDist);
		return similarity;
	}

	/**
	 * finds minimal similarity in heap in respect to heap centrum [0,1]
	 * 
	 * @param Vector
	 *            <Instance> heap
	 * @return double minSimilar
	 */
	public double minSimilarity(Vector<Instance> heap) {
		Instance meanH = meanH(heap);
		double minSimilar = similarity(heap.get(0), meanH);
		for (int i = 1; i < heap.size(); i++) {
			double sim = similarity(heap.get(i), meanH);
			minSimilar = Math.min(minSimilar, sim);
		}
		return minSimilar;
	}

	/**
	 * find least similar instance in heap (has lowest similarity value) [0,1]
	 * 
	 * @param Vector
	 *            <Instance> heap
	 * 
	 * @return index of least similar instance
	 */
	public int leastSim(Vector<Instance> heap) {
		Instance meanH = meanH(heap);
		double sim1 = similarity(heap.get(0), meanH);
		int index = 0;
		for (int i = 1; i < heap.size(); i++) {
			Instance x = heap.get(i);
			double sim2 = similarity(x, meanH);
			if (sim2 > sim1) {
				sim1 = sim2;
				index = heap.indexOf(x);
			}
		}
		return index;
	}

	/**
	 * calculates average similarity of heap H [0,1]
	 * 
	 * @param Vector
	 *            <Instance> heap
	 * @return double average similarity of heap
	 */
	public double avg(Vector<Instance> heap) {
		double avg = 0;
		Instance meanH = meanH(heap);
		// average similatiry of the heap
		for (int i = 0; i < heap.size(); i++) {
			avg += similarity(heap.get(i), meanH);
		}
		avg /= heap.size();
		return avg;
	}

	/**
	 * calculates Lukasiewicz t-norm of avg(carried heap L) and the similarity
	 * between clustercentra heap L and heap H.
	 * 
	 * @param Vector
	 *            <Instance> heap carried, Vector<Instance> heap
	 * @return double tW
	 */
	public double tW(Vector<Instance> heapCarried, Vector<Instance> heapH) {
		Instance meanCarried = meanH(heapCarried);
		Instance meanH = meanH(heapH);
		double similarity = similarity(meanCarried, meanH);
		double avgCarried = avg(heapCarried);
		double tW = Math.max(0, similarity + avgCarried - 1);
		return tW;
	}

	/**
	 * finds stimulus value for drop/pick action
	 * 
	 * @param double
	 *            avg(heap H)
	 * @param double
	 *            for pick: minSimilar(heap H); for drop: tW
	 * @param int
	 *            i: for pick instance = 1, for pick heap = 2, for drop = 3
	 * @return double stimulus
	 */
	public double stimulus(double param1, double param2, int i) {
		int indexParam1 = 0;
		int indexParam2 = 0;
		for (int j = 0; j < 6; j++) {
			if (param1 > similarityRanges[0][j]
					&& param1 < similarityRanges[0][j + 1]) {
				indexParam1 = (int) similarityRanges[1][j];
			}
			if (param2 > similarityRanges[0][j]
					&& param2 < similarityRanges[0][j + 1]) {
				indexParam2 = (int) similarityRanges[1][j];
			}
		}
		double stimulus = 0;
		if (i == 1) {
			stimulus = stimPickI[indexParam2][indexParam1];
		} else if (i == 2) {
			stimulus = stimPickH[indexParam2][indexParam1];
		} else if (i == 3) {
			stimulus = stimDrop[indexParam2][indexParam1];
		} else {
			System.out.println("failure: no stimulus calculated!!!");
		}
		return stimulus;
	}

	/**
	 * probability to pick 1 instance from heap H or whole heap [0,1]
	 * 
	 * @param stimI =
	 *            stimulus to pick 1 instance.
	 * @param stimH =
	 *            stimulus to pick the complete heap.
	 * @param int
	 *            i: for pick instance = 1, for pick heap.
	 * 
	 * @return double probability to pick
	 */
	public double probPick(double stimI, double stimH, int i) {
		double probPick = 0;
		if (i == 1) {
			probPick = (stimI / (stimI + stimH))
					* (Math.pow(stimI, m1) / (Math.pow(teta1, m1) + Math.pow(
							stimI, m1)));
		} else if (i == 2) {
			probPick = (stimH / (stimI + stimH))
					* (Math.pow(stimH, m2) / (Math.pow(teta2, m2) + Math.pow(
							stimH, m2)));
		}
		return probPick;
	}

	// probability to drop instance/heap other instance/heap [0,1]
	/**
	 * probability to drop instance/heap [0,1]
	 * 
	 * @param stim =
	 *            stimulus to drop.
	 * @param n:
	 *            for instance standard 10, for heap 20
	 * @return double probability to drop
	 */
	public double probDrop(double stim, double n) {
		double probDrop = Math.pow(stim, n)
				/ (Math.pow(teta3, n) + Math.pow(stim, n));
		return probDrop;
	}

	// main
	public Dataset[] executeClustering(Dataset data) {
		if (data.size() == 0) {
			throw new RuntimeException("The dataset should not be empty");
		}
		// add one instances to one heap, add all heap to clusters.
		for (int i = 0; i < data.size(); i++) {
			Vector<Instance> tmpHeap = new Vector<Instance>();
			Instance in = data.getInstance(i);
			tmpHeap.add(in);
			clusters.add(tmpHeap);
		}
		// set initial parameters
		instanceLength = data.getInstance(0).size();
		maxDist = maxDist(data);
		failMovesDrop = 0;
		failMovesPick = 0;
		failMovesGlobal = 0;
		heap.clear();
		// first, load ant with instance from a random heap and remove instance/
		// heap from clusters
		randomHeap = rg.nextInt(clusters.size());
		heap = clusters.get(randomHeap);
		carried.add(heap.get(0));
		clusters.remove(heap);
		heap.clear();

		// main algorithm
		int j = 0, stopSign = 0;
		while (j < iterations && stopSign == 0) {
			j++;
			System.out.println("-------iterations: " + j + "-------");
			// drop instance / heap
			System.out
					.println("::MAIN:: try to drop instance / heap, carried size: "
							+ carried.size());
			while (carried != null && stopSign == 0) {
				double probDropC = 0;
				// move ant to random heap with carried instance
				randomHeap = rg.nextInt(clusters.size());
				heap = clusters.get(randomHeap);
				// calculated drop probability
				if (heap.size() == 1) {
					if (carried.size() == 1) {
						probDropC = Math.pow(similarity(heap.get(0), carried
								.get(0)), 5);
					} else {
						probDropC = 0;
					}
				} else if (heap.size() > 1) {
					double avgHeap = avg(heap);
					double tW = tW(carried, heap);
					double stimToDrop = stimulus(avgHeap, tW, 3);
					if (carried.size() == 1) {
						probDropC = probDrop(stimToDrop, n1);
					} else {
						probDropC = probDrop(stimToDrop, n2);
					}
				} else {
					System.out
							.println("::MAIN:: failure: selected heap is empty!!!");
				}
				// generate random drop probability value.
				randomProb = rg.nextDouble();
				// drop instance if random prob <= probDrop
				if (randomProb <= probDropC) {
					for (int i = 0; i < carried.size(); i++) {
						heap.add(carried.get(i));
					}
					System.out.println("::MAIN:: succesfull drop");
					failMovesDrop = 0;
					carried = null;
				} else {
					System.out.println("::MAIN:: failed to drop");
					failMovesDrop++;
					failMovesGlobal++;
				}
				if (failMovesDrop >= maxFailMovesDrop) {
					System.out
							.println("::MAIN:: failMoves>maxFailMoves, put carried in new heap");
					Vector<Instance> newHeap = new Vector<Instance>();
					for (int i = 0; i < carried.size(); i++) {
						newHeap.add(carried.get(i));
					}
					clusters.add(newHeap);
					failMovesDrop = 0;
					carried = null;
				}
			}

			// pick instance/heap
			System.out.println("::MAIN:: try to pick new instance / heap");
			while (carried == null && stopSign == 0) {
				// move to other random heap
				randomHeap = rg.nextInt(clusters.size());
				heap = clusters.get(randomHeap);
				if (heap.size() == 1) {
					// pick instance from heap and remove heap from clusters
					Vector<Instance> tmp = new Vector<Instance>();
					tmp.add(heap.firstElement());
					carried = tmp;
					clusters.remove(heap);
					heap.clear();
					failMovesPick = 0;
					System.out.println("::MAIN:: succesfull pick instance");
				} else if (heap.size() == 2) {
					// pick 1 instance and remove from heap
					Vector<Instance> tmp = new Vector<Instance>();
					tmp.add(heap.firstElement());
					carried = tmp;
					// other option is to make random choice between instance 1
					// or 2.
					heap.remove(tmp.elementAt(0));
					failMovesPick = 0;
					System.out.println("::MAIN:: succesfull pick instance");
				} else if (heap.size() > 2) {
					// calculate stimulus and pick probability for picking 1
					// instance or heap
					double averageSim = avg(heap);
					double minSim = minSimilarity(heap);
					double stimI = stimulus(averageSim, minSim, 1);
					double stimH = stimulus(averageSim, minSim, 2);
					double probPickI = probPick(stimI, stimH, 1);
					double probPickH = probPick(stimI, stimH, 2);
					randomProb = rg.nextDouble();
					// for highest probability, generate random probability
					// value.
					if (probPickI > probPickH) {
						// pick instance if randomProb <= propPick
						if (randomProb <= probPickI) {
							// pick least similar instance and remove from heap
							int indexLeastSimInstance = leastSim(heap);
							Vector<Instance> tmp = new Vector<Instance>();
							tmp.add(heap.get(indexLeastSimInstance));
							carried = tmp;
							heap.remove(indexLeastSimInstance);
							System.out
									.println("::MAIN:: succesfull pick instance");
						} else {
							System.out.println("::MAIN:: failed to pick");
							failMovesPick++;
							failMovesGlobal++;
						}
					} else if (probPickI < probPickH) {
						// pick instance if randomProb <= propPick
						if (randomProb <= probPickH) {
							// pick heap and remove from clusters
							Vector<Instance> tmp = new Vector<Instance>();
							tmp.addAll(heap);
							carried = tmp;
							clusters.remove(heap);
							heap.clear();
							System.out.println("::MAIN:: succesfull pick heap");
						} else {
							System.out.println("::MAIN:: failed to pick");
							failMovesPick++;
							failMovesGlobal++;
						}
					}
					// if fail moves to pick grows to high, stop algorithm
					if (failMovesPick >= maxFailMovesPick) {
						System.out
								.println("::MAIN:: failMovesPick >maxFailMovesPick, stop algorithm");
						stopSign = 1;
					}
				}
			}
		}
		Dataset[] output = new Dataset[clusters.size()];
		System.out.println("::MAIN:: clusters.size()" + clusters.size());
		for (int i = 0; i < clusters.size(); i++) {
			output[i] = new SimpleDataset();
			Vector<Instance> getCluster = new Vector<Instance>();
			getCluster = clusters.get(i);
			System.out.println("::MAIN:: cluster size: " + getCluster.size());
			for (int k = 0; k < getCluster.size(); k++) {
				output[i].addInstance(getCluster.get(k));
			}
		}
		return output;
	}
}
