/**
 * DatasetTools.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import net.sf.javaml.distance.DistanceMeasure;

/**
 * This class provides utility methods on datasets.
 * 
 * {@jmlSource}
 * 
 * @see Dataset
 * @see SimpleDataset
 * 
 * @author Thomas Abeel
 * 
 */
final public class DatasetTools {

    /**
     * Returns the instance of the given dataset that is closest to the instance
     * that is given as a parameter.
     * 
     * @param data
     *            the dataset to search in
     * @param dm
     *            the distance measure used to calculate the distance between
     *            instances
     * @param inst
     *            the instance for which we need to find the closest
     * @return the instance from the supplied dataset that is closest to the
     *         supplied instance
     * 
     */
    public static Instance getClosest(Dataset data, DistanceMeasure dm, Instance inst) {
        Instance closest = data.getInstance(0);
        double bestDistance = dm.calculateDistance(inst, closest);
        for (int i = 1; i < data.size(); i++) {
            double tmpDistance = dm.calculateDistance(inst, data.getInstance(i));
            if (dm.compare(tmpDistance, bestDistance)) {
                bestDistance = tmpDistance;
                closest = data.getInstance(i);
            }
        }
        return closest;
    }

    /**
     * Calculate the standard deviation of all attributes for all instances in a
     * dataset. The result is returned as an instance with as value for each
     * attribute the standard deviation of the values of that attribute in the
     * instances from the dataset.
     * 
     * @param data
     *            the dataset for which you want to calculate the standard
     *            deviation
     * @return an instance that contains the standard deviations of all
     *         attribute values
     */
    public static Instance getStandardDeviation(Dataset data) {
        int numAttributes = data.getInstance(0).size();
        double[] stdValues = new double[numAttributes];
        double[] attr = new double[data.size()];
        for (int i = 0; i < numAttributes; i++) {
            StandardDeviation std = new StandardDeviation();
            for (int j = 0; j < data.size(); j++) {
                attr[j] = data.getInstance(j).getValue(i);
            }
            stdValues[i] = std.evaluate(attr);
        }
        return new SimpleInstance(stdValues);

    }

    /**
     * Calculates the centroid of a dataset. A centroid is the middle or the
     * average of a dataset.
     * 
     * @param data
     *            the dataset to calculate the centroid of
     * 
     * @return the centroid of this dataset
     */
    public static Instance getCentroid(Dataset data) {
        if (data.size() == 0)
            return null;
        int instanceLength = data.getInstance(0).size();
        double[] sumPosition = new double[instanceLength];
        for (int i = 0; i < data.size(); i++) {
            Instance in = data.getInstance(i);
            for (int j = 0; j < instanceLength; j++) {

                sumPosition[j] += in.getWeight() * in.getValue(j);

            }

        }
        for (int j = 0; j < instanceLength; j++) {
            sumPosition[j] /= data.size();
        }
        return new SimpleInstance(sumPosition);

    }

    /**
     * Performs an epsilon range query for this instance relative to the
     * supplied dataset.
     * 
     * @param data
     *            the dataset on which to run the query.
     * @param epsilon
     *            the range for the query
     * @param instance
     *            the instance that is used as query-object for the epsilon
     *            range query
     * @param dm
     *            The distance measure used to calculated the distances.
     * @return a list with all the instances that are within the specified range
     */
    public static List<Instance> epsilonRangeQuery(Dataset data, double epsilon, Instance instance, DistanceMeasure dm) {
        ArrayList<Instance> epsilonRange_List = new ArrayList<Instance>();

        for (int i = 0; i < data.size(); i++) {
            Instance tmp = data.getInstance(i);
            double distance = dm.calculateDistance(tmp, instance);
            if (distance < epsilon) {
                epsilonRange_List.add(tmp);
            }
        }

        return epsilonRange_List;
    }

    /**
     * Removes an instance from a dataset and returns the resulting dataset.
     * 
     * @warning This method does actually construct a new dataset with all
     *          instances that are not removed.
     * @param data
     *            the dataset from which to remove an instance
     * @param index
     *            the index of the instance to remove
     * @return the new dataset without the instance to remove
     */
    public static Dataset removeInstance(Dataset data, int index) {
        Dataset out = new SimpleDataset();
        for (int i = 0; i < data.size(); i++) {
            if (i != index)
                out.addInstance(data.getInstance(i));
        }
        return out;

    }

    /**
     * Merges all the data from the two datasets into the first one.
     * 
     * @param data
     *            the first dataset
     * @param added
     *            the dataset to add to the first one
     */
    public static void merge(Dataset data, Dataset added) {
        for (int i = 0; i < added.size(); i++) {
            data.addInstance(added.getInstance(i));
        }
    }

    /**
     * Removes all the empty datasets from an array of datasets.
     * 
     * @param input
     *            the array with all datasets
     * @return an array with only datasets that have at least size 1
     */
    public static Dataset[] filterEmpty(Dataset[] input) {
        int nonEmptyClusterCount = 0;
        for (int i = 0; i < input.length; i++) {
            if (input[i].size() > 0)
                nonEmptyClusterCount++;
        }
        Dataset[] output = new Dataset[nonEmptyClusterCount];
        int index = 0;
        for (Dataset tmp : input) {
            if (tmp.size() > 0) {
                output[index] = tmp;
                index++;
            }
        }
        return output;
    }

}
