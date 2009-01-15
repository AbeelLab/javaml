/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.dtw;

import java.util.ArrayList;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.AbstractSimilarity;

/**
 * A similarity measure based on "Dynamic Time Warping". The DTW distance is
 * mapped to a similarity measure using f(x)= 1 - (x / (1 + x)). Feature weights
 * are also supported.
 * 
 * @author Piotr Kasprzak
 * @author Thomas Abeel
 * 
 */
public class DTWSimilarity extends AbstractSimilarity {

    private static final long serialVersionUID = -8898553450277603746L;

    private double pointDistance(int i, int j, double[] ts1, double[] ts2) {
        double diff = ts1[i] - ts2[j];
        return (diff * diff);
    }

    private double distance2Similarity(double x) {
        return (1.0 - (x / (1 + x)));
    }

    @Override
    public double measure(Instance x, Instance y) {

        ArrayList<Double> l1 = new ArrayList<Double>();
        ArrayList<Double> l2 = new ArrayList<Double>();
        int i, j;

        /** Filter NaNs */
        for (i = 0; i < x.noAttributes(); i++) {
            double value = x.value(i);
            if (!Double.isNaN(value)) {
                l1.add(value);
            }
        }

        for (i = 0; i < y.noAttributes(); i++) {
            double value = y.value(i);
            if (!Double.isNaN(value)) {
                l2.add(new Double(value));
            }
        }

        /** Transform the examples to vectors */
        double[] ts1 = new double[l1.size()];
        double[] ts2 = new double[l2.size()];

        for (i = 0; i < ts1.length; i++) {
            ts1[i] = l1.get(i);
        }

        for (i = 0; i < ts2.length; i++) {
            ts2[i] = l2.get(i);
        }

        /** Build a point-to-point distance matrix */
        double[][] dP2P = new double[ts1.length][ts2.length];
        for (i = 0; i < ts1.length; i++) {
            for (j = 0; j < ts2.length; j++) {
                dP2P[i][j] = pointDistance(i, j, ts1, ts2);
            }
        }

        /** Check for some special cases due to ultra short time series */
        if (ts1.length == 0 || ts2.length == 0) {
            return Double.NaN;
        }

        if (ts1.length == 1 && ts2.length == 1) {
            return distance2Similarity(Math.sqrt(dP2P[0][0]));
        }

        /**
         * Build the optimal distance matrix using a dynamic programming
         * approach
         */
        double[][] D = new double[ts1.length][ts2.length];

        D[0][0] = dP2P[0][0]; // Starting point

        for (i = 1; i < ts1.length; i++) { // Fill the first column of our
            // distance matrix with optimal
            // values
            D[i][0] = dP2P[i][0] + D[i - 1][0];
        }

        if (ts2.length == 1) { // TS2 is a point
            double sum = 0;
            for (i = 0; i < ts1.length; i++) {
                sum += D[i][0];
            }
            return distance2Similarity(Math.sqrt(sum) / ts1.length);
        }

        for (j = 1; j < ts2.length; j++) { // Fill the first row of our
            // distance matrix with optimal
            // values
            D[0][j] = dP2P[0][j] + D[0][j - 1];
        }

        if (ts1.length == 1) { // TS1 is a point
            double sum = 0;
            for (j = 0; j < ts2.length; j++) {
                sum += D[0][j];
            }
            return distance2Similarity(Math.sqrt(sum) / ts2.length);
        }

        for (i = 1; i < ts1.length; i++) { // Fill the rest
            for (j = 1; j < ts2.length; j++) {
                double[] steps = { D[i - 1][j - 1], D[i - 1][j], D[i][j - 1] };
                double min = Math.min(steps[0], Math.min(steps[1], steps[2]));
                D[i][j] = dP2P[i][j] + min;
            }
        }

        /**
         * Calculate the distance between the two time series through optimal
         * alignment.
         */
        i = ts1.length - 1;
        j = ts2.length - 1;
        int k = 1;
        double dist = D[i][j];

        while (i + j > 2) {
            if (i == 0) {
                j--;
            } else if (j == 0) {
                i--;
            } else {
                double[] steps = { D[i - 1][j - 1], D[i - 1][j], D[i][j - 1] };
                double min = Math.min(steps[0], Math.min(steps[1], steps[2]));

                if (min == steps[0]) {
                    i--;
                    j--;
                } else if (min == steps[1]) {
                    i--;
                } else if (min == steps[2]) {
                    j--;
                }
            }
            k++;
            dist += D[i][j];
        }

        return distance2Similarity(Math.sqrt(dist) / k);
    }

}
