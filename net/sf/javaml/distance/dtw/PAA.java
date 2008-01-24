/**
 * PAA.java
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
 * Copyright (c) 2004 Stan Salvador
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 * Based on work by Stan Salvador and Philip Chan.
 * 
 */
package net.sf.javaml.distance.dtw;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.WrapperInstance;

final class PAA {

    private double[] newPoints;

    PAA(Instance ts, int shrunkSize) {
        if (shrunkSize > ts.size())
            throw new InternalError(
                    "ERROR:  The size of an aggregate representation may not be larger than the \noriginal time series (shrunkSize="
                            + shrunkSize + " , origSize=" + ts.size() + ").");
        if (shrunkSize <= 0)
            throw new InternalError(
                    "ERROR:  The size of an aggregate representation must be greater than zero and \nno larger than the original time series.");
        originalLength = ts.size();
        aggPtSize = new int[shrunkSize];
        newPoints = new double[shrunkSize];

        double reducedPtSize = (double) ts.size() / (double) shrunkSize;
        int ptToReadTo;
        int index = 0;
        for (int ptToReadFrom = 0; ptToReadFrom < ts.size(); ptToReadFrom = ptToReadTo + 1) {
            ptToReadTo = (int) Math.round(reducedPtSize * (double) (index + 1)) - 1;
            int ptsToRead = (ptToReadTo - ptToReadFrom) + 1;
            double timeSum = 0.0D;
            double measurementSums = 0;
            for (int pt = ptToReadFrom; pt <= ptToReadTo; pt++) {
                measurementSums += ts.value(pt);
                // timeSum += ts.getTimeAtNthPoint(pt);
                // for(int dim = 0; dim < ts.numOfDimensions(); dim++)
                // measurementSums[dim] += currentPoint[dim];

            }

            timeSum /= ptsToRead;
            // for(int dim = 0; dim < ts.numOfDimensions(); dim++)
            measurementSums /= (double) ptsToRead;

            aggPtSize[index] = ptsToRead;
            newPoints[index] = measurementSums;
            index++;
        }

    }

    int originalSize() {
        return originalLength;
    }

    int aggregatePtSize(int ptIndex) {
        return aggPtSize[ptIndex];
    }

    public String toString() {
        return "(" + originalLength + " point time series represented as " + newPoints.length + " points)\n"
                + super.toString();
    }

    private int aggPtSize[];

    private final int originalLength;

    Instance getNewInstance() {
        return new WrapperInstance(newPoints);
    }
}