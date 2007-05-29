/**
 * KMeans.java
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

import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * Implements the K-means algorithms as described by Mac Queen in 1967.
 * 
 * <bibtex> J. B. MacQueen (1967): "Some Methods for classification and Analysis
 * of Multivariate Observations, Proceedings of 5-th Berkeley Symposium on
 * Mathematical Statistics and Probability", Berkeley, University of California
 * Press, 1:281-297 </bibtex>
 * 
 * XXX add pseudocode of the algorithm
 * 
 * @author Thomas Abeel
 * 
 */
public class KMeans implements Clusterer {
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
     * The centroids of the different clusters.
     */
    private Instance[] centroids;

    /**
     * Constuct a default Simple K-means clusterer with 100 iterations, 2
     * clusters, a default random generator and using the Euclidean distance.
     */
    public KMeans() {
    	this(4, 100);
    }
    
    /**
     * Create a new Simple K-means clusterer with the given number of clusters
     * and iterations. The internal random generator is a new one based upon the
     * current system time. For the distance we use the Euclidean n-space
     * distance.
     * 
     * @param clusters
     *            the number of clusters
     * @param iterations
     *            the number of iterations
     */
    public KMeans(int clusters, int iterations) {
        this(clusters, iterations, new EuclideanDistance());
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
     * 
     * @param dm
     *            the distance measure to use
     */
    public KMeans(int clusters, int iterations, DistanceMeasure dm) {
        this.numberOfClusters = clusters;
        this.numberOfIterations = iterations;
        this.dm = dm;
        rg = new Random(System.currentTimeMillis());
    }

    /**
     * XXX add doc
     */
    public Dataset[] executeClustering(Dataset data) {
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
            double[] randomInstance = new double[instanceLength];
            for (int i = 0; i < instanceLength; i++) {
                double dist = Math.abs(max.getValue(i) - min.getValue(i));
                randomInstance[i] = (float) (min.getValue(i) + rg.nextDouble() * dist);

            }
            this.centroids[j] = new SimpleInstance(randomInstance);
        }

        int iterationCount = 0;
        boolean centroidsChanged = true;
        boolean randomCentroids=true;
        while (randomCentroids ||(iterationCount < this.numberOfIterations && centroidsChanged)) {
            iterationCount++;
            // Assign each object to the group that has the closest centroid.
            int[] assignment = new int[data.size()];
            for (int i = 0; i < data.size(); i++) {
                int tmpCluster = 0;
                double minDistance = dm.calculateDistance(centroids[0], data.getInstance(i));
                for (int j = 1; j < centroids.length; j++) {
                    double dist = dm.calculateDistance(centroids[j], data.getInstance(i));
                    if (dm.compare(dist, minDistance)) {
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
                Instance in = data.getInstance(i);
                for (int j = 0; j < instanceLength; j++) {

                    sumPosition[assignment[i]][j] += in.getWeight() * in.getValue(j);

                }
                countPosition[assignment[i]]++;
            }
            centroidsChanged = false;
            randomCentroids=false;
            for (int i = 0; i < this.numberOfClusters; i++) {
                if (countPosition[i] > 0) {
                    double[] tmp = new double[instanceLength];
                    for (int j = 0; j < instanceLength; j++) {
                        tmp[j] = (float) sumPosition[i][j] / countPosition[i];
                    }
                    Instance newCentroid = new SimpleInstance(tmp);
                    if (dm.calculateDistance(newCentroid, centroids[i]) > 0.0001) {
                        centroidsChanged = true;
                        centroids[i] = newCentroid;
                    }
                } else {
                    double[] randomInstance = new double[instanceLength];
                    for (int j = 0; j < instanceLength; j++) {
                        double dist = Math.abs(max.getValue(j) - min.getValue(j));
                        randomInstance[j] = (float) (min.getValue(j) + rg.nextDouble() * dist);

                    }
                    randomCentroids = true;
                    this.centroids[i] = new SimpleInstance(randomInstance);
                }

            }

        }
        Dataset[] output = new Dataset[centroids.length];
        for (int i = 0; i < centroids.length; i++)
            output[i] = new SimpleDataset();
        for (int i = 0; i < data.size(); i++) {
            int tmpCluster = 0;
            double minDistance = dm.calculateDistance(centroids[0], data.getInstance(i));
            for (int j = 0; j < centroids.length; j++) {
                double dist = dm.calculateDistance(centroids[j], data.getInstance(i));
                if (dm.compare(dist, minDistance)) {
                    minDistance = dist;
                    tmpCluster = j;
                }
            }
            output[tmpCluster].addInstance(data.getInstance(i));

        }
        return output;
    }

}
