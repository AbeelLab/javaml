/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.AbstractDistance;
import net.sf.javaml.distance.fastdtw.dtw.TimeWarpInfo;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

/**
 * 
 * Implementation of the FastDTW algorithm as described by Salvador and Chan.
 * 
 * Stan Salvador and Philip Chan, FastDTW: Toward Accurate Dynamic Time Warping
 * in Linear Time and Space, KDD Workshop on Mining Temporal and Sequential
 * Data, pp. 70-80, 2004. http://www.cs.fit.edu/~pkc/papers/tdm04.pdf
 * 
 * 
 * Stan Salvador and Philip Chan, Toward Accurate Dynamic Time Warping in Linear
 * Time and Space, Intelligent Data Analysis, 11(5):561-580, 2007.
 * http://www.cs.fit.edu/~pkc/papers/ida07.pdf
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * @author Philip Chan, pkc@cs.fit.edu
 * 
 * 
 * 
 */
public class FastDTW extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = -3604661850260159935L;

    private int radius;

    @Override
    public double measure(Instance x, Instance y) {
        final TimeSeries tsI = new TimeSeries(x);
        final TimeSeries tsJ = new TimeSeries(y);
        final TimeWarpInfo info = net.sf.javaml.distance.fastdtw.dtw.FastDTW.getWarpInfoBetween(tsI, tsJ, radius);
        return info.getDistance();
    }

    public FastDTW(int radius) {
        super();
        this.radius = radius;
    }

}
