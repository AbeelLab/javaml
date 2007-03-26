/**
 * FarthestFirst.java
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
 * Copyright (c) 2000 Mark Hall (mhall@cs.waikato.ac.nz)
 * Copyright (c) 2002 Bernhard Pfahringer
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
import net.sf.javaml.distance.DistanceMeasure;

/**
 * Cluster data using the FarthestFirst algorithm.<br/> <br/> For more
 * information see:<br/> <br/> Hochbaum, Shmoys (1985). A best possible
 * heuristic for the k-center problem. Mathematics of Operations Research.
 * 10(2):180-184.<br/> <br/> Sanjoy Dasgupta: Performance Guarantees for
 * Hierarchical Clustering. In: 15th Annual Conference on Computational Learning
 * Theory, 351-363, 2002.<br/> <br/> Notes:<br/> - works as a fast simple
 * approximate clusterer<br/> - modelled after SimpleKMeans, might be a useful
 * initializer for it <p/>
 * 
 * BibTeX:
 * 
 * <pre>
 * &#64;article{Hochbaum1985,
 *    author = {Hochbaum and Shmoys},
 *    journal = {Mathematics of Operations Research},
 *    number = {2},
 *    pages = {180-184},
 *    title = {A best possible heuristic for the k-center problem},
 *    volume = {10},
 *    year = {1985}
 * }
 * 
 * &#64;inproceedings{Dasgupta2002,
 *    author = {Sanjoy Dasgupta},
 *    booktitle = {15th Annual Conference on Computational Learning Theory},
 *    pages = {351-363},
 *    publisher = {Springer},
 *    title = {Performance Guarantees for Hierarchical Clustering},
 *    year = {2002}
 * }
 * </pre>
 * 
 * <p/>
 * 
 * @author Bernhard Pfahringer (bernhard@cs.waikato.ac.nz)
 * @author Thomas Abeel
 * 
 */
public class FarthestFirst implements Clusterer {

    /**
     * training instances, not necessary to keep, could be replaced by centroids
     * where needed for header info
     */
    private Dataset data;

    /**
     * number of clusters to generate
     */
    private int m_NumClusters = 2;

    /**
     * holds the cluster centroids
     */
    private Instance[] centroids;

    // /**
    // * attribute min values
    // */
    // private double[] m_Min;
    //
    // /**
    // * attribute max values
    // */
    // private double[] m_Max;

    private DistanceMeasure dm;

    private void updateMinDistance(double[] minDistance, boolean[] selected, Instance center) {
        for (int i = 0; i < selected.length; i++)
            if (!selected[i]) {
                double d = dm.calculateDistance(center, data.getInstance(i));// instance(i));
                if (d < minDistance[i])
                    minDistance[i] = d;
            }
    }

    private int farthestAway(double[] minDistance, boolean[] selected) {
        double maxDistance = -1.0;
        int maxI = -1;
        for (int i = 0; i < selected.length; i++)
            if (!selected[i])
                if (maxDistance < minDistance[i]) {
                    maxDistance = minDistance[i];
                    maxI = i;
                }
        return maxI;
    }

    //
    // private void initMinMax(Instances data) {
    // m_Min = new double[data.numAttributes()];
    // m_Max = new double[data.numAttributes()];
    // for (int i = 0; i < data.numAttributes(); i++) {
    // m_Min[i] = m_Max[i] = Double.NaN;
    // }
    //
    // for (int i = 0; i < data.numInstances(); i++) {
    // updateMinMax(data.instance(i));
    // }
    // }

    // /**
    // * Updates the minimum and maximum values for all the attributes based on
    // a
    // * new instance.
    // *
    // * @param instance
    // * the new instance
    // */
    // private void updateMinMax(Instance instance) {
    //
    // for (int j = 0; j < instance.numAttributes(); j++) {
    // if (Double.isNaN(m_Min[j])) {
    // m_Min[j] = instance.value(j);
    // m_Max[j] = instance.value(j);
    // } else {
    // if (instance.value(j) < m_Min[j]) {
    // m_Min[j] = instance.value(j);
    // } else {
    // if (instance.value(j) > m_Max[j]) {
    // m_Max[j] = instance.value(j);
    // }
    // }
    // }
    // }
    // }
    //
    // /**
    // * clusters an instance that has been through the filters
    // *
    // * @param instance
    // * the instance to assign a cluster to
    // * @return a cluster number
    // */
    // protected int clusterProcessedInstance(Instance instance) {
    // double minDist = Double.MAX_VALUE;
    // int bestCluster = 0;
    // for (int i = 0; i < m_NumClusters; i++) {
    // double dist = distance(instance, centroids.instance(i));
    // if (dist < minDist) {
    // minDist = dist;
    // bestCluster = i;
    // }
    // }
    // return bestCluster;
    // }

    // /**
    // * Classifies a given instance.
    // *
    // * @param instance
    // * the instance to be assigned to a cluster
    // * @return the number of the assigned cluster as an integer if the class
    // is
    // * enumerated, otherwise the predicted value
    // * @throws Exception
    // * if instance could not be classified successfully
    // */
    // public int clusterInstance(Instance instance) throws Exception {
    // m_ReplaceMissingFilter.input(instance);
    // m_ReplaceMissingFilter.batchFinished();
    // Instance inst = m_ReplaceMissingFilter.output();
    //
    // return clusterProcessedInstance(inst);
    // }

