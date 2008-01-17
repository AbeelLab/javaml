/**
 * RemoveAttributes.java
 *
 * %SVN.HEADER%
 * 
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleInstance;
import nz.ac.waikato.cs.weka.Utils;

public class RemoveAttributes extends AbstractFilter {
    /**
     * The indices to remove.
     */
    private boolean[] binIndices;

    /**
     * Number of unique indices to remove
     */
    private int count;

    /**
     * Construct a remove filter that removes all the attributes with the
     * indices given in the array as parameter.
     * 
     * @param indices
     *            the indices of the columns that will be removed.
     */
    public RemoveAttributes(int[] indices) {
        if (indices.length > 0) {
            int max = indices[Utils.maxIndex(indices)];
            binIndices = new boolean[max + 1];
            count = 0;
            for (int i = 0; i < indices.length; i++) {
                if (!binIndices[indices[i]])
                    count++;
                binIndices[indices[i]] = true;
            }
        } else {
            binIndices = new boolean[0];
        }
    }

    public Dataset filterDataset(Dataset data) {
        return FilterUtils.applyFilter(this, data);
    }

    public Instance filterInstance(Instance instance) {
        double[] newVals = new double[instance.size() - count];
        int index = 0;
        for (int i = 0; i < instance.size(); i++) {
            if (i >= binIndices.length || !binIndices[i]) {
                newVals[index] = instance.getValue(i);
                index++;
            }
        }
        return new SimpleInstance(newVals, instance);
    }

    public Instance unfilterInstance(Instance instance) {
        throw new UnsupportedOperationException("RemoveAttributes is a one-way filter.");
    }

    public Dataset unfilterDataset(Dataset data) {
        throw new UnsupportedOperationException("RemoveAttributes is a one-way filter.");
    }

}
