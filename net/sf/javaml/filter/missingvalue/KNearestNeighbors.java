/**
 * KNearestNeighbors.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.missingvalue;

import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.InstanceTools;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
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
 * @{jmlSource}
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

    public Dataset filterDataset(Dataset data) {
        Dataset output = new SimpleDataset();
        for (Instance i : data) {
            Instance x = removeMissingValues(i, data);
            output.add(x);
        }
        return output;
    }

    private Instance removeMissingValues(Instance inst, Dataset data) {
        if (InstanceTools.hasMissingValues(inst)) {
            Vector<Instance> nk = (Vector<Instance>) DatasetTools.getNearestK(data, euc, inst, k);
            double[] values = new double[inst.size()];
            for (int i = 0; i < inst.size(); i++) {
                if (Double.isNaN(inst.value(i))) {

                    double sum = 0;
                    int count = 0;
                    for (Instance nn : nk) {
                        if (!Double.isNaN(nn.value(i))) {
                            sum += nn.value(i);
                            count++;
                        }
                    }
                    values[i] = sum / count;
                    if (Double.isNaN(values[i])) {
                        //TODO Should be done better
                        System.err.println("Still missing values present in attribute " + i);
                    }
                } else {
                    values[i] = inst.value(i);
                }
            }
            return new SimpleInstance(values, inst);
        } else
            return inst;
    }

    
}
