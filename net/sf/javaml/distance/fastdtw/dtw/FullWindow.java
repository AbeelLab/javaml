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
public class FullWindow extends SearchWindow {

    public FullWindow(TimeSeries tsI, TimeSeries tsJ) {
        super(tsI.size(), tsJ.size());
        for (int i = 0; i < tsI.size(); i++) {
            super.markVisited(i, minJ());
            super.markVisited(i, maxJ());
        }

    }
}