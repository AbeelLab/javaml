/**
 * FastDTW.java
 *
 * This file is part of the Java Machine Learning API
 * 
 * The Java Machine Learning API is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The Java Machine Learning API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning API; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2004 Stan Salvador
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 * Based on work by Stan Salvador and Philip Chan.
 * 
 */
package net.sf.javaml.distance.dtw;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.Pair;
import net.sf.javaml.distance.AbstractDistance;

/**
 * Implementation of the Fast Dynamic Time Warping algorithm as proposed by
 * Salvador and Chan, 2004.
 * 
 * <pre>
 * FastDTW: Toward Accurate Dynamic Time Warping in Linear Time and Space
 * Stan Salvador and Philip Chan
 * KDD Workshop on Mining Temporal and Sequential Data
 * </pre>
 * 
 * This algorithm is an approximation of the real Dynamic Time Warping Distance
 * 
 * At this point, it is not clear whether this implementation actually works.
 * 
 * @author Thomas Abeel
 * 
 */

public class FastDTW extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = -8616157911806438667L;

    public WarpPath getWarpPath(Instance tsI, Instance tsJ) {
        return fastDTW(tsI, tsJ, searchwindow).y();
    }

    private static Pair<Double, WarpPath> fastDTW(Instance tsI, Instance tsJ, int searchRadius) {
        if (searchRadius < 0)
            searchRadius = 0;
        int minTSsize = searchRadius + 2;
        if (tsI == null || tsJ == null)
            throw new RuntimeException("Null instances are not allowed");
        if (tsI.size() <= minTSsize || tsJ.size() <= minTSsize) {
            return DTW.dtw(tsI, tsJ);
        } else {
            // double resolutionFactor = 2D;
            PAA shrunkI = new PAA(tsI, (int) ((double) tsI.size() / 2D));
            PAA shrunkJ = new PAA(tsJ, (int) ((double) tsJ.size() / 2D));
            SearchWindow window = new SearchWindow(tsI, tsJ, shrunkI, shrunkJ, fastDTW(tsI, tsJ, searchRadius).y(),
                    searchRadius);
            return DTW.dtw(tsI, tsJ, window);
        }
    }

    private int searchwindow;

    public FastDTW() {
        this(1);
    }

    public FastDTW(int searchwindow) {
        this.searchwindow = searchwindow;
    }

    public double calculateDistance(Instance i, Instance j) {
        return fastDTW(i, j, searchwindow).x();
    }

    public double getMaximumDistance(Dataset data) {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

    public double getMinimumDistance(Dataset data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

}
