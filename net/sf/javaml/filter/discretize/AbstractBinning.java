/**
 * Binning.java
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
 * Copyright (c) 1999 University of Waikato, Hamilton, New Zealand
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.filter.discretize;

import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.Filter;

/**
 * A filter that discretizes a range of numeric attributes in the dataset into
 * nominal attributes. Discretization is done by binning.
 * 
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author Thomas Abeel
 */
public abstract class AbstractBinning implements Filter {
    /**
     * The number of bins
     */
    protected int numBins = 10;

    /**
     * The border points for the bins
     */
    private double[][] borderPoints = null;

    /**
     * Attributes to discretize
     */
    private Vector<Integer> binnedAttributes = null;

    protected AbstractBinning() {
        this.numBins = 10;
    }

    protected AbstractBinning(int[] binnedAttributes) {
        this(binnedAttributes, 10);
    }

    protected AbstractBinning(int[] binnedAttributes, int bins) {
        this.numBins = bins;
        this.binnedAttributes = new Vector<Integer>();
        for (Integer i : binnedAttributes) {
            this.binnedAttributes.add(i);
        }
    }

    /** Generate the cutpoints for each attribute */
    private void calculateBorderPoints(Dataset data) {
        borderPoints = new double[binnedAttributes.size()][];
        for (int i = 0; i < binnedAttributes.size(); i++) {
            borderPoints[i] = calculateBorderPoints(data, binnedAttributes.get(i));
        }

    }

    protected abstract double[] calculateBorderPoints(Dataset data, int index);

    public Dataset filterDataset(Dataset data) {
        if (binnedAttributes == null) {
            binnedAttributes = new Vector<Integer>();
            for (int i = 0; i < data.getInstance(0).size(); i++) {
                binnedAttributes.add(i);
            }
        }
        calculateBorderPoints(data);
        Dataset out = new SimpleDataset();
        for (Instance i : data) {
            out.addInstance(filterInstance(i));
        }
        return out;
    }

    public Instance filterInstance(Instance instance) {
        int index = 0;
        double[] vals = new double[instance.size()];
        for (int i = 0; i < instance.size(); i++) {
            if (binnedAttributes.contains(i)) {

                double currentVal = instance.getValue(i);
                if (borderPoints[binnedAttributes.indexOf(i)] == null) {

                    vals[index] = 0;

                    index++;
                } else {

                    int j = 0;
                    while (j < borderPoints[binnedAttributes.indexOf(i)].length
                            && currentVal > borderPoints[binnedAttributes.indexOf(i)][j])
                        j++;

                    vals[index] = j;

                    index++;

                }
            } else {
                vals[index] = instance.getValue(i);
                index++;
            }
        }

        return new SimpleInstance(vals, instance);

    }

    public Instance unfilterInstance(Instance instance) {
        throw new UnsupportedOperationException("This method is not available for this filter");
    }

}