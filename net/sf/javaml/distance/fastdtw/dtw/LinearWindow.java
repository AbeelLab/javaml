/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.dtw;

import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public class LinearWindow extends SearchWindow {

    public LinearWindow(TimeSeries tsI, TimeSeries tsJ, int searchRadius) {
        super(tsI.size(), tsJ.size());
        double ijRatio = (double) tsI.size() / (double) tsJ.size();
        boolean isIlargest = tsI.size() >= tsJ.size();
        for (int i = 0; i < tsI.size(); i++)
            if (isIlargest) {
                int j = Math.min((int) Math.round((double) i / ijRatio), tsJ.size() - 1);
                super.markVisited(i, j);
            } else {
                int maxJ = (int) Math.round((double) (i + 1) / ijRatio) - 1;
                int minJ = (int) Math.round((double) i / ijRatio);
                super.markVisited(i, minJ);
                super.markVisited(i, maxJ);
            }

        super.expandWindow(searchRadius);
    }

    public LinearWindow(TimeSeries tsI, TimeSeries tsJ) {
        this(tsI, tsJ, 0);
    }

    private static final int defaultRadius = 0;
}