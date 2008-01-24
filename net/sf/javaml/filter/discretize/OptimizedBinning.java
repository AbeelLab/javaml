/**
 * OptimizedBinning.java
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
public class OptimizedBinning extends AbstractBinning {

    public OptimizedBinning() {
        super();
    }

    public OptimizedBinning(int[] binnedAttributes) {
        super(binnedAttributes);
    }

    /**
     * Optimizes the number of bins using leave-one-out cross-validation.
     * 
     * @param index
     *            the attribute index
     */
    @Override
    protected double[] calculateBorderPoints(Dataset data, int index) {

        double binWidth = 0, entropy, bestEntropy = Double.MAX_VALUE;
        double[] distribution;
        int bestNumBins = 1;

        // Min and max
        Instance minInstance = data.getMinimumInstance();
        Instance maxInstance = data.getMaximumInstance();

        // Find best number of bins
        for (int i = 0; i < numBins; i++) {
            distribution = new double[i + 1];
            binWidth = (maxInstance.value(index) - minInstance.value(index)) / (i + 1);

            // Compute distribution
            for (int j = 0; j < data.size(); j++) {
                Instance currentInstance = data.instance(j);
                for (int k = 0; k < i + 1; k++) {
                    if (currentInstance.value(index) <= (minInstance.value(index) + (((double) k + 1) * binWidth))) {
                        distribution[k] += currentInstance.weight();
                        break;
                    }
                }

            }

            // Compute cross-validated entropy
            entropy = 0;
            for (int k = 0; k < i + 1; k++) {
                if (distribution[k] < 2) {
                    entropy = Double.MAX_VALUE;
                    break;
                }
                entropy -= distribution[k] * Math.log((distribution[k] - 1) / binWidth);
            }

            // Best entropy so far?
            if (entropy < bestEntropy) {
                bestEntropy = entropy;
                bestNumBins = i + 1;
            }
        }

        // Compute cut points
        double[] cutPoints = null;
        if ((bestNumBins > 1) && (binWidth > 0)) {
            cutPoints = new double[bestNumBins - 1];
            for (int i = 1; i < bestNumBins; i++) {
                cutPoints[i - 1] = minInstance.value(index) + binWidth * i;
            }
        }
        return cutPoints;
    }

}
