/**
 * CIndex.java, 5-dec-06
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
import net.sf.javaml.distance.EuclideanDistance;

/**
 * TODO uitleg
 * 
 * @author Andreas De Rijcke
 * 
 */

public class CIndex implements ClusterEvaluation {
	private DistanceMeasure dm = new EuclideanDistance();

	public double score(Clusterer c, Dataset data) {
		Dataset[] datas = new Dataset[c.getNumberOfClusters()];
		double dw = 0;
		double minDw = Double.MAX_VALUE, maxDw = Double.MIN_VALUE;
		// get clusters
		for (int i = 0; i < c.getNumberOfClusters(); i++) {
			datas[i] = new SimpleDataset();
		}
		for (int i = 0; i < data.size(); i++) {
			Instance in = data.getInstance(i);
			datas[c.predictCluster(in)].addInstance(in);
		}
		// calculate intra cluster distances and sum of all.
		for (int i = 0; i < c.getNumberOfClusters(); i++) {
			System.out.println("---------------------------cluster: "+i+", size: "+datas[i].size());
			for (int j = 0; j < datas[i].size(); j++) {
				Instance x = datas[i].getInstance(j);
				for (int k = j + 1; k < datas[i].size(); k++) {
					Instance y = datas[i].getInstance(k);
					double distance = dm.calculateDistance(x, y);
					System.out.println("distance: "+distance);
					System.out.println("dw: "+dw);
					dw += distance;
					System.out.println("new dw: "+dw);
					if (maxDw < distance) {
						maxDw = distance;
						System.out.println("new maxDw: "+maxDw);
					}
					if (minDw > distance) {
						minDw = distance;
						System.out.println("new minDw: "+minDw);
					}
				}
			}			
		}
		// calculate C Index
		System.out.println("dw: "+dw);
		System.out.println("minDw: "+minDw+",maxDw: "+maxDw);
		double cIndex = (dw - minDw) / (maxDw - minDw);
		return cIndex;
	}

	public boolean compareScore(double score1, double score2) {
		// TODO check condition for best score???
		return score2 > score1;
	}
}
