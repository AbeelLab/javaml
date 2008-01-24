/**
 * ManhattanDistance.java
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
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * The Manhattan distance is the sum of the (absolute) differences of their
 * coordinates. The taxicab metric is also known as recti-linear distance,
 * Minkowski's L1 distance, city block distance, or Manhattan distance.
 * 
 * 
 * @linkplain http://en.wikipedia.org/wiki/Taxicab_geometry
 * @linkplain http://www.nist.gov/dads/HTML/manhattanDistance.html
 * @linkplain http://mathworld.wolfram.com/TaxicabMetric.html
 * 
 * @author Thomas Abeel
 */
public class ManhattanDistance extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = 4954621120642494177L;

    /**
     * Calculates the Manhattan distance as the sum of the absolute differences
     * of their coordinates.
     * 
     * @return the Manhattan distance between the two instances.
     */
    public double calculateDistance(Instance x, Instance y) {
        if (x.size() != y.size())
            throw new RuntimeException("Both instances should contain the same number of values.");
        double sum = 0.0;
        for (int i = 0; i < x.size(); i++) {
            sum += Math.abs(x.value(i) - y.value(i));
        }
        return sum;
    }

    /**
     * Returns the theoretical maximum distance for the given data set. This is
     * based on the virtual Min and Max instances of the data set.
     * 
     * @see Dataset
     * 
     * @param data
     *            the data set for which the maximal possible distance should be
     *            calculated.
     * 
     * @return the maximum possible distance between instances in the data set
     */
    public double getMaximumDistance(Dataset data) {
        Instance max = data.getMaximumInstance();
        Instance min = data.getMinimumInstance();
        return calculateDistance(min, max);
    }

    /**
     * Return the minimal Manhattan distance between two instances in the given
     * data set. This is always zero as the Manhattan distances cannot be
     * negative and the distance between two equal instances is zero.
     * 
     * @param data
     *            the data set for which the minimal possible distance should be
     *            calculated.
     * @return the minimum possible Manhattan distance for the data set, i.e.
     *         zero
     */
    public double getMinimumDistance(Dataset data) {
        return 0;
    }

}
