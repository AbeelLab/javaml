/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.dtw;

import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

final class PAA {

    private double[] newPoints;

    PAA(Instance ts, int shrunkSize) {
        if (shrunkSize > ts.noAttributes())
            throw new InternalError(
                    "ERROR:  The size of an aggregate representation may not be larger than the \noriginal time series (shrunkSize="
                            + shrunkSize + " , origSize=" + ts.noAttributes() + ").");
        if (shrunkSize <= 0)
            throw new InternalError(
                    "ERROR:  The size of an aggregate representation must be greater than zero and \nno larger than the original time series.");
        originalLength = ts.noAttributes();
        aggPtSize = new int[shrunkSize];
        newPoints = new double[shrunkSize];

        double reducedPtSize = (double) ts.noAttributes() / (double) shrunkSize;
        int ptToReadTo;
        int index = 0;
        for (int ptToReadFrom = 0; ptToReadFrom < ts.noAttributes(); ptToReadFrom = ptToReadTo + 1) {
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
        return new DenseInstance(newPoints);
    }
}
