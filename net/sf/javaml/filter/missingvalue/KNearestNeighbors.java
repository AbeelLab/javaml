/**
 * KNearestNeighbors.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.missingvalue;

import java.util.Set;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.InstanceTools;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.filter.DatasetFilter;

/**
 * Replaces the missing value with the average of the values of its nearest
 * neighbors.
 * 
 * This technique does not guarantee that all missing will be replaced. If all
 * neighbors also have the same missing attributes, it is impossible to replace
 * the original value.
 * 
 * {@jmlSource}
 * 
 * @author Thomas Abeel
 * 
 */
public class KNearestNeighbors implements DatasetFilter {

    private int k = 5;

    public void setK(int k) {
        this.k = k;
    }

    public void build(Dataset data) {
        // do nothing

    }

    private DistanceMeasure euc = new EuclideanDistance();

    public void filterDataset(Dataset data) {
        // Dataset output = new SimpleDataset();
        for (Instance i : data) {
            removeMissingValues(i, data);
            // output.add(x);
        }
        // return output;
    }

    private void removeMissingValues(Instance inst, Dataset data) {
        if (InstanceTools.hasMissingValues(inst)) {
            Set<Instance> nearest = data.kNearest(k, euc, inst);

            Instance sum = new DenseInstance(new double[inst.noAttributes()]);
            for (Instance x : data.kNearest(k, euc, inst)) {
                sum = sum.plus(x);
            }
            sum = sum.divide(nearest.size());

            for (int i = 0; i < inst.noAttributes(); i++) {
                if (Double.isNaN(inst.value(i))) {
                    inst.put(i, sum.value(i));
                    if (Double.isNaN(inst.value(i))) {
                        // TODO Should be done better
                        System.err.println("Still missing values present in attribute " + i);
                    }
                }
            }

        }
    }

}
