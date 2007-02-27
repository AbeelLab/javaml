/**
 * IterativeKMeans.java
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
 * Copyright (c) 2006-2007, Thomas Abeel
 * Copyright (c) 2006-2007, Andreas De Rijcke
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering;

import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.DistanceMeasure;

/**
 * This class implements an extension of SimpleKMeans. SKM will be run
 * several iterations with a different k value, starting from kMin and
 * increasing to kMax. Each clustering result is evaluated with an evaluation
 * score, the result with the best score will be returned as final result.
 * 
 * @author Thomas Abeel
 * @author Andreas De Rijcke
 * 
 */

public class IterativeKMeans implements Clusterer {
	private int kMin;

	private int kMax;

	private ClusterEvaluation ce;

	private DistanceMeasure dm;

	private int iterations;

	public IterativeKMeans(int kMin, int kMax, int iterations,
			DistanceMeasure dm, ClusterEvaluation ce) {
		this.kMax = kMax;
		this.iterations = iterations;
		this.kMin = kMin;
		this.dm = dm;
		this.ce = ce;
	}

	public Dataset[] executeClustering(Dataset data) {
		SimpleKMeans km = new SimpleKMeans(this.kMin, this.iterations, this.dm);
		Dataset[] bestClusters = km.executeClustering(data);
		double bestScore = this.ce.score(bestClusters);
		for (int i = kMin + 1; i <= kMax; i++) {
			km = new SimpleKMeans(i, this.iterations, this.dm);
			Dataset[] tmpClusters = km.executeClustering(data);
			double tmpScore = this.ce.score(tmpClusters);
			if (this.ce.compareScore(bestScore, tmpScore)) {
				bestScore = tmpScore;
				bestClusters = tmpClusters;
			}
		}
		return bestClusters;
	}
}
