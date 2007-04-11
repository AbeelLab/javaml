/**
 * Gamma.java
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

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;

/**
 * TODO uitleg
 * 
 * @author Andreas De Rijcke
 */
public class Gamma implements ClusterEvaluation {

	public Gamma(DistanceMeasure dm) {
		this.dm = dm;
	}

	private DistanceMeasure dm;

	public double score(Dataset[] datas) {
		double maxIntraDist = Double.MIN_VALUE;
		double sPlus = 0, sMin = 0;

		// calculate max intra cluster distance
		for (int i = 0; i < datas.length; i++) {
			for (int j = 0; j < datas[i].size(); j++) {
				Instance x = datas[i].getInstance(j);
				for (int k = j + 1; k < datas[i].size(); k++) {
					Instance y = datas[i].getInstance(k);
					double distance = dm.calculateDistance(x, y);
					if (maxIntraDist < distance) {
						maxIntraDist = distance;
					}
				}
			}
		}
		// calculate inter cluster distances
		// count sPlus and sMin
		for (int i = 0; i < datas.length; i++) {
			for (int j = 0; j < datas[i].size(); j++) {
				Instance x = datas[i].getInstance(j);
				for (int k = i + 1; k < datas.length; k++) {
					for (int l = 0; l < datas[k].size(); l++) {
						Instance y = datas[k].getInstance(l);
						double distance = dm.calculateDistance(x, y);
						if (distance < maxIntraDist) {
							sMin++;
						}
						if (distance > maxIntraDist) {
							sPlus++;
						}
					}
				}
			}
		}
		// calculate gamma
		double gamma = (sPlus - sMin) / (sPlus + sMin);
		return gamma;
	}

	public boolean compareScore(double score1, double score2) {
		// should be maximized. range = [-1,1]
		return score2 > score1;
	}
}
