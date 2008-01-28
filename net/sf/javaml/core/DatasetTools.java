/**
 * DatasetTools.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.filter.ClassRemoveFilter;
import net.sf.javaml.filter.ClassRetainFilter;

import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

/**
 * This class provides utility methods on datasets.
 * 
 * {@jmlSource}
 * 
 * @see Dataset
 * @see SimpleDataset
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
final public class DatasetTools {

    /**
     * Returns the instance of the given data set that is closest to the
     * instance that is given as a parameter.
     * 
     * @param data
     *            the data set to search in
     * @param dm
     *            the distance measure used to calculate the distance between
     *            instances
     * @param inst
     *            the instance for which we need to find the closest
     * @return the instance from the supplied data set that is closest to the
     *         supplied instance
     * 
     */
    public static Instance getNearest(Dataset data, DistanceMeasure dm, Instance inst) {
        Instance closest = data.instance(0);
        double bestDistance = dm.calculateDistance(inst, closest);
        for (int i = 1; i < data.size(); i++) {
            double tmpDistance = dm.calculateDistance(inst, data.instance(i));
            if (dm.compare(tmpDistance, bestDistance)) {
                bestDistance = tmpDistance;
                closest = data.instance(i);
            }
        }
        return closest;
    }

    /**
     * Returns the k instances of the given data set that are the closest to the
     * instance that is given as a parameter.
     * 
     * @param data
     *            the data set to search in
     * @param dm
     *            the distance measure used to calculate the distance between
     *            instances
     * @param inst
     *            the instance for which we need to find the closest
     * @return the instances from the supplied data set that are closest to the
     *         supplied instance
     * 
     */
    public static List<Instance> getNearestK(Dataset data, DistanceMeasure dm, Instance inst, int k) {
        Vector<Instance> closest = new Vector<Instance>();
        // double bestDistance = dm.calculateDistance(inst, closest);
        for (Instance tmp : data) {
            if (!inst.equals(tmp)) {
                closest.add(tmp);
                if (closest.size() > k)
                    removeFarthest(closest, inst, dm);
            }
            // double tmpDistance = dm.calculateDistance(inst,
            // data.instance(i));
            // if (dm.compare(tmpDistance, bestDistance)) {
            // bestDistance = tmpDistance;
            // closest = data.instance(i);
            // }
        }
        return closest;
    }

    /*
     * Removes the element from the vector that is farthest from the supplied
     * element.
     */
    private static void removeFarthest(Vector<Instance> vector, Instance supplied, DistanceMeasure dist) {
        Instance tmp = null;// ; = vector.get(0);
        double max = 0;// dist.calculateDistance(vector.get(0), supplied);
        for (Instance inst : vector) {
            double tmpDist = dist.calculateDistance(inst, supplied);
            if (dist.compare(max, tmpDist)) {
                max = tmpDist;
                tmp = inst;
            }
        }

        if (!vector.remove(tmp)) {
            System.out.println(tmp);
            throw new RuntimeException("This should not happen...");
        }

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
        int numAttributes = data.instance(0).size();
        double[] stdValues = new double[numAttributes];
        double[] attr = new double[data.size()];
        for (int i = 0; i < numAttributes; i++) {
            StandardDeviation std = new StandardDeviation();
            for (int j = 0; j < data.size(); j++) {
                attr[j] = data.instance(j).value(i);
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
        int instanceLength = data.instance(0).size();
        double[] sumPosition = new double[instanceLength];
        for (int i = 0; i < data.size(); i++) {
            Instance in = data.instance(i);
            for (int j = 0; j < instanceLength; j++) {

                sumPosition[j] += in.weight() * in.value(j);

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
            Instance tmp = data.instance(i);
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
                out.add(data.instance(i));
        }
        return out;

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

    /**
     * Create a random sample from the data set. Sampling is done with
     * replacement.
     * 
     * @param data
     *            the data set to sample
     * @param size
     *            the number of instances in the output data set
     * @return a random sample from the input data
     */
    public static Dataset randomSample(Dataset data, int size) {
        Random rg = new Random();
        Dataset out = new SimpleDataset();
        while (out.size() < size) {
            out.add(data.instance(rg.nextInt(data.size())).copy());
        }
        return out;

    }

    /**
     * Create folds from the supplied data set for cross-validation
     * 
     * @param data
     *            the data set to make folds from
     * @param positiveClass
     *            the positive class
     * @param numFolds
     *            the number of folds to create
     * @return
     */
    public static Dataset[] createFolds(Dataset data, int positiveClass, int numFolds) {
        ClassRetainFilter retain = new ClassRetainFilter(positiveClass);
        ClassRemoveFilter remove = new ClassRemoveFilter(positiveClass);
        Dataset positive = retain.filterDataset(data);
        Dataset negative = remove.filterDataset(data);
        Dataset[] out = new Dataset[numFolds];
        for (int i = 0; i < out.length; i++)
            out[i] = new SimpleDataset();
        for (int i = 0; i < positive.size(); i++)
            out[i % numFolds].add(positive.instance(i));
        for (int i = 0; i < negative.size(); i++)
            out[i % numFolds].add(negative.instance(i));
        return out;

    }

    /**
     * Create a random slice from the data set. This is basically sampling
     * without replacement.
     * 
     * @param data
     *            the data set to sample
     * @param size
     *            the percentage of instances in the output data set
     * @return a random sample from the input data
     */
    public static Dataset randomSlice(Dataset data, double d, Random rg) {
        Dataset out = data.copy();
        while (out.size() > d * data.size()) {
            out.remove(rg.nextInt(out.size()));
        }
        return out;

    }

}
