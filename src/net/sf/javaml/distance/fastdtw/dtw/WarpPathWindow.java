/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.dtw;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public class WarpPathWindow extends SearchWindow {

    public WarpPathWindow(WarpPath path, int searchRadius) {
        super(path.get(path.size() - 1).getCol() + 1, path.get(path.size() - 1).getRow() + 1);
        for (int p = 0; p < path.size(); p++)
            super.markVisited(path.get(p).getCol(), path.get(p).getRow());

        super.expandWindow(searchRadius);
    }

    private static final int defaultRadius = 0;
}