    // /**
    // * Calculates the distance between two instances
    // *
    // * @param first
    // * the first instance
    // * @param second
    // * the second instance
    // * @return the distance between the two given instances, between 0 and 1
    // */
    // protected double distance(Instance first, Instance second) {
    //
    // double distance = 0;
    // int firstI, secondI;
    //
    // for (int p1 = 0, p2 = 0; p1 < first.numValues() || p2 <
    // second.numValues();) {
    // if (p1 >= first.numValues()) {
    // firstI = data.numAttributes();
    // } else {
    // firstI = first.index(p1);
    // }
    // if (p2 >= second.numValues()) {
    // secondI = data.numAttributes();
    // } else {
    // secondI = second.index(p2);
    // }
    // if (firstI == data.classIndex()) {
    // p1++;
    // continue;
    // }
    // if (secondI == data.classIndex()) {
    // p2++;
    // continue;
    // }
    // double diff;
    // if (firstI == secondI) {
    // diff = difference(firstI, first.valueSparse(p1), second.valueSparse(p2));
    // p1++;
    // p2++;
    // } else if (firstI > secondI) {
    // diff = difference(secondI, 0, second.valueSparse(p2));
    // p2++;
    // } else {
    // diff = difference(firstI, first.valueSparse(p1), 0);
    // p1++;
    // }
    // distance += diff * diff;
    // }
    //
    // return Math.sqrt(distance / data.numAttributes());
    // }

    // /**
    // * Computes the difference between two given attribute values.
    // */
    // protected double difference(int index, double val1, double val2) {
    //
    // switch (data.attribute(index).type()) {
    // case Attribute.NOMINAL:
    //
    // // If attribute is nominal
    // if (Instance.isMissingValue(val1) || Instance.isMissingValue(val2) ||
    // ((int) val1 != (int) val2)) {
    // return 1;
    // } else {
    // return 0;
    // }
    // case Attribute.NUMERIC:
    //
    // // If attribute is numeric
    // if (Instance.isMissingValue(val1) || Instance.isMissingValue(val2)) {
    // if (Instance.isMissingValue(val1) && Instance.isMissingValue(val2)) {
    // return 1;
    // } else {
    // double diff;
    // if (Instance.isMissingValue(val2)) {
    // diff = norm(val1, index);
    // } else {
    // diff = norm(val2, index);
    // }
    // if (diff < 0.5) {
    // diff = 1.0 - diff;
    // }
    // return diff;
    // }
    // } else {
    // return norm(val1, index) - norm(val2, index);
    // }
    // default:
    // return 0;
    // }
    // }

    // /**
    // * Normalizes a given value of a numeric attribute.
    // *
    // * @param x
    // * the value to be normalized
    // * @param i
    // * the attribute's index
    // * @return the normalized value
    // */
    // private double norm(double x, int i) {
    //
    // if (Double.isNaN(m_Min[i]) || Utils.eq(m_Max[i], m_Min[i])) {
    // return 0;
    // } else {
    // return (x - m_Min[i]) / (m_Max[i] - m_Min[i]);
    // }
    // }

    // /**
    // * return a string describing this clusterer
    // *
    // * @return a description of the clusterer as a string
    // */
    // public String toString() {
    // StringBuffer temp = new StringBuffer();
    //
    // temp.append("\n FarthestFirst\n==============\n");
    //
    // temp.append("\nCluster centroids:\n");
    // for (int i = 0; i < m_NumClusters; i++) {
    // temp.append("\nCluster " + i + "\n\t");
    // for (int j = 0; j < centroids.numAttributes(); j++) {
    // if (centroids.attribute(j).isNominal()) {
    // temp.append(" "
    // + centroids.attribute(j).value((int) centroids.instance(i).value(j)));
    // } else {
    // temp.append(" " + centroids.instance(i).value(j));
    // }
    // }
    // }
    // temp.append("\n\n");
    // return temp.toString();
    // }

    /**
     * XXX doc
     */
    private Random rg;

    /**
     * XXX doc
     * 
     * @param numClusters
     * @param rg
     */
    public FarthestFirst(int numClusters, DistanceMeasure dm, Random rg) {
        super();
        m_NumClusters = numClusters;
        this.dm = dm;
        this.rg = rg;
    }

    public Dataset[] executeClustering(Dataset data) {
        this.data = data;
        centroids = new Instance[m_NumClusters];

        int n = data.size();// .numInstances();
        boolean[] selected = new boolean[n];
        double[] minDistance = new double[n];

        for (int i = 0; i < n; i++)
            minDistance[i] = Double.MAX_VALUE;

        int firstI = rg.nextInt(n);
        centroids[0] = data.getInstance(firstI);
        selected[firstI] = true;

        updateMinDistance(minDistance, selected, data.getInstance(firstI));

        if (m_NumClusters > n)
            m_NumClusters = n;

        for (int i = 1; i < m_NumClusters; i++) {
            int nextI = farthestAway(minDistance, selected);
            centroids[i] = data.getInstance(nextI);
            selected[nextI] = true;
            updateMinDistance(minDistance, selected, data.getInstance(nextI));
        }
        Dataset[] clusters = new Dataset[m_NumClusters];
        for (int i = 0; i < m_NumClusters; i++) {
            clusters[i] = new SimpleDataset();
        }
        for (int i = 0; i < data.size(); i++) {
            Instance inst = data.getInstance(i);
            double min = dm.calculateDistance(inst, centroids[0]);
            int index = 0;
            for (int j = 1; j < m_NumClusters; j++) {
                double tmp = dm.calculateDistance(inst, centroids[j]);
                if (tmp < min) {
                    min = tmp;
                    index = j;
                }
            }
            clusters[index].addInstance(inst);
        }
        return clusters;
        // data = new Instances(data, 0);
        // long end = System.currentTimeMillis();
        // System.out.println("Clustering Time = " + (end-start));
    }
}
