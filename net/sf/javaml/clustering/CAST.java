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
	 * calculates affinity of an instance x for a currentCluster, as summation
	 * of all similarity values of x in respect to all instances y of
	 * currentCluster.
	 * 
	 * @param Instance
	 *            x
	 * @param Vector
	 *            <Instance> currentCluster
	 * @param Dataset
	 *            data
	 * @param double
	 *            [][]similarityMatrix
	 * 
	 * @return double affinity
	 * 
	 */
	public double affinity(Instance x, Vector<Instance> currentCluster,
			Dataset data, double[][] similarityMatrix) {
		double affinity = 0;
		for (int i = 0; i < currentCluster.size(); i++) {
			Instance y = currentCluster.get(i);
			affinity += similarityMatrix[data.getIndex(x)][data.getIndex(y)];
		}
		return affinity;
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
        
        while (all != null){
        	// open new cluster, add random instance as seed
        	currentCluster = new Vector<Instance>();
        	int randomInstance = rg.nextInt(data.size());
        	currentCluster.add(data.getInstance(randomInstance));
        	all.remove(data.getInstance(randomInstance));
        	
        	while(){
        		// add element U with max affinity to open cluster
        		double maxAffinity=affinityThreshold*currentCluster.size();
        		int indexU;
        		for (int i = 0; i<all.size();i++){
        			Instance u = all.elementAt(i);
        			double affinityU = affinity(u,currentCluster, data, simMatrix);
        			if (affinityU >maxAffinity){
        				maxAffinity = affinityU;
        				indexU = i;
        			}
        		}
        		Instance U = all.get(indexU);
        		currentCluster.add(U);
        		all.remove(U);
        		
        		
        		
        	}
        	
        	
        	// remove elements from open cluster
        	// close cluster when stabl
        	// optimization of the clusters	
        	
        }
        
//      write results to output
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
