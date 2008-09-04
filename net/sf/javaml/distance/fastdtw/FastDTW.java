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
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public class FastDTW extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = -3604661850260159935L;

    // PUBLIC FUNCTIONS
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("USAGE:  java FastDTW timeSeries1 timeSeries2 radius");
            System.exit(1);
        } else {
            final TimeSeries tsI = new TimeSeries(args[0], false, false, ',');
            final TimeSeries tsJ = new TimeSeries(args[1], false, false, ',');
            final TimeWarpInfo info = net.sf.javaml.distance.fastdtw.dtw.FastDTW.getWarpInfoBetween(tsI, tsJ, Integer
                    .parseInt(args[2]));

            System.out.println("Warp Distance: " + info.getDistance());
            System.out.println("Warp Path:     " + info.getPath());
        } // end if

    } // end main()

    private int radius;

    @Override
    public double measure(Instance x, Instance y) {
        final TimeSeries tsI = new TimeSeries(x);
        final TimeSeries tsJ = new TimeSeries(y);
        final TimeWarpInfo info = net.sf.javaml.distance.fastdtw.dtw.FastDTW.getWarpInfoBetween(tsI, tsJ, radius);

        System.out.println("Warp Distance: " + info.getDistance());
        System.out.println("Warp Path:     " + info.getPath());
        return info.getDistance();
    }

    public FastDTW(int radius) {
        super();
        this.radius = radius;
    }

} // end class FastDTW
