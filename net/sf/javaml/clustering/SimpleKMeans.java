/**
 * SimpleKMeans.java, 24-okt-06
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

import java.util.Map;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.ArrayFactory;

public class SimpleKMeans implements Clusterer {
	/**
	 * The number of clusters.
	 */
	private int numberOfClusters = 2;

	/**
	 * The number of iterations the algorithm should make. If this value is
	 * Integer.INFINITY, then the algorithm runs until the centroids no longer
	 * change.
	 * 
	 */
	private int numberOfIterations = 100;

	public SimpleKMeans(){
		
	}
	public SimpleKMeans(int clusters,int iterations){
		this.numberOfClusters=clusters;
		this.numberOfIterations=iterations;
	}
	
	
	public void buildClusterer(Dataset data) {
		if(data.size()==0)
			throw new RuntimeException("The dataset should not be empty");
		// Place K points into the space represented by the objects that are
		// being clustered. These points represent initial group centroids.
		
		int instanceLength=data.getInstance(0).size();
		for(int i=0;i<data.size();i++){
			//TODO busy here....
			
		}
		
		Instance[]centroids=new Instance[numberOfClusters];
		
		// Assign each object to the group that has the closest centroid.
		
		// When all objects have been assigned, recalculate the positions of the
		// K centroids and start over.
		
		
	}

	public int getNumberOfClusters() {
		return this.numberOfClusters;
	}

	public Number predictCluster(Instance instance) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<Number, Double> predictMembershipDistribution(Instance instance) {
		// TODO Auto-generated method stub
		return null;
	}

}



	/**
	 * Runs clustering algorithm.
	 * 
	 * @exception MiningException
	 *                cannot run algorithm
	 */
	protected void runAlgorithm() throws MiningException {

		int numbAtt = metaData.getAttributesNumber();
		int numbVec = 0;

		// Get minimum and maximum of attributes:
		double[] minArr = new double[numbAtt];
		double[] maxArr = new double[numbAtt];
		for (int i = 0; i < numbAtt; i++) {
			minArr[i] = 0.0;
			maxArr[i] = 0.0;
		}
		;
		while (miningInputStream.next()) {
			MiningVector vec = miningInputStream.read();
			for (int i = 0; i < numbAtt; i++) {
				if (vec.getValue(i) < minArr[i])
					minArr[i] = vec.getValue(i);
				if (vec.getValue(i) > maxArr[i])
					maxArr[i] = vec.getValue(i);
			}
			;
			numbVec = numbVec + 1;
		}
		;
		distance.setMinAtt(minArr);
		distance.setMaxAtt(maxArr);

		// Create array of clusters:
		clusters = new Cluster[numberOfClusters];
		for (int i = 0; i < numberOfClusters; i++) {
			clusters[i] = new Cluster();
			clusters[i].setName("clust" + String.valueOf(i));
		}
		;

		// Find numbOfClusters random vectors:
		clusterAssignments = new int[numbVec];
		boolean selected[] = new boolean[numbVec];
		Random rand = new Random(10);
		for (int i = 0; i < numberOfClusters; i++) {
			int index = 0;
			do {
				index = Math.abs(rand.nextInt()) % numbVec;
			} while (selected[index]);

			// Add center vector to cluster array:
			MiningVector vec = miningInputStream.read(index);
			clusters[i].setCenterVec(vec);

			selected[index] = true;
		}
		;

		// Iterations:
		numberOfIterations = 0;
		boolean converged = false;
		while (!converged && numberOfIterations < maxNumberOfIterations) {
			System.out.println("iter: " + (numberOfIterations + 1));
			converged = true;

			// Find nearest cluster for all vectors:
			for (int i = 0; i < numbVec; i++) {
				int nC = clusterVector(miningInputStream.read(i));
				if (nC != clusterAssignments[i]) {
					clusterAssignments[i] = nC;
					converged = false;
				}
				;
			}
			;

			// Find new center vectors:
			for (int i = 0; i < numberOfClusters; i++) {
				double[] nullVal = new double[numbAtt];
				MiningVector nullVec = new MiningVector(nullVal);
				nullVec.setMetaData(metaData);
				clusters[i].setCenterVec(nullVec);
			}
			;
			int[] cardClusters = new int[numberOfClusters];
			for (int i = 0; i < numbVec; i++) {
				MiningVector vec = miningInputStream.read(i);
				int index = clusterAssignments[i];
				for (int j = 0; j < numbAtt; j++) {
					double val = clusters[index].getCenterVec().getValue(j);
					val = val + vec.getValue(j);
					clusters[index].getCenterVec().setValue(j, val);
				}
				;
				cardClusters[index] = cardClusters[index] + 1;
			}
			;
			for (int i = 0; i < numberOfClusters; i++) {
				for (int j = 0; j < numbAtt; j++) {
					double val = clusters[i].getCenterVec().getValue(j);
					int card = cardClusters[i];
					if (card == 0)
						card = 1;
					val = val / card;
					clusters[i].getCenterVec().setValue(j, val);
				}
				;
			}
			;
			numberOfIterations = numberOfIterations + 1;
		}
		;

	}

	/**
	 * Assign vector to nearest cluster.
	 * 
	 * @param vec
	 *            mining vector to be assigned to nearest cluster
	 * @return number of the nearest cluster, -1 if no cluster exist
	 * @exception MiningException
	 *                cannot cluster vector
	 */
	private int clusterVector(MiningVector vec) throws MiningException {

		if (clusters == null || clusters.length == 0)
			return -1;

		int nearestClust = 0;
		double minDist = distance.distance(vec, clusters[0].getCenterVec());
		for (int i = 1; i < numberOfClusters; i++) {
			double dist = distance.distance(vec, clusters[i].getCenterVec());
			if (dist < minDist) {
				minDist = dist;
				nearestClust = i;
			}
			;
		}
		;

		return nearestClust;
	}

	/**
	 * Sets number of clusters.
	 * 
	 * @param numberOfClusters
	 *            new number of clusters
	 */
	public void setNumberOfClusters(int numberOfClusters) {
		this.numberOfClusters = numberOfClusters;
	}

	/**
	 * Returns number of clusters.
	 * 
	 * @return number of clusters
	 */
	public int getNumberOfClusters() {
		return numberOfClusters;
	}

	/**
	 * Sets maximum number of iterations.
	 * 
	 * @param maxNumberOfIterations
	 *            new maximum number of iterations
	 */
	public void setMaxNumberOfIterations(int maxNumberOfIterations) {
		this.maxNumberOfIterations = maxNumberOfIterations;
	}

	/**
	 * Returns maximum number of iterations.
	 * 
	 * @return maximum number of iteartions
	 */
	public int getMaxNumberOfIterations() {
		return maxNumberOfIterations;
	}
}
