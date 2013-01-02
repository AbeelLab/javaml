/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.timeseries;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

public class TimeSeriesPoint {

    public TimeSeriesPoint(double values[]) {
        hashCode = 0;
        measurements = new double[values.length];
        for (int x = 0; x < values.length; x++) {
            hashCode += (new Double(values[x])).hashCode();
            measurements[x] = values[x];
        }

    }

    public TimeSeriesPoint(Collection values) {
        measurements = new double[values.size()];
        hashCode = 0;
        Iterator i = values.iterator();
        for (int index = 0; i.hasNext(); index++) {
            Object nextElement = i.next();
            if (nextElement instanceof Double)
                measurements[index] = ((Double) nextElement).doubleValue();
            else if (nextElement instanceof Integer)
                measurements[index] = ((Integer) nextElement).doubleValue();
            else if (nextElement instanceof BigInteger)
                measurements[index] = ((BigInteger) nextElement).doubleValue();
            else
                throw new InternalError("ERROR:  The element " + nextElement + " is not a valid numeric type");
            hashCode += (new Double(measurements[index])).hashCode();
        }

    }

    public double get(int dimension) {
        return measurements[dimension];
    }

    public void set(int dimension, double newValue) {
        hashCode -= (new Double(measurements[dimension])).hashCode();
        measurements[dimension] = newValue;
        hashCode += (new Double(newValue)).hashCode();
    }

    public double[] toArray() {
        return measurements;
    }

    public int size() {
        return measurements.length;
    }

    public String toString() {
        String outStr = "(";
        for (int x = 0; x < measurements.length; x++) {
            outStr = outStr + measurements[x];
            if (x < measurements.length - 1)
                outStr = outStr + ",";
        }

        outStr = outStr + ")";
        return outStr;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof TimeSeriesPoint) {
            double testValues[] = ((TimeSeriesPoint) o).toArray();
            if (testValues.length == measurements.length) {
                for (int x = 0; x < measurements.length; x++)
                    if (measurements[x] != testValues[x])
                        return false;

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        return hashCode;
    }

    private double measurements[];

    private int hashCode;
}