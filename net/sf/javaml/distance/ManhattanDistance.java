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
 * Copyright (c) 2001-2006, Michael Wurst
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * The Manhattan distance.
 * 
 * @author Michael Wurst
 * @author Thomas Abeel
 */
public class ManhattanDistance extends AbstractDistance {

    /**
     * XXX doc
     */
    public double calculateDistance(Instance x, Instance y) {
        float[] e1 = x.toArray();
        float[] e2 = y.toArray();
        double sum = 0.0;
        int counter = 0;

        for (int i = 0; i < e1.length; i++) {

            if ((!Double.isNaN(e1[i])) && (!Double.isNaN(e2[i]))) {
                sum = sum + Math.abs(e1[i] - e2[i]);
                counter++;
            }

        }

        double d = sum;

        if (counter > 0)
            return d;
        else
            return Double.NaN;
    }

    /**
     * XXX doc
     */
    public double getMaximumDistance(Dataset data) {
        // TODO implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * XXX doc
     */
    public double getMinimumDistance(Dataset data) {
        // TODO implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
