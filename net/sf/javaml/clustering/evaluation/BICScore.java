/**
 * BICScore.java, 17-nov-06
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

import net.sf.javaml.core.Dataset;
import net.sf.javaml.utils.LogLikelihoodFunction;

/**
 * XXX DOC
 * 
 * @author Andreas De Rijcke
 * @author Thomas Abeel
 * 
 */

public class BICScore implements ClusterEvaluation {

	public double score(Dataset[] clusters) {
		// number of free parameters K
		double k = 1;
		// sampelsize N
		double datasize = 0;

		for (int i = 0; i < clusters.length; i++) {
			datasize += clusters[i].size();
		}
		LogLikelihoodFunction likelihood = new LogLikelihoodFunction();
		// loglikelihood log(L)
		double l = likelihood.loglikelihoodsum(clusters);
		// BIC score
		double bic = -2 * l + Math.log10(datasize) * k;
		return bic;
	}

	public boolean compareScore(double score1, double score2) {
		// should be minimzed.
		return score2 > score1;
	}
}