/**
 * CIndex.java
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
import net.sf.javaml.distance.EuclideanDistance;

/**
 * TODO uitleg
 * 
 * @author Andreas De Rijcke
 * @author Thomas Abeel
 * 
 */

public class CIndex implements ClusterEvaluation {

    public CIndex(DistanceMeasure dm) {
        this.dm = dm;
    }

    private DistanceMeasure dm = new EuclideanDistance();

    public double score(Dataset[]clusters) {
        double dw = 0;
        double minDw = Double.MAX_VALUE, maxDw = Double.MIN_VALUE;

        // calculate intra cluster distances and sum of all.
        for (int i = 0; i < clusters.length; i++) {
            for (int j = 0; j < clusters[i].size(); j++) {
                Instance x = clusters[i].getInstance(j);
                for (int k = j + 1; k < clusters[i].size(); k++) {
                    Instance y = clusters[i].getInstance(k);
                    double distance = dm.calculateDistance(x, y);
                    dw += distance;
                    if (maxDw < distance) {
                        maxDw = distance;
                    }
                    if (minDw > distance) {
                        minDw = distance;
                    }
                }
            }
        }
        // calculate C Index
        double cIndex = (dw - minDw) / (maxDw - minDw);
        return cIndex;
    }

    public boolean compareScore(double score1, double score2) {
        // should be minimalized ( smallest intra cluster distances)
        return score2 < score1;
    }
}
