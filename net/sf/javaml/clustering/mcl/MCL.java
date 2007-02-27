/**
 * MCL.java, 22-nov-2006
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
 * Copyright (c) 2006, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering.mcl;

import java.util.Vector;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

// TODO implements bridge between Gregory's implementation and the interfaces.
public class MCL implements Clusterer {

	public Dataset[] executeClustering(Dataset data) {
		// TODO convert dataset to matrix of distances
		MarkovClustering mcl = new MarkovClustering();
		SparseMatrix matrix = mcl.run(dataConverted, maxResidual, pGamma,
				loopGain, maxZero);
		
		// convert matrix to output dataset:
		
		Vector<double[]> attractors = new Vector<double[]>();
		// find attractors in diagonal
		for (int i = 0; i < data.size(); i++) {
			// attractor[0][1]: [0] = index in dataset/matrix, [1]= value in
			// matrix.
			double[] attractor = new double[2];
			double val = matrix.get(i, i);
			if (val != 0) {
				attractor[0] = i;
				attractor[1] = val;
				attractors.add(attractor);
			}
		}
		// search for the different attractors values, their number is resulting
		// number of clusters.
		Vector<Double> attractorvalues = new Vector<Double>();
		double temp = attractors.get(0)[1];
		attractorvalues.add(temp);
		for (int i = 1; i < attractors.size(); i++) {
			double temp2 = attractors.get(i)[1];
			for (int j = 0; j < attractorvalues.size(); j++) {
				double temp3 = attractors.get(j)[1];
				if (temp2 != temp3) {
					attractorvalues.add(temp3);
				}
			}
		}
		// put index of attractors with same value together. = attractorsystem
		Vector<Double> attractorIndex = new Vector<Double>();
		Vector<Vector<Double>> attractorSystems = new Vector<Vector<Double>>();
		for (int i =0; i< attractorvalues.size();i++){
			for (int j = 0; j < attractors.size();j++ ){
				if ( attractors.get(j)[1]== attractorvalues.get(i)){
					attractorIndex.add(attractors.get(j)[1]);
				}
			}
			attractorSystems.add(attractorIndex);
		}
		// cluster consist of an attractorsystem and the data they attract.
		// for each attractorsystem, find attracted data and add to cluster
		Vector<Instance> cluster= new Vector<Instance>();
		Vector<Vector<Instance>> finalClusters = new Vector<Vector<Instance>>();
		// for each attractorsystem
		for (int i =0; i< attractorSystems.size();i++){
			// get columnindexes
			attractorIndex = attractorSystems.get(i);
			// for each column
			for (int j = 0; j < attractorIndex.size();j++ ){
				int value = attractorIndex.get(j).intValue();
				// add attractor to cluster
				cluster.add(data.getInstance(value));
				// find data attracted to attractor (when matrix value is not 0)
				// and add to cluster if not already present
				for (int k = 0; k < data.size(); k++){
					if( matrix.get(value,k) != 0){
						for(int l = 0; l<cluster.size();l++){
							if(data.getInstance(k) !=  cluster.get(j)){
								cluster.add(data.getInstance(k));
							}		
						}
						
					}
				}	
			}
			finalClusters.add(cluster);
		}
		// convert cluster
		Dataset[] output = new Dataset[finalClusters.size()];
		for (int i = 0; i < finalClusters.size(); i++) {
			Vector<Instance> getCluster = new Vector<Instance>();
			getCluster = finalClusters.get(i);
			for (int j = 0; j < getCluster.size(); j++) {
				output[i].addInstance(getCluster.get(j));
			}
		}
		return output;
	}
}
