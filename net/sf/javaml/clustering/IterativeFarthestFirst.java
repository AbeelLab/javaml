/**
 * IterativeFarthestFirst.java
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
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering;

import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * XXX DOC
 * 
 * @author Thomas Abeel
 * 
 */
public class IterativeFarthestFirst implements Clusterer {
    /**
     * XXX DOC
     */
    private DistanceMeasure dm;

    /**
     * XXX DOC
     */
    private ClusterEvaluation ce;

    /**
     * XXX DOC
     */
    private int min, max;

    
    public IterativeFarthestFirst(ClusterEvaluation ce){
        this(new EuclideanDistance(),ce,2,6);
    }
    /**
     * XXX DOC
     * 
     * @param dm
     * @param ce
     * @param min
     * @param max
     */
    public IterativeFarthestFirst(DistanceMeasure dm, ClusterEvaluation ce, int min, int max) {
        super();
        this.dm = dm;
        this.ce = ce;
        this.min = min;
        this.max = max;
    }

    /**
     * XXX DOC
     */
    public Dataset[] executeClustering(Dataset data) {

        FarthestFirst ff = new FarthestFirst(min, dm);
        Dataset[] bestClusters = ff.executeClustering(data);
        double bestScore = ce.score(bestClusters);

        for (int i = min + 1; i <= max; i++) {
            ff = new FarthestFirst(i, dm);
            Dataset[] tmp = ff.executeClustering(data);
            double tmpScore = ce.score(tmp);
            if (ce.compareScore(bestScore, tmpScore)) {
                bestScore = tmpScore;
                bestClusters = tmp;
            }

        }
        return bestClusters;
    }

}
