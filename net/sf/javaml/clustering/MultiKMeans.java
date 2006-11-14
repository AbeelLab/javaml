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
import net.sf.javaml.distance.DistanceMeasureFactory;
/**
 * This variant of the SimpleKMeans algorithm will run the Simple KMeans
 * algorithm multiple times and will only return the best centroids as the final
 * result.
 * 
 * @author Thomas Abeel, Andreas De Rijcke
 * 
 */

public class MultiKMeans extends SimpleKMeans implements Clusterer {
	private int k;
	private int repeats;
	private double bestCosSim;
	private Instance[] bestCentroids;
	
	public MultiKMeans (int k, int repeats){
		this.k = k;
		this.repeats = repeats;
	}
	
	public void buildcluster (Dataset data){
		 super.dm=DistanceMeasureFactory.getCosineSimilarity();
	    if (data.size() == 0)
			throw new RuntimeException("The dataset should not be empty");
	    if (k == 0)
			throw new RuntimeException("There should be at least one cluster");
	    	
	    	bestCosSim=0;
	    for (int i = 0; i < repeats; i++){
	    	//Clusterer km=new super(k,100);
	    	super.numberOfClusters = k;
	    	super.numberOfIterations = 100;
	    	super.buildClusterer(data);
	    	Dataset[] datas = new Dataset[k];
	    	for (int j = 0; j < k; j++) {
	    		datas[j] = new SimpleDataset();
	    	}
	    	for (int j = 0; j < data.size(); j++) {
	    		Instance in = data.getInstance(j);
	    		datas[super.predictCluster(in)].addInstance(in);
	    	}
	    	
	    	double cosSim = 0;
    		cosSim = CosSim.cosSim(datas,super.centroids, k);
    		System.out.println("cosSim = "+cosSim);
    		System.out.println("old bestCosSim  = "+bestCosSim);
    		if (cosSim > bestCosSim){
    			bestCosSim = cosSim;
    			bestCentroids  = super.centroids;
    		}
    		System.out.println("new bestCosSim  = "+bestCosSim);
    		System.out.println();
	    }
	    super.centroids=bestCentroids;
	    super.numberOfClusters=k;
	    System.out.println("Final centroid count: "+super.centroids.length);
        System.out.println("Final number of Clusters: "+super.numberOfClusters);
	    
	}
	
	
}
