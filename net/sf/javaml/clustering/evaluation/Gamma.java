/**
 * GammaSCore.java, 1-dec-06
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


package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.DistanceMeasureFactory;
/**
 * TODO complete code
 * 
 * @author Andreas De Rijcke
 *
 */
public class Gamma implements ClusterEvaluation{
	public double score(Clusterer c, Dataset data){
		Dataset[] datas = new Dataset[c.getNumberOfClusters()];
		for (int i = 0; i < c.getNumberOfClusters(); i++) {
			datas[i] = new SimpleDataset();
		}
		for (int i = 0; i < data.size(); i++) {
			Instance in = data.getInstance(i);
			datas[c.predictCluster(in)].addInstance(in);
		}
		// search for min inter cluster distance
		double minInterClustDist= Double.MAX_VALUE;
		for (int i=0; i<c.getNumberOfClusters(); i++){
			for (int j=0; j< datas[i].size();j++){
				Instance x = datas[i].getInstance(j);
				int nextClusterSize;
				int nextClusterIndex;
				if ( i == (c.getNumberOfClusters()-1)){
					nextClusterSize = datas[0].size();
					nextClusterIndex = 0;
				}else{
					nextClusterSize = datas[i+1].size();
					nextClusterIndex = i+1;
				}
				for (int k=0; k< nextClusterSize ;k++){
					Instance y = datas[nextClusterIndex].getInstance(k);
					DistanceMeasure dm = DistanceMeasureFactory.getEuclideanDistanceMeasure();
					double distance = dm.calculateDistance(x, y);
					if ( distance<minInterClustDist){
						minInterClustDist = distance;
					}
				}
			}		
		}

		return gamma;
	}

    
    public boolean compareScore(double score1, double score2);
    return;
}
