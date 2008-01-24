/**
 * NormalizedEuclideanDistance.java
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
import net.sf.javaml.core.SimpleInstance;

/**
 * A normalized version of the Euclidean distance. This distance measure is
 * normalized in the interval [0,1].
 * 
 * High values denote low similar items (high distance) and low values denote
 * highly similar items (low distance).
 * 
 * @author Thomas Abeel
 * 
 */
public class NormalizedEuclideanDistance extends EuclideanDistance {

    /**
     * 
     */
    private static final long serialVersionUID = -6489071802740149683L;

    private Dataset data;

    public NormalizedEuclideanDistance(Dataset data) {
        super();
        this.data = data;
    }

    public double calculateDistance(Instance i, Instance j) {
        Instance normI = normalizeMidrange(0.5, 1, data.getMinimumInstance(), data.getMaximumInstance(), i);
        Instance normJ = normalizeMidrange(0.5, 1, data.getMinimumInstance(), data.getMaximumInstance(), j);
        return super.calculateDistance(normI, normJ) / Math.sqrt(i.size());

    }

    private Instance normalizeMidrange(double normalMiddle, double normalRange, Instance min, Instance max,
            Instance instance) {
        double[] out = new double[instance.size()];
        for (int i = 0; i < out.length; i++) {
            double range = Math.abs(max.value(i) - min.value(i));
            double middle = Math.abs(max.value(i) + min.value(i)) / 2;
            out[i] = ((instance.value(i) - middle) / range) * normalRange + normalMiddle;
        }
        return new SimpleInstance(out, instance);
    }
}
