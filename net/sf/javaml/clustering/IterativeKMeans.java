/**
 * IterativeKMeans.java, 10-nov-2006
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
package net.sf.javaml.clustering;

import java.util.Vector;

import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.ClusterEvaluationFactory;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * TODO: uitleg
 * 
 * @author Thomas Abeel, Andreas De Rijcke
 * 
 */

public class IterativeKMeans extends SimpleKMeans {
    private int kMin;

    private int kMax;

    public IterativeKMeans(int kMin, int kMax) {
        this.kMax = kMax;
        this.kMin = kMin;
    }

    @Override
    public void buildClusterer(Dataset data) {
        System.out.println("Build Iterative clusterer");
        if (data.size() == 0)
            throw new RuntimeException("The dataset should not be empty");
        if (kMin == 0)
            throw new RuntimeException("There should be at least one cluster");

        int bestNumberOfClusters = 0;
        double bestScore = 0;
        Instance[] bestCentroids = null;

        for (int k = kMin; k <= kMax; k++) {
            super.numberOfClusters = k;
            super.numberOfIterations = 100;
            super.buildClusterer(data);

            ClusterEvaluation ce = ClusterEvaluationFactory.getTau(); // getSumOfSquaredErrors();//getSumOfAveragePairWiseSimilarities();//getSumOfCentroidSimilarities();//ClusterEvaluationFactory.getSumOfSquaredErrors();
            double newScore = ce.score(this, data);
            if (k == kMin) {// for the first value, copy results
                bestScore = newScore;
                bestCentroids = super.centroids;
                bestNumberOfClusters = k;
            }
            if (ce.compareScore(bestScore, newScore)) {// if new results are
                                                        // better, copy them
                                                        // over the old ones
                bestScore = newScore;
                bestCentroids = super.centroids;
                bestNumberOfClusters = k;
            }
            System.out.println("k = " + k);
            System.out.println("score = " + newScore);

            System.out.println("Best centroids: " + bestCentroids);
            System.out.println("new bestScore  = " + bestScore);
            System.out.println("bestNumberOfClusters = " + bestNumberOfClusters);
            System.out.println();
        }

        // copy centroids
        System.out.println("Centroids: " + bestCentroids);
        super.centroids = bestCentroids;
        super.numberOfClusters = bestNumberOfClusters;
        // FILTER BESTCENTROIDS
        int[] freqTable = new int[bestNumberOfClusters];
        for (int i = 0; i < data.size(); i++) {
            freqTable[predictCluster(data.getInstance(i))]++;
        }
        Vector<Instance> tmpCentroids = new Vector<Instance>();
        int nonEmptyClusterCount = 0;
        for (int i = 0; i < freqTable.length; i++) {
            if (freqTable[i] > 0) {
                tmpCentroids.add(bestCentroids[i]);
                nonEmptyClusterCount++;
            }
        }
        super.centroids = new Instance[tmpCentroids.size()];
        super.centroids = tmpCentroids.toArray(super.centroids);
        super.numberOfClusters = nonEmptyClusterCount;
        // System.out.println("Final centroid count: "+super.centroids.length);
        // System.out.println("Final number of Clusters:
        // "+super.numberOfClusters);
    }
}
