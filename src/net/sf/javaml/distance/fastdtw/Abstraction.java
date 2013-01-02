/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw;

import java.util.ArrayList;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.AbstractDistance;
import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.distance.fastdtw.dtw.SearchWindow;
import net.sf.javaml.distance.fastdtw.dtw.TimeWarpInfo;
import net.sf.javaml.distance.fastdtw.dtw.WarpPath;
import net.sf.javaml.distance.fastdtw.dtw.WarpPathWindow;
import net.sf.javaml.distance.fastdtw.timeseries.PAA;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public class Abstraction extends AbstractDistance {

    private int radius = 5;

    public Abstraction(int radius) {
        this.radius = radius;
    }

    @Override
    public double measure(Instance x, Instance y) {
        final TimeSeries tsI = new TimeSeries(x);
        final TimeSeries tsJ = new TimeSeries(y);

        final PAA shrunkI = new PAA(tsI, (int) Math.round(Math.sqrt((double) tsI.size())));
        final PAA shrunkJ = new PAA(tsJ, (int) Math.round(Math.sqrt((double) tsJ.size())));
        final WarpPath coarsePath = DTW.getWarpPathBetween(shrunkI, shrunkJ);
        final WarpPath expandedPath = expandPath(coarsePath, shrunkI, shrunkJ);
        final SearchWindow w = new WarpPathWindow(expandedPath, radius);
        final TimeWarpInfo info = DTW.getWarpInfoBetween(tsI, tsJ, w);

        System.out.println("Warp Distance: " + info.getDistance());
        System.out.println("Warp Path:     " + info.getPath());
        return info.getDistance();
    }

    /**
     * 
     */
    private static final long serialVersionUID = -4864690770638592535L;

    // PUBLIC FUNCTIONS
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("USAGE:  java Abstraction timeSeries1 timeSeries2 radius");
            System.exit(1);
        } else {
            final TimeSeries tsI = new TimeSeries(args[0], false, false, ',');
            final TimeSeries tsJ = new TimeSeries(args[1], false, false, ',');

            final PAA shrunkI = new PAA(tsI, (int) Math.round(Math.sqrt((double) tsI.size())));
            final PAA shrunkJ = new PAA(tsJ, (int) Math.round(Math.sqrt((double) tsJ.size())));
            final WarpPath coarsePath = DTW.getWarpPathBetween(shrunkI, shrunkJ);
            final WarpPath expandedPath = expandPath(coarsePath, shrunkI, shrunkJ);
            final SearchWindow w = new WarpPathWindow(expandedPath, Integer.parseInt(args[2]));
            final TimeWarpInfo info = DTW.getWarpInfoBetween(tsI, tsJ, w);

            System.out.println("Warp Distance: " + info.getDistance());
            System.out.println("Warp Path:     " + info.getPath());
        } // end if
    } // end main()

    // Expand the small warp path to the resolution of the original time series
    // for tsI and tsJ.
    private static WarpPath expandPath(WarpPath path, PAA tsI, PAA tsJ) {
        final ArrayList iPoints = new ArrayList();
        final ArrayList jPoints = new ArrayList();

        iPoints.add(new Integer(0));
        jPoints.add(new Integer(0));
        int startI = 0; // tsI.aggregatePtSize(0);
        int startJ = 0; // tsJ.aggregatePtSize(0);
        if (path.get(1).getCol() != 0)
            startI = tsI.aggregatePtSize(0) - 1;
        else
            startI = (tsI.aggregatePtSize(0) - 1) / 2;

        if (path.get(1).getRow() != 0)
            startJ = tsJ.aggregatePtSize(0) - 1;
        else
            startJ = (tsJ.aggregatePtSize(0) - 1) / 2;

        int lastI = 0;
        int lastJ = 0;

        for (int x = 1; x < path.size() - 1; x++) {
            int currentI = path.get(x).getCol();
            int currentJ = path.get(x).getRow();

            if ((lastI != currentI)) {
                if (lastI == 0)
                    startI = tsI.aggregatePtSize(0) - 1;

                if (currentI == path.get(path.size() - 1).getCol())
                    startI -= tsI.aggregatePtSize(currentI) / 2;
                {
                    iPoints.add(new Integer(startI + tsI.aggregatePtSize(currentI) / 2));
                    startI += tsI.aggregatePtSize(currentI);
                }

                lastI = currentI;
            } else {
                iPoints.add(new Integer(startI));
            }

            if ((lastJ != currentJ)) {
                if (lastJ == 0)
                    startJ = tsJ.aggregatePtSize(0) - 1;

                if (currentJ == path.get(path.size() - 1).getRow())
                    startJ -= tsJ.aggregatePtSize(currentJ) / 2;
                {
                    jPoints.add(new Integer(startJ + tsJ.aggregatePtSize(currentJ) / 2));
                    startJ += tsJ.aggregatePtSize(currentJ);
                }

                lastJ = currentJ;
            } else {
                jPoints.add(new Integer(startJ));
            }
        } // end for loop

        iPoints.add(new Integer(tsI.originalSize() - 1));
        jPoints.add(new Integer(tsJ.originalSize() - 1));

        // Interpolate between coarse warp path points.
        final WarpPath expandedPath = new WarpPath();

        startI = 0;
        startJ = 0;
        int endI;
        int endJ;

        for (int p = 1; p < iPoints.size(); p++) {
            endI = ((Integer) iPoints.get(p)).intValue();
            endJ = ((Integer) jPoints.get(p)).intValue();
            expandedPath.addLast(startI, startJ);

            if ((endI - startI) >= (endJ - startJ)) {
                for (int i = startI + 1; i < endI; i++)
                    expandedPath.addLast(i, (int) Math.round(startJ + ((double) i - startI) / ((double) endI - startI)
                            * (endJ - startJ)));
            } else {
                for (int j = startJ + 1; j < endJ; j++)
                    expandedPath.addLast((int) Math.round(startI + ((double) j - startJ) / ((double) endJ - startJ)
                            * (endI - startI)), j);
            } // end if

            startI = endI;
            startJ = endJ;
        } // end for loop

        expandedPath.addLast(tsI.originalSize() - 1, tsJ.originalSize() - 1);
        return expandedPath;
    }

} // end class Abstraction
