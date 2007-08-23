/**
 * KMedoids.java
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
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * Implementation of the K-medoids algorithm
 * 
 * @author Thomas Abeel
 * 
 */
public class KMedoids implements Clusterer {
    /**
     * XXX doc
     */
    private DistanceMeasure dm;

    /**
     * XXX doc
     */
    private int numberOfClusters;

    /**
     * XXX doc
     */
    private Random rg;

    /**
     * XXX doc
     */
    private int maxIterations;

    /**
     * default constructor
     */
    public KMedoids() {
        this(4, 100, new EuclideanDistance());
    }

    /**
     * XXX doc
     * 
     * @param numberOfClusters
     * @param maxIterations
     * @param DistanceMeasure
     *            dm
     * 
     */
    public KMedoids(int numberOfClusters, int maxIterations, DistanceMeasure dm) {
        super();
        this.numberOfClusters = numberOfClusters;
        this.maxIterations = maxIterations;
        this.dm = dm;
        rg = new Random(System.currentTimeMillis());
    }

    /**
     * XXX doc
     */
    public Dataset[] executeClustering(Dataset data) {
        Instance[] medoids = new Instance[numberOfClusters];
        Dataset[] output = new SimpleDataset[numberOfClusters];
        for (int i = 0; i < numberOfClusters; i++) {
            int random = rg.nextInt(data.size());
            medoids[i] = data.getInstance(random);
        }

        boolean changed = true;
        int count = 0;
        while (changed && count < maxIterations) {
            changed = false;
            count++;
            int[] assignment = assign(medoids, data);
            changed = recalculateMedoids(assignment, medoids, output, data);

        }
        if (count == maxIterations) {
            // HACK: in this case there can be empty clusters. When the number of
            // iterations is set too low or the number of clusters too high, it
            // may not be possible for the algorithm to find enough non empty
            // clusters within the maximum number of iterations.
            return DatasetTools.filterEmpty(output);
        } else {
            return output;
        }
    }

    /**
     * Assign all instances from the dataset to the medoids.
     * 
     * @param medoids
     * @param data
     * @return
     */
    private int[] assign(Instance[] medoids, Dataset data) {
        int[] out = new int[data.size()];
        for (int i = 0; i < data.size(); i++) {
            double bestDistance = dm.calculateDistance(data.getInstance(i), medoids[0]);
            int bestIndex = 0;
            for (int j = 1; j < medoids.length; j++) {
                double tmpDistance = dm.calculateDistance(data.getInstance(i), medoids[j]);
                if (dm.compare(tmpDistance, bestDistance)) {
                    bestDistance = tmpDistance;
                    bestIndex = j;
                }
            }
            out[i] = bestIndex;

        }
        return out;

    }

    /**
     * Return a array with on each position the clusterIndex to which the
     * Instance on that position in the dataset belongs.
     * 
     * @param medoids
     *            the current set of cluster medoids, will be modified to fit
     *            the new assignment
     * @param assigment
     *            the new assignment of all instances to the different medoids
     * @param output
     *            the cluster output, this will be modified at the end of the
     *            method
     * @return the
     */
    private boolean recalculateMedoids(int[] assignment, Instance[] medoids, Dataset[] output, Dataset data) {
        boolean changed = false;
        for (int i = 0; i < numberOfClusters; i++) {
            output[i] = new SimpleDataset();
            for (int j = 0; j < assignment.length; j++) {
                if (assignment[j] == i) {
                    output[i].addInstance(data.getInstance(j));
                }
            }
            if (output[i].size() == 0) { // new random, empty medoid
                medoids[i] = data.getInstance(rg.nextInt(data.size()));
                changed = true;
            } else {
                Instance centroid = DatasetTools.getCentroid(output[i]);
                Instance oldMedoid = medoids[i];
                medoids[i] = DatasetTools.getClosest(data, dm, centroid);
                if (!medoids[i].equals(oldMedoid))
                    changed = true;
            }
        }
        return changed;
    }

}
