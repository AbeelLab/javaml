/**
 * MaxProductSimilarity.java
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

import net.sf.javaml.core.Instance;

/**
 * Specialized similarity that takes the maximum product of two feature values.
 * If this value is zero, the similarity is undefined. This similarity measure
 * is used mainly with features extracted from cluster models.
 * 
 * @author Michael Wurst
 * @author Thomas Abeel
 */
public class MaxProductSimilarity extends AbstractSimilarity {
    /**
     * 
     */
    private static final long serialVersionUID = 3737968543405527283L;

    /**
     * XXX doc
     */
    public double measure(Instance x, Instance y) {

        double max = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < x.noAttributes(); i++) {

            double v = x.value(i) * y.value(i);

            if (v > max)
                max = v;

        }

        if (max > 0.0)
            return max;
        else
            return Double.NaN;
    }

}
