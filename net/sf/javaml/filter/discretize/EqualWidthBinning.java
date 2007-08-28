/**
 * FixedBinning.java
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

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * A filter that discretizes a range of numeric attributes in the dataset into
 * nominal attributes. Discretization is done by binning.
 * 
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author Thomas Abeel
 */
public class EqualWidthBinning extends AbstractBinning {

    public EqualWidthBinning() {
        super();
    }

    public EqualWidthBinning(int[] binnedAttributes) {
        super(binnedAttributes);
    }

    public EqualWidthBinning(int[] binnedAttributes, int numBins) {
        super(binnedAttributes, numBins);
    }

    /**
     * Calculate cutpoints for a single attribute.
     * 
     * @param index
     *            the index of the attribute to calculate cutpoints for
     */
    protected double[] calculateBorderPoints(Dataset data, int index) {

        // Scan for max and min values
        // double max = 0, min = 1, currentVal;
        Instance minInstance = data.getMinimumInstance();
        Instance maxInstance = data.getMaximumInstance();

        double width = (maxInstance.getValue(index) - minInstance.getValue(index)) / numBins;
        double[] out = null;
        if ((numBins > 1) && (width > 0)) {
            out = new double[numBins - 1];
            for (int i = 1; i < numBins; i++) {
                out[i - 1] = minInstance.getValue(index) + width * i;
            }
        }
        return out;
    }
}
