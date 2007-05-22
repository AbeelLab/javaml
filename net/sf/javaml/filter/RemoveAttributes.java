/**
 * RemoveAttributes.java
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
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import nz.ac.waikato.cs.weka.Utils;

public class RemoveAttributes implements Filter {
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
        int max = indices[Utils.maxIndex(indices)];
        binIndices = new boolean[max + 1];
        count = 0;
        for (int i = 0; i < indices.length; i++) {
            if (!binIndices[indices[i]])
                count++;
            binIndices[indices[i]] = true;
        }
    }

    public Dataset filterDataset(Dataset data) {
        Dataset out = new SimpleDataset();
        for (int i = 0; i < data.size(); i++) {
            out.addInstance(filterInstance(data.getInstance(i)));
        }
        return out;
    }

    public Instance filterInstance(Instance instance) {
        float[] newVals = new float[instance.size() - count];
        int index = 0;
        for (int i = 0; i < instance.size(); i++) {
            if (i>=binIndices.length||!binIndices[i]) {
                newVals[index] = instance.getValue(i);
                index++;
            }
        }
        return new SimpleInstance(newVals, instance.getWeight(), instance.isClassSet(), instance.getClassValue());
    }

    public Instance unfilterInstance(Instance instance) {
        throw new RuntimeException("RemoveAttributes is a one-way filter.");
    }

}
