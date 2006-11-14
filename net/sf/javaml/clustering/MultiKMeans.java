/**
 * MultiKMeans.java, 10-nov-2006
 *
 * This file is part of the Java Machine Learning API
 * 
 * php-agenda is free software; you can redistribute it and/or modify
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
package net.sf.javaml.clustering;

import net.sf.javaml.clustering.evaluation.CosSim;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
/**
 * This variant of the SimpleKMeans algorithm will run the Simple KMeans
 * algorithm multiple times and will only return the best centroids as the final
 * result.
 * 
 * @author Thomas Abeel, Andreas De Rijcke
 * 
 */


public class MultiKMeans extends SimpleKMeans implements Clusterer {
	private int kMin;
	private int kMax;
	private int bestNumberOfClusters;
	private double bestCosSim;
	private Instance[] bestCentroids;
	
	public MultiKMeans(int kMin, int kMax) {
		this.kMax = kMax;
		this.kMin = kMin;
	}
	
    public void buildClusterer(Dataset data){
    	if (data.size() == 0)
			throw new RuntimeException("The dataset should not be empty");
    	if (kMin == 0)
			throw new RuntimeException("There should be at least one cluster");
    	
    	bestCosSim=0;
    	    	
    	for (int k = kMin; k <= kMax; k++){
    		System.out.println("k = "+k);
    		// Clusterer km=new super(k,100);
    		this.numberOfClusters = k;
    		this.numberOfIterations = 100;
    		super.buildClusterer(data);
    		Dataset[] datas = new Dataset[k];
    		for (int i = 0; i < k; i++) {
    			datas[i] = new SimpleDataset();
    		}
    		for (int i = 0; i < data.size(); i++) {
    			Instance in = data.getInstance(i);
    			datas[super.predictCluster(in)].addInstance(in);
    		}
    		double cosSim = 0;
    		cosSim = CosSim.cosSim(datas,super.centroids, k);
    		
    		System.out.println("cosSim = "+cosSim);
    		System.out.println("old bestCosSim  = "+bestCosSim);
    		if (cosSim > bestCosSim){
    			bestCosSim = cosSim;
    			bestCentroids  = super.centroids;
    			bestNumberOfClusters = k;
    		}
    		System.out.println("new bestCosSim  = "+bestCosSim);
    		System.out.println("bestNumberOfClusters = "+bestNumberOfClusters);
    		System.out.println();
    	}
        
    }


    public int getNumberOfClusters() {
        return this.bestNumberOfClusters;
    }

    public int predictCluster(Instance instance) {
        if (this.centroids == null)
            throw new RuntimeException("The cluster should first be constructed");
        int tmpCluster = -1;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.bestNumberOfClusters; i++) {
            double dist = dm.calculateDistance(bestCentroids[i], instance);
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

}
