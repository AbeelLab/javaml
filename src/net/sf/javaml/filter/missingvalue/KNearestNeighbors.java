/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.missingvalue;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.filter.DatasetFilter;
import net.sf.javaml.tools.InstanceTools;
import net.sf.javaml.utils.ArrayUtils;

/**
 * Replaces the missing value with the average of the values of its nearest
 * neighbors.
 * 
 * This technique does not guarantee that all missing will be replaced. If all
 * neighbors also have the same missing attributes, it is impossible to replace
 * the original value.
 * 
 * 
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

    public void filter(Dataset data) {
        for (Instance i : data) {
            removeMissingValues(i, data);
        }
    }

    private void removeMissingValues(Instance inst, Dataset data) {
        if (InstanceTools.hasMissingValues(inst)) {
            double[] sum = new double[inst.noAttributes()];
            double[] count = new double[inst.noAttributes()];
            for (Instance x : data.kNearest(k, inst, new EuclideanDistance())) {
                for (int i = 0; i < x.noAttributes(); i++) {
                    if (!Double.isNaN(x.value(i))) {
                        sum[i] += x.value(i);
                        count[i]++;
                    }

                }
            }
            sum = ArrayUtils.divide(sum, count);

            for (int i = 0; i < inst.noAttributes(); i++) {
                if (Double.isNaN(inst.value(i)) && count[i] != 0) {
                    inst.put(i, sum[i]);
                }
            }

        }
    }
}
