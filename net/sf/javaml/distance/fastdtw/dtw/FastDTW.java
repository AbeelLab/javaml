/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.dtw;

import net.sf.javaml.distance.fastdtw.timeseries.PAA;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public class FastDTW {

    public FastDTW() {
    }

    public static double getWarpDistBetween(TimeSeries tsI, TimeSeries tsJ) {
        return fastDTW(tsI, tsJ, 1).getDistance();
    }

    public static double getWarpDistBetween(TimeSeries tsI, TimeSeries tsJ, int searchRadius) {
        return fastDTW(tsI, tsJ, searchRadius).getDistance();
    }

    public static WarpPath getWarpPathBetween(TimeSeries tsI, TimeSeries tsJ) {
        return fastDTW(tsI, tsJ, 1).getPath();
    }

    public static WarpPath getWarpPathBetween(TimeSeries tsI, TimeSeries tsJ, int searchRadius) {
        return fastDTW(tsI, tsJ, searchRadius).getPath();
    }

    public static TimeWarpInfo getWarpInfoBetween(TimeSeries tsI, TimeSeries tsJ, int searchRadius) {
        return fastDTW(tsI, tsJ, searchRadius);
    }

    private static TimeWarpInfo fastDTW(TimeSeries tsI, TimeSeries tsJ, int searchRadius) {
        if (searchRadius < 0)
            searchRadius = 0;
        int minTSsize = searchRadius + 2;
        if (tsI.size() <= minTSsize || tsJ.size() <= minTSsize) {
            return DTW.getWarpInfoBetween(tsI, tsJ);
        } else {
            double resolutionFactor = 2D;
            PAA shrunkI = new PAA(tsI, (int) ((double) tsI.size() / 2D));
            PAA shrunkJ = new PAA(tsJ, (int) ((double) tsJ.size() / 2D));
            SearchWindow window = new ExpandedResWindow(tsI, tsJ, shrunkI, shrunkJ, getWarpPathBetween(shrunkI,
                    shrunkJ, searchRadius), searchRadius);
            return DTW.getWarpInfoBetween(tsI, tsJ, window);
        }
    }

    static final int DEFAULT_SEARCH_RADIUS = 1;
}