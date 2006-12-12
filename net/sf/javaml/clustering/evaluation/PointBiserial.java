/**
 * PointBiserial.java, 6-dec-06
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

public class PointBiserial implements ClusterEvaluation {
	private DistanceMeasure dm = new EuclideanDistance();

	public double score(Clusterer c, Dataset data) {
		Dataset[] datas = new Dataset[c.getNumberOfClusters()];
		double dw = 0, fw = 0;
		double db = 0, fb = 0;
		double nd, sd, pb;
		// get clusters
		for (int i = 0; i < c.getNumberOfClusters(); i++) {
			datas[i] = new SimpleDataset();
		}
		for (int i = 0; i < data.size(); i++) {
			Instance in = data.getInstance(i);
			datas[c.predictCluster(in)].addInstance(in);
		}
		for (int i = 0; i < c.getNumberOfClusters(); i++) {
			for (int j = 0; j < datas[i].size(); j++) {
				Instance x = datas[i].getInstance(j);
				// calculate sum of intra cluster distances dw and count their
				// number.
				for (int k = j + 1; k < datas[i].size(); k++) {
					Instance y = datas[i].getInstance(k);
					double distance = dm.calculateDistance(x, y);
					dw += distance;
					fw++;
				}
				// calculate sum of inter cluster distances dw and count their
				// number.
				for (int k = i + 1; k < c.getNumberOfClusters(); k++) {
					for (int l = 0; l < datas[k].size(); l++) {
						Instance y = datas[k].getInstance(l);
						double distance = dm.calculateDistance(x, y);
						db += distance;
						fb++;
					}
				}
			}
		}
		// calculate total number of distances
		nd = fw + fb;
		// calculate mean dw and db
		double meanDw = dw / fw;
		double meanDb = db / fb;
		// calculate standard deviation of all distances (sum inter and intra)
		double tmpSdw = 0, tmpSdb = 0;
		for (int i = 0; i < c.getNumberOfClusters(); i++) {
			for (int j = 0; j < datas[i].size(); j++) {
				Instance x = datas[i].getInstance(j);
				for (int k = j + 1; k < datas[i].size(); k++) {
					Instance y = datas[i].getInstance(k);
					double distance = dm.calculateDistance(x, y);
					tmpSdw += (distance - meanDw)*(distance - meanDw);
				}
				for (int k = i + 1; k < c.getNumberOfClusters(); k++) {
					for (int l = 0; l < datas[k].size(); l++) {
						Instance y = datas[k].getInstance(l);
						double distance = dm.calculateDistance(x, y);
						tmpSdb += (distance - meanDb)*(distance - meanDb);
					}
				}
			}
		}
		sd = Math.sqrt((tmpSdw+tmpSdb) / nd);
		// calculate point biserial score		
		pb = (meanDb - meanDw) * Math.sqrt(((fw * fb) / (nd * nd))) / sd;
		return pb;
	}

	public boolean compareScore(double score1, double score2) {
		// TODO check condition for best score???
		return score2 > score1;
	}
}
