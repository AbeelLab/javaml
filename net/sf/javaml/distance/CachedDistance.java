/**
 * CachedDistance.java
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

import java.util.HashMap;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.Pair;

/**
 * This class implements a wrapper around other distance measure to cache
 * previously calculated distances.
 * 
 * This should only be used with time consuming distance measures. For Euclidean
 * distance for example it is faster to recalculate it each time.
 * 
 * @author Thomas Abeel
 * 
 */
public class CachedDistance implements DistanceMeasure {

    private DistanceMeasure dm = null;

    public CachedDistance(DistanceMeasure dm) {
        this.dm = dm;
    }

    // row map
    HashMap<Pair<Instance, Instance>, Double> cache = new HashMap<Pair<Instance, Instance>, Double>();

    public double calculateDistance(Instance i, Instance j) {
        Pair<Instance, Instance> pair = new Pair<Instance, Instance>(i, j);
        if (cache.containsKey(pair)) {
            return cache.get(pair);
        } else {
            double dist = dm.calculateDistance(i, j);
            cache.put(pair, dist);
            return dist;
        }
    }

    public boolean compare(double x, double y) {
        return dm.compare(x, y);
    }

    public double getMaximumDistance(Dataset data) {
        return dm.getMaximumDistance(data);
    }

    public double getMinimumDistance(Dataset data) {
        return dm.getMinimumDistance(data);
    }

}
