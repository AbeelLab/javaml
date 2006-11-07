/**
 * SimpleKMeans.java, 24-okt-06
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

import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.DistanceMeasureFactory;

public class SimpleKMeans implements Clusterer {
    /**
     * The number of clusters.
     */
    private int numberOfClusters = -1;

    /**
     * The number of iterations the algorithm should make. If this value is
     * Integer.INFINITY, then the algorithm runs until the centroids no longer
     * change.
     * 
     */
    private int numberOfIterations = -1;

    /**
     * Random generator for this clusterer.
     */
    private Random rg;

    /**
     * The distance measure used in the algorithm, defaults to Euclidean
     * distance.
     */
    private DistanceMeasure dm;

    /**
     * Constuct a default Simple K-means clusterer with 100 iterations, 2
     * clusters, a default random generator and using the Euclidean distance.
     */
    public SimpleKMeans() {
        this(2, 100);
    }

    /**
     * The centroids of the different clusters.
     */
    private Instance[] centroids;

    /**
     * Create a new Simple K-means clusterer with the given number of clusters
     * and iterations. The internal random generator is a new one based upon the
     * current system time. For the distance we use the Euclidean n-space
     * distance.
     * 
     * @param clusters
     *            the number of clustesr
     * @param iterations
     *            the number of iterations
     */
    public SimpleKMeans(int clusters, int iterations) {
        this(clusters, iterations, new Random(System.currentTimeMillis()));
    }

    /**
     * Create a new K-means clusterer with the given number of clusters and
     * iterations. Also the Random Generator for the clusterer is given as
     * parameter.
     * 
     * @param clusters
     *            the number of clustesr
     * @param iterations
     *            the number of iterations
     * @param rg
     *            the random generator for the clusterer
     */
    public SimpleKMeans(int clusters, int iterations, Random rg) {
        this(clusters, iterations, rg, DistanceMeasureFactory.getEuclideanDistanceMeasure());

    }

    /**
     * Create a new K-means clusterer with the given number of clusters and
     * iterations. Also the Random Generator for the clusterer is given as
     * parameter.
     * 
     * @param clusters
     *            the number of clustesr
     * @param iterations
     *            the number of iterations
     * @param rg
     *            the random generator for the clusterer
     * @param dm
     *            the distance measure to use
     */
    public SimpleKMeans(int clusters, int iterations, Random rg, DistanceMeasure dm) {
        this.numberOfClusters = clusters;
        this.numberOfIterations = iterations;
        this.rg = rg;
        this.dm = dm;

    }

    public void buildClusterer(Dataset data) {
        if (data.size() == 0)
            throw new RuntimeException("The dataset should not be empty");
        if (numberOfClusters == 0)
            throw new RuntimeException("There should be at least one cluster");
        // Place K points into the space represented by the objects that are
        // being clustered. These points represent the initial group of
        // centroids.
        Instance min = data.getMinimumInstance();
        Instance max = data.getMaximumInstance();
        this.centroids = new Instance[numberOfClusters];
        int instanceLength = data.getInstance(0).size();
        for (int j = 0; j < numberOfClusters; j++) {
            float[] randomInstance = new float[instanceLength];
            for (int i = 0; i < instanceLength; i++) {
                float dist = Math.abs(max.getValue(i) - min.getValue(i));
                randomInstance[i] = (float)(min.getValue(i) + rg.nextDouble() * dist);

            }
            this.centroids[j] = new SimpleInstance(randomInstance);
        }

        int iterationCount = 0;
        boolean centroidsChanged = true;
        while (iterationCount < this.numberOfIterations && centroidsChanged) {
            iterationCount++;
            // Assign each object to the group that has the closest centroid.
            int[] assignment = new int[data.size()];
            for (int i = 0; i < data.size(); i++) {
                int tmpCluster = -1;
                double minDistance = Double.MAX_VALUE;
                for (int j = 0; j < centroids.length; j++) {
                    double dist = dm.calculateDistance(centroids[j], data.getInstance(i));
                    if (dist < minDistance) {
                        minDistance = dist;
                        tmpCluster = j;
                    }
                }
                assignment[i] = tmpCluster;

            }
            // When all objects have been assigned, recalculate the positions of
            // the K centroids and start over.
            // The new position of the centroid is the weighted center of the
            // current cluster.
            double[][] sumPosition = new double[this.numberOfClusters][instanceLength];
            int[] countPosition = new int[this.numberOfClusters];
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < instanceLength; j++) {
                    Instance in = data.getInstance(i);
                    sumPosition[assignment[i]][j] += in.getWeight() * in.getValue(j);
                    countPosition[assignment[i]]++;
                }
            }
            centroidsChanged = false;
            for (int i = 0; i < this.numberOfClusters; i++) {
                if (countPosition[i] > 0) {// when there are no instances
                    // associated with this centroid, it
                    // remains the same.
                    float[] tmp = new float[instanceLength];
                    for (int j = 0; j < instanceLength; j++) {
                        tmp[j] = (float)sumPosition[i][j] / countPosition[i];
                    }
                    Instance newCentroid = new SimpleInstance(tmp);
                    if (dm.calculateDistance(newCentroid, centroids[i]) > 0.0001) {
                        centroidsChanged = true;
                        centroids[i] = newCentroid;
                    }
                }

            }

        }

    }

    public int getNumberOfClusters() {
        return this.numberOfClusters;
    }

    public int predictCluster(Instance instance) {
        if (this.centroids == null)
            throw new RuntimeException("The clusterer should first be constructed using the buildClusterer method.");
        int tmpCluster = -1;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.numberOfClusters; i++) {
            double dist = dm.calculateDistance(centroids[i], instance);
            if (dist < minDistance) {
                minDistance = dist;
                tmpCluster = i;
            }
        }
        return tmpCluster;
    }

    public double[] predictMembershipDistribution(Instance instance) {
        double[] tmp = new double[this.getNumberOfClusters()];
        tmp[this.predictCluster(instance)] = 1;
        return tmp;
    }

}
