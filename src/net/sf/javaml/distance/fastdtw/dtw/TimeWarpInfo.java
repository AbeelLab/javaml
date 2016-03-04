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
public class TimeWarpInfo {

    TimeWarpInfo(double dist, WarpPath wp) {
        distance = dist;
        path = wp;
    }

    public double getDistance() {
        return distance;
    }

    public WarpPath getPath() {
        return path;
    }

    public String toString() {
        return "(Warp Distance=" + distance + ", Warp Path=" + path + ")";
    }

    private final double distance;

    private final WarpPath path;
}