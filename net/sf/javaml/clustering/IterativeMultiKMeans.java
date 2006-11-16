/**
 * IterativeMultiKMeans.java, 16-nov-2006
 *
 * This file is part of the Java Machine Learning API
 * 
 * php-agenda is free software; you can redistribute it and/or modify
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
 * Copyright (c) 2006, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering;

import java.util.Vector;

import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
/**
 * TODO code uitkuisen van output, er mag geen output zijn
 * @author Thomas Abeel
 *
 */
public class IterativeMultiKMeans extends SimpleKMeans {

    private int kMin;

    private int kMax;

    private int repeats;

    public IterativeMultiKMeans(int kMin, int kMax, int repeats) {
        this.kMax = kMax;
        this.kMin = kMin;
        this.repeats = repeats;
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

            for (int i = 0; i < repeats; i++) {
                super.buildClusterer(data);
                ClusterEvaluation ce = new SumOfSquaredErrors();//new SumOfCentroidSimilarities();// I_2
                double newScore = ce.score(this, data);
                if (k == kMin && i==0) {
                    bestScore = newScore;
                    bestNumberOfClusters = k;
                }
                System.out.println("k = " + k);
                System.out.println("score = " + newScore);
                // System.out.println("old bestCosSim = "+bestCosSim);
                if (ce.compareScore(bestScore, newScore)) {
                    bestScore = newScore;
                    bestCentroids = super.centroids;
                    bestNumberOfClusters = k;
                }
                System.out.println("new bestCosSim  = " + bestScore);
                System.out.println("bestNumberOfClusters = " + bestNumberOfClusters);
                System.out.println();
            }

        }

        // copy centroids
        super.centroids = bestCentroids;
        super.numberOfClusters = bestNumberOfClusters;
        // FILTER BESTCENTROIDS
        int[] freqTable = new int[bestNumberOfClusters];
        for (int i = 0; i < data.size(); i++) {
            freqTable[super.predictCluster(data.getInstance(i))]++;
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
