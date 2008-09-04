/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.timeseries;

import java.util.Random;

// Referenced classes of package timeseries:
//            TimeSeries, TimeSeriesPoint

public class SineWave extends TimeSeries {

    public SineWave(int length, double cycles, double noise) {
        super(1);
        for (int x = 0; x < length; x++) {
            double nextPoint = Math.sin(((double) x / (double) length) * 2D * 3.1415926535897931D * cycles)
                    + rand.nextGaussian() * noise;
            super.addLast(x, new TimeSeriesPoint(new double[] { nextPoint }));
        }

    }

    private static final Random rand = new Random();

}