/**
 * HybridPairwiseSimilarities.java, 16-nov-2006
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
 * Copyright (c) 2006, Thomas Abeel, Andreas De Rijcke
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.evaluation.TraceScatterMatrix;
import net.sf.javaml.clustering.evaluation.SumOfAveragePairwiseSimilarities;
import net.sf.javaml.core.Dataset;

/**
 * H_1 from the Zhao 2001 paper
 * 
 * TODO uitleg
 * @author Andreas De Rijcke
 *
 */

public class HybridPairwiseSimilarities implements ClusterEvaluation{
		
	public double score (Clusterer c, Dataset data) {
		ClusterEvaluation ceTop = new SumOfAveragePairwiseSimilarities();//I_1
		double sum = ceTop.score(c, data);
		ClusterEvaluation ce = new TraceScatterMatrix();//E_1
		sum /= ce.score(c, data);
	
	return sum;
	}
	
	public boolean compareScore(double score1, double score2) {
        //should be maxed
        return score2>score1;
	}
}
