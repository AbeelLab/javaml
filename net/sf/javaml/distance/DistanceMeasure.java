/**
 * DistanceMeasure.java
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

import java.io.Serializable;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * A distance measure is an algorithm to calculate the distance between to
 * instances. Objects that are close together or are very similar should have
 * low distance values, while object that are far apart or are not similar
 * should have high distance values.
 * 
 * By convention measures that end in <code>Similarity</code> have a high
 * value for highly similar (low distance) and measure that end in
 * <code>Distance</code> have a high value for items that have low similarity
 * (high distance).
 * 
 * Some distance measures are normalized, i.e. in the interval [0,1], but this
 * is not required by the interface.
 * 
 * @author Thomas Abeel
 * 
 */
public interface DistanceMeasure extends Serializable {
    /**
     * XXX add doc
     * 
     * @param i
     * @param j
     * @return
     */
    public double calculateDistance(Instance i, Instance j);

    /**
     * XXX add doc maximal distance, minimal similarity for distance measures
     * this value should be high, for similarity measures this value should be
     * low
     * 
     * @param data
     * @return
     */
    public double getMaximumDistance(Dataset data);

    /**
     * XXX add doc minimal distance, maximal similarty for distance measures
     * this value should be low, for similarity measures this value should be
     * high
     * 
     * @param data
     * @return
     */
    public double getMinimumDistance(Dataset data);
    /**
     * Returns whether the first distance/similarity is better than the second distance/similarity.
     * 
     * For similarity measures the higher the similarity the better the measure,
     * for distance measures it is the lower the better.
     * 
     * @param x the first distance or similarity
     * @param y the second distance or similarity
     * @return true if the first distance is better than the second, false in other cases.
     */
    public boolean compare(double x,double y);
}
