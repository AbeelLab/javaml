/**
 * CosineSimilarity.java
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
 * This similarity based distance measure actually measures the angle between
 * two vectors. 
 * 
 * The value returned lies in the interval [0,1].
 * 
 * @author Thomas Abeel
 * 
 */
public class CosineDistance extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = 7818036381569908860L;

    public double calculateDistance(Instance x, Instance y) {
        if (x.size() != y.size()) {
            throw new RuntimeException("Both instances should contain the same number of values.");
        }
        return 1-new CosineSimilarity().calculateDistance(x, y);

    }

    public double getMaximumDistance(Dataset data) {
        //TODO implement
        throw new RuntimeException("Method getMaximumDistance is not implemented in CosineDistance.");
    }

    public double getMinimumDistance(Dataset data) {
        // TODO implement
        throw new RuntimeException("Method getMinimumDistance is not implemented in CosineDistance.");
    }
}
