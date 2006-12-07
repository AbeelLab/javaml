/**
 * Tau.java, 6-dec-06
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
public class Tau implements ClusterEvaluation {
	private DistanceMeasure dm = new EuclideanDistance();

	public double score(Clusterer c, Dataset data) {
		Dataset[] datas = new Dataset[c.getNumberOfClusters()];
		double maxIntraDist[] = new double[c.getNumberOfClusters()];
		double sPlus = 0,sMin = 0;
		double fw = 0, fb = 0;
		double t = 0, nd;	
		// get clusters
		for (int i = 0; i < c.getNumberOfClusters(); i++) {
			datas[i] = new SimpleDataset();
		}
		for (int i = 0; i < data.size(); i++) {
			Instance in = data.getInstance(i);
			datas[c.predictCluster(in)].addInstance(in);
		}
		for (int i = 0; i < c.getNumberOfClusters(); i++) {
			maxIntraDist[i] = Double.MIN_VALUE;
			for (int j = 0; j < datas[i].size(); j++) {
				Instance x = datas[i].getInstance(j);
				// calculate intra cluster distances, count their number and find max.
				// count t.
				for (int k = j + 1; k < datas[i].size(); k++) {
					Instance y = datas[i].getInstance(k);
					double distance = dm.calculateDistance(x, y);
					fw++;
					t++;
					if (maxIntraDist[i] < distance) {
						maxIntraDist[i] = distance;
					}
				}
				// calculate inter cluster distances, count their number and find min.
				// count sPlus, sMin and t.
				for (int k = i + 1; k < c.getNumberOfClusters(); k++) {
					for (int l = 0; l < datas[k].size(); l++) {
						Instance y = datas[k].getInstance(l);
						double distance = dm.calculateDistance(x, y);
						fb++;
						
						if (distance < maxIntraDist[i]) {
							sMin++;
						}
						t++;
						if (distance > maxIntraDist[i]) {
							sPlus++;
						}
						t++;
					}
				}
			}
		}
		nd = fw + fb;
		System.out.println("s(+): "+sPlus+",s(-): "+sMin);
		System.out.println("Nd: "+nd+",t: "+t);
		double tau = (sPlus-sMin)/Math.sqrt((nd*(nd-1)/2-t)*(nd*(nd-1)/2));
		System.out.println("tau: "+tau);
		return tau;
	}

	public boolean compareScore(double score1, double score2) {
		// TODO check condition for best score: should be maxed??
		return score2 > score1;
	}

}
