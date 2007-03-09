/**
 * TraceScatterMatrix.java
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
package net.sf.javaml.clustering.evaluation;

import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;

/**
 * E_1 from the Zhao 2001 paper
 * 
 * Distance measure has to be CosineSimilarity TODO uitleg
 * 
 * @author Andreas De Rijcke
 */

public class TraceScatterMatrix implements ClusterEvaluation {
	public TraceScatterMatrix(DistanceMeasure dm) {
		this.dm = new CosineSimilarity();
	}

	private DistanceMeasure dm = new CosineSimilarity();

	public Instance mean(Dataset cluster, int instanceLength) {
		Instance in;
		float[] sumVector = new float[instanceLength];
		for (int i = 0; i < cluster.size(); i++) {
			in = cluster.getInstance(i);
			for (int j = 0; j < instanceLength; j++) {
				sumVector[j] += in.getValue(j);
			}
		}
		for (int j = 0; j < instanceLength; j++) {
			sumVector[j] /= cluster.size();
		}
		Instance mean = new SimpleInstance(sumVector);
		return mean;
	}

	public double score(Dataset[] clusters) {
		Instance clusterCentroid;
		Instance overAllCentroid;
		Vector<Instance> clusterCentroids =new Vector<Instance>();
		Vector<Integer> clusterSizes =new Vector<Integer>();
		// TODO check why trouble here: array index out of range
		int instanceLength = clusters[0].getInstance(0).size();
        
        // calculate centroids of each cluster
        for (int i =0; i<clusters.length; i++){
        	clusterCentroid = mean(clusters[i], instanceLength);
        	clusterCentroids.add(clusterCentroid);
        	clusterSizes.add(clusters[i].size());
        }
        
        // calculate centroid all instances
        // firs put all cluster back together
        Dataset data = new SimpleDataset();
        for (int i =0; i<clusters.length; i++){
        	for(int j = 0; j < clusters[i].size(); j++) {
        		data.addInstance(clusters[i].getInstance(j));  
        	}
        }
        overAllCentroid = mean(data, instanceLength);    
        
        // calculate trace of the between-cluster scatter matrix.
        double sum = 0;
        for (int i = 0; i < clusterCentroids.size(); i++) {
            double cos = dm.calculateDistance(clusterCentroids.get(i), overAllCentroid);
            sum += cos * clusterSizes.get(i);
        }
        return sum;
    }

	public boolean compareScore(double score1, double score2) {
		// should be minimized
		return score2 < score1;
	}
}
