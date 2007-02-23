/**
 * HybridCentroidSimilarity.java
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

package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.DistanceMeasure;

/**
 * H_2 from the Zhao 2001 paper
 * 
 * TODO uitleg
 * 
 * @author Andreas De Rijcke
 * @author Thomas Abeel
 * 
 */

public class HybridCentroidSimilarity implements ClusterEvaluation {

    public HybridCentroidSimilarity(DistanceMeasure dm) {
        this.dm = dm;
    }

    private DistanceMeasure dm;
    public double score(Dataset[] datas) {
        ClusterEvaluation ceTop = new SumOfCentroidSimilarities(dm);// I_2
        double sum = ceTop.score(datas);
        ClusterEvaluation ce = new TraceScatterMatrix(dm);// E_1
        sum /= ce.score(datas);

        return sum;
    }

    public boolean compareScore(double score1, double score2) {
        // should be maxed
        return score2 > score1;
    }
}
