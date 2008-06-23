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
import net.sf.javaml.distance.EuclideanDistance;

/**
 * This class implements an extension of KMeans. SKM will be run several
 * iterations with a different k value, starting from kMin and increasing to
 * kMax. Each clustering result is evaluated with an evaluation score, the
 * result with the best score will be returned as final result.
 * 
 * @param kMin
 *            minimal value for k (the number of clusters)
 * @param kMax
 *            maximal value for k (the number of clusters)
 * @param iterations
 *            the number of iterations in SKM
 * @param dm
 *            distance measure used for internal cluster evaluation
 * @param ce
 *            clusterevaluation methode used for internal cluster evaluation
 * 
 * @author Thomas Abeel
 * @author Andreas De Rijcke
 * 
 */
public class IterativeKMeans implements Clusterer {

    /**
     * XXX add doc
     */
    private int kMin;

    /**
     * XXX add doc
     */
    private int kMax;

    /**
     * XXX add doc
     */
    private ClusterEvaluation ce;

    /**
     * XXX add doc
     */
    private DistanceMeasure dm;

    /**
     * XXX add doc
     */
    private int iterations;

    /**
     * default constructor
     * @param ClusterEvaluation ce
     */
    public IterativeKMeans(ClusterEvaluation ce){
        this(2,10,100,new EuclideanDistance(),ce);
    }
    
    /**
     * XXX add doc
     * 
     * @param kMin
     * @param kMax
     * @param ClusterEvaluation ce
     * 
     */
    public IterativeKMeans(int kMin, int kMax, ClusterEvaluation ce) {
    	this(kMin,kMax,100,new EuclideanDistance(),ce);
    }
    
    /**
     * XXX add doc
     * 
     * @param kMin
     * @param kMax
     * @param interations
     * @param DistanceMeasure dm
     * @param ClusterEvaluation ce
     * 
     */
    public IterativeKMeans(int kMin, int kMax, int iterations, DistanceMeasure dm, ClusterEvaluation ce) {
        this.kMin = kMin;
        this.kMax = kMax;
        this.iterations = iterations;
        this.dm = dm;
        this.ce = ce;
    }

    /**
     * XXX add doc
     */
    public Dataset[] cluster(Dataset data) {
        KMeans km = new KMeans(this.kMin, this.iterations, this.dm);
        Dataset[] bestClusters = km.cluster(data);
        double bestScore = this.ce.score(bestClusters);
        for (int i = kMin + 1; i <= kMax; i++) {
            km = new KMeans(i, this.iterations, this.dm);
            Dataset[] tmpClusters = km.cluster(data);
            double tmpScore = this.ce.score(tmpClusters);
            if (this.ce.compareScore(bestScore, tmpScore)) {
                bestScore = tmpScore;
                bestClusters = tmpClusters;
            }
        }
        return bestClusters;
    }
}
