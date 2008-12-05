/**
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

import java.util.Random;

import org.apache.commons.math.stat.StatUtils;

/**
 * This class provides utility methods on data sets.
 * 
 * {@jmlSource}
 * 
 * @see Dataset
 * @see DefaultDataset
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
final public class DatasetTools {

    /**
     * All data will be merged together in the first supplied data set.
     * 
     * @param datasets
     *            a number of data sets
     * 
     */
    public static void merge(Dataset... datasets) {
        Dataset out = null;
        for (Dataset data : datasets) {
            if (out == null)
                out = data;
            else
                out.addAll(data);
        }
    }

    /**
     * Generate a bootstrap sample from the data set with a particular size,
     * using the given random generator.
     * 
     * This is done by sampling with replacement.
     */
    public static Dataset bootstrap(Dataset data, int size, Random rg) {
        Dataset out = new DefaultDataset();
        while (out.size() < size) {
            out.add(data.instance(rg.nextInt(data.size())).copy());
        }
        return out;
    }

    /**
     * Create an instance that contains all the maximum values for the
     * attributes.
     * 
     * @param data
     * @return
     */
    public static Instance maxAttributes(Dataset data) {
        Instance max = new SparseInstance();
        for (Instance i : data) {
            for (Integer index : i.keySet()) {
                double val = i.value(index);
                if (!max.containsKey(index))
                    max.put(index, val);
                else if (max.get(index) < val)
                    max.put(index, val);

            }

        }
        return max;
    }

    public static Instance minAttributes(Dataset data) {
        Instance min = new SparseInstance();
        for (Instance i : data) {
            for (Integer index : i.keySet()) {
                double val = i.value(index);
                if (!min.containsKey(index))
                    min.put(index, val);
                else if (min.get(index) > val)
                    min.put(index, val);
            }
        }
        return min;
    }

    public static Instance standardDeviation(Dataset data, Instance avg) {
        Instance sum = new DenseInstance(new double[avg.noAttributes()]);
        for (Instance i : data) {
            Instance diff = i.minus(avg);
            sum = sum.plus(diff.multiply(diff));
        }
        sum = sum.divide(data.size());
        return sum.sqrt();

    }

    public static Instance average(Dataset data) {
        Instance max = new SparseInstance();
        Instance min = new SparseInstance();
        for (Instance i : data) {
            for (Integer index : i.keySet()) {
                double val = i.value(index);
                if (!max.containsKey(index))
                    max.put(index, val);
                else if (max.get(index) < val)
                    max.put(index, val);
                if (!min.containsKey(index))
                    min.put(index, val);
                else if (min.get(index) > val)
                    min.put(index, val);
            }

        }
        return max.plus(min).divide(2);
    }

    /**
     * Calculates the percentile hinge for a given percentile.
     * 
     * @param data
     *            data set to calculate percentile for
     * @param perc
     *            percentile to calculate, Q1=0.25, Q2=median=0.50,Q3=0.75
     * @return
     */
    public static Instance percentile(Dataset data, double perc) {
        double[] tmpOut = new double[data.noAttributes()];
        for (int i = 0; i < data.noAttributes(); i++) {
            double[] vals = new double[data.size()];
            for (int j = 0; j < data.size(); j++) {
                vals[j] = data.get(j).value(i);
            }
            tmpOut[i] = StatUtils.percentile(vals, perc);

        }
        return new DenseInstance(tmpOut);
    }

   

}
