/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.dtw;

import net.sf.javaml.distance.fastdtw.matrix.ColMajorCell;
import net.sf.javaml.distance.fastdtw.timeseries.PAA;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public class ExpandedResWindow extends SearchWindow {

    public ExpandedResWindow(TimeSeries tsI, TimeSeries tsJ, PAA shrunkI, PAA shrunkJ, WarpPath shrunkWarpPath,
            int searchRadius) {
        super(tsI.size(), tsJ.size());
        int currentI = shrunkWarpPath.minI();
        int currentJ = shrunkWarpPath.minJ();
        int lastWarpedI = 0x7fffffff;
        int lastWarpedJ = 0x7fffffff;
        for (int w = 0; w < shrunkWarpPath.size(); w++) {
            ColMajorCell currentCell = shrunkWarpPath.get(w);
            int warpedI = currentCell.getCol();
            int warpedJ = currentCell.getRow();
            int blockISize = shrunkI.aggregatePtSize(warpedI);
            int blockJSize = shrunkJ.aggregatePtSize(warpedJ);
            if (warpedJ > lastWarpedJ)
                currentJ += shrunkJ.aggregatePtSize(lastWarpedJ);
            if (warpedI > lastWarpedI)
                currentI += shrunkI.aggregatePtSize(lastWarpedI);
            if (warpedJ > lastWarpedJ && warpedI > lastWarpedI) {
                super.markVisited(currentI - 1, currentJ);
                super.markVisited(currentI, currentJ - 1);
            }
            for (int x = 0; x < blockISize; x++) {
                super.markVisited(currentI + x, currentJ);
                super.markVisited(currentI + x, (currentJ + blockJSize) - 1);
            }

            lastWarpedI = warpedI;
            lastWarpedJ = warpedJ;
        }

        super.expandWindow(searchRadius);
    }
}