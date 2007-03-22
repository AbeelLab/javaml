/**
 * CAST.java
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
 * Copyright (c) 2006-2007, Andreas De Rijcke
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.clustering;

import java.util.Random;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.NormalizedEuclideanSimilarity;

public class CAST {

	/**
	 * XXX add docu
	 * 
	 * @param affinityThreshold
	 */
	public CAST(int affinityThreshold) {
		this.affinityThreshold = affinityThreshold;
	}

	/**
	 * XXX add doc
	 */
	private int affinityThreshold;

	/**
	 * XXX add doc
	 */
	private double[][] simMatrix;

	/**
	 * current cluster being build, contains instances assigned to this cluster
	 */
	private Vector<Instance> currentCluster;

	/**
	 * vector containing final clusters
	 */
	private Vector<Vector<Instance>> clusters = new Vector<Vector<Instance>>();

	/**
	 * XXX add doc
	 */
	private DistanceMeasure dm;

	/**
	 * XXX add doc
	 */
	private Random rg = new Random(System.currentTimeMillis());

	/**
	 * builds symmetric similarity matrix S(i,j) for all instances in the
	 * dataset in respect to eachother with values i,j between [0,1]. 100%
	 * similarity = 1, 0% = 0.
	 * 
	 * @param Dataset
	 *            data
	 * 
	 * @return double[][] similarityMatrix
	 */
	public double[][] similarityMatrix(Dataset data) {
		double[][] similarityMatrix = new double[data.size()][data.size()];
		for (int i = 0; i < data.size(); i++) {
			for (int j = 0; j < data.size(); j++) {
				this.dm = new NormalizedEuclideanSimilarity(data);
				double similarity = dm.calculateDistance(data.getInstance(i),
						data.getInstance(j));
				if (i == j) {
					similarityMatrix[i][i] = similarity;
				} else {
					similarityMatrix[i][j] = similarity;
					similarityMatrix[j][i] = similarity;
				}
			}
		}
		return similarityMatrix;
	}

	/**
	 * 
	 * @param Vector
	 *            <Instance> currentCluster
	 * @param Dataset
	 *            data
	 * @param double
	 *            [][]similarityMatrix
	 * 
	 * @return Vector double affinity
	 * 
	 */
	public Vector<Double> affinityVector(Vector<Instance> currentCluster,
			Vector<Instance> all, Dataset data, double[][] similarityMatrix) {
		Vector<Double> affinityVector = new Vector<Double>();
		for (int i = 0; i < all.size(); i++) {
			double affinity = 0;
			Instance x = all.get(i);
			for (int j = 0; j < currentCluster.size(); j++) {
				Instance y = currentCluster.get(i);
				affinity += similarityMatrix[data.getIndex(x)][data.getIndex(y)];
			}
			affinityVector.add(affinity);
		}
		return affinityVector;
	}

	/**
	 * 
	 * @return index of max affinity value
	 */
	public int indexMaxAffinity(Vector<Double> affinityVector) {
		int indexMaxAffinity = 0;
		double maxAffinity = 0;
		for (int i = 0; i < affinityVector.size(); i++) {
			double affinity = affinityVector.elementAt(i);
			if (affinity > maxAffinity) {
				maxAffinity = affinity;
				indexMaxAffinity = i;
			}
		}
		return indexMaxAffinity;
	}

	/**
	 * 
	 * main XXX add doc
	 */
	public Dataset[] executeClustering(Dataset data) {
		if (data.size() == 0) {
			throw new RuntimeException("The dataset should not be empty");
		}

		// build similarity matrix
		simMatrix = similarityMatrix(data);
		// build temporarily processing vector containig all data
		Vector<Instance> all = new Vector<Instance>();
		for (int i = 0; i < data.size(); i++) {
			Instance in = data.getInstance(i);
			all.add(in);
		}

		while (all != null) {
			// open new cluster, add random instance as seed
			currentCluster = new Vector<Instance>();
			int randomInstance = rg.nextInt(all.size());
			currentCluster.add(all.get(randomInstance));
			all.remove(all.get(randomInstance));

			// create affinity vector of All in respect to open cluster
			Vector<Double> affVectorAll = new Vector<Double>();
			affVectorAll = affinityVector(currentCluster, all, data, simMatrix);
			// find max affinity index in affVector
			int maxAffIndex = indexMaxAffinity(affVectorAll);
			// get max affinity
			double maxAff = affVectorAll.get(maxAffIndex);

			// create affinity vector of current cluster
			Vector<Double> affVectorCluster = new Vector<Double>();
			affVectorCluster = affinityVector(currentCluster, currentCluster,
					data, simMatrix);

			while (maxAff >= (affinityThreshold * currentCluster.size())) {
				// add element U with max affinity to open cluster and remove
				// from all.
				Instance U = all.get(maxAffIndex);
				currentCluster.add(U);
				affVectorCluster.add(affVectorAll.get(maxAffIndex));
				all.remove(U);
				affVectorAll.remove(maxAffIndex);
				
				// update affinity values in affinity vector of All
				Vector<Double> tmp = new Vector<Double>();
				for (int i = 0; i < affVectorAll.size(); i++) {
					Instance x = all.get(i);
					double newAff = affVectorAll.get(i);
					newAff += simMatrix[data.getIndex(U)][data.getIndex(x)];
					tmp.add(newAff);
				}
				affVectorAll = tmp;
				// update affinity vector of currentCluster
				Vector<Double> tmp2 = new Vector<Double>();
				for (int i = 0; i < affVectorCluster.size(); i++) {
					Instance x = currentCluster.get(i);
					double newAff = affVectorCluster.get(i);
					newAff += simMatrix[data.getIndex(U)][data.getIndex(x)];
					tmp2.add(newAff);
				}
				affVectorCluster = tmp2;
				
			}

			// remove elements from open cluster
			// close cluster when stabl
			// optimization of the clusters

		}

		// write results to output
		Dataset[] output = new Dataset[clusters.size()];
		for (int i = 0; i < clusters.size(); i++) {
			output[i] = new SimpleDataset();
			Vector<Instance> getCluster = new Vector<Instance>();
			getCluster = clusters.get(i);
			for (int j = 0; j < getCluster.size(); j++) {
				output[i].addInstance(getCluster.get(j));
			}
		}
		return output;
	}
}
