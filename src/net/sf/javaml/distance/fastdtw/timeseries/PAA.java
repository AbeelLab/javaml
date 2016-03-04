/**
 * %SVN.HEADER%
 */

package net.sf.javaml.distance.fastdtw.timeseries;

// Referenced classes of package timeseries:
//            TimeSeries, TimeSeriesPoint

public class PAA extends TimeSeries {

    public PAA(TimeSeries ts, int shrunkSize) {
        if (shrunkSize > ts.size())
            throw new InternalError(
                    "ERROR:  The size of an aggregate representation may not be largerr than the \noriginal time series (shrunkSize="
                            + shrunkSize + " , origSize=" + ts.size() + ").");
        if (shrunkSize <= 0)
            throw new InternalError(
                    "ERROR:  The size of an aggregate representation must be greater than zero and \nno larger than the original time series.");
        originalLength = ts.size();
        aggPtSize = new int[shrunkSize];
        super.setMaxCapacity(shrunkSize);
        setLabels(ts.getLabels());
        double reducedPtSize = (double) ts.size() / (double) shrunkSize;
        int ptToReadTo;
        for (int ptToReadFrom = 0; ptToReadFrom < ts.size(); ptToReadFrom = ptToReadTo + 1) {
            ptToReadTo = (int) Math.round(reducedPtSize * (double) (size() + 1)) - 1;
            int ptsToRead = (ptToReadTo - ptToReadFrom) + 1;
            double timeSum = 0.0D;
            double measurementSums[] = new double[ts.numOfDimensions()];
            for (int pt = ptToReadFrom; pt <= ptToReadTo; pt++) {
                double currentPoint[] = ts.getMeasurementVector(pt);
                timeSum += ts.getTimeAtNthPoint(pt);
                for (int dim = 0; dim < ts.numOfDimensions(); dim++)
                    measurementSums[dim] += currentPoint[dim];

            }

            timeSum /= ptsToRead;
            for (int dim = 0; dim < ts.numOfDimensions(); dim++)
                measurementSums[dim] = measurementSums[dim] / (double) ptsToRead;

            aggPtSize[super.size()] = ptsToRead;
            addLast(timeSum, new TimeSeriesPoint(measurementSums));
        }

    }

    public int originalSize() {
        return originalLength;
    }

    public int aggregatePtSize(int ptIndex) {
        return aggPtSize[ptIndex];
    }

    public String toString() {
        return "(" + originalLength + " point time series represented as " + size() + " points)\n" + super.toString();
    }

    private int aggPtSize[];

    private final int originalLength;
}