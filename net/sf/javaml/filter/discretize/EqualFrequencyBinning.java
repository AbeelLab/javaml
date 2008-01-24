/**
 * EqualFrequencyBinning.java
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

/**
 * A filter that discretizes a range of numeric attributes in the data set into
 * nominal attributes. Discretization is done by binning.
 * 
 * @author Len Trigg 
 * @author Eibe Frank
 * @author Thomas Abeel
 */
public class EqualFrequencyBinning extends AbstractBinning {

    public EqualFrequencyBinning() {
        super();
    }

    public EqualFrequencyBinning(int[] binnedAttributes) {
        super(binnedAttributes);

    }

    public EqualFrequencyBinning(int[] binnedAttributes, int bins) {
        super(binnedAttributes);
        this.numBins = bins;
    }

    public EqualFrequencyBinning(int[] binnedAttributes, double weight) {
        super(binnedAttributes);
        this.m_DesiredWeightOfInstancesPerInterval = weight;
    }

    /** The desired weight of instances per bin */
    protected double m_DesiredWeightOfInstancesPerInterval = -1;

    /**
     * Calculate cutpoints for a single attribute.
     * 
     * @param index
     *            the index of the attribute to set cutpoints for
     */
    @Override
    protected double[] calculateBorderPoints(Dataset data, int index) {

        // Sort input data
        data.sort(index);

        // Compute weight of instances without missing values
        double sumOfWeights = 0;
        for (int i = 0; i < data.size(); i++) {

            sumOfWeights += data.instance(i).weight();

        }
        double freq;
        double[] cutPoints = new double[numBins - 1];
        if (m_DesiredWeightOfInstancesPerInterval > 0) {
            freq = m_DesiredWeightOfInstancesPerInterval;
            cutPoints = new double[(int) (sumOfWeights / freq)];
        } else {
            freq = sumOfWeights / numBins;
            cutPoints = new double[numBins - 1];
        }

        // Compute break points
        double counter = 0, last = 0;
        int cpindex = 0, lastIndex = -1;
        for (int i = 0; i < data.size() - 1; i++) {

            counter += data.instance(i).weight();
            sumOfWeights -= data.instance(i).weight();

            // Do we have a potential breakpoint?
            if (data.instance(i).value(index) < data.instance(i + 1).value(index)) {

                // Have we passed the ideal size?
                if (counter >= freq) {

                    // Is this break point worse than the last one?
                    if (((freq - last) < (counter - freq)) && (lastIndex != -1)) {
                        cutPoints[cpindex] = (data.instance(lastIndex).value(index) + data.instance(
                                lastIndex + 1).value(index)) / 2;
                        counter -= last;
                        last = counter;
                        lastIndex = i;
                    } else {
                        cutPoints[cpindex] = (data.instance(i).value(index) + data.instance(i + 1).value(
                                index)) / 2;
                        counter = 0;
                        last = 0;
                        lastIndex = -1;
                    }
                    cpindex++;
                    freq = (sumOfWeights + counter) / ((cutPoints.length + 1) - cpindex);
                } else {
                    lastIndex = i;
                    last = counter;
                }
            }
        }

        // Check whether there was another possibility for a cut point
        if ((cpindex < cutPoints.length) && (lastIndex != -1)) {
            cutPoints[cpindex] = (data.instance(lastIndex).value(index) + data.instance(lastIndex + 1)
                    .value(index)) / 2;
            cpindex++;
        }

        // Did we find any cutpoints?
        if (cpindex == 0) {
            return null;
        } else {
            double[] cp = new double[cpindex];
            System.arraycopy(cutPoints, 0, cp, 0, cpindex);
            // for (int i = 0; i < cpindex; i++) {
            // cp[i] = cutPoints[i];
            // }
            //            
            return cp;
        }
    }

}
