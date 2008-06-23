/**
 * ChebychevDistance.java
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

import net.sf.javaml.core.Instance;

public class ChebychevDistance extends AbstractDistance {

    private static final long serialVersionUID = -5886297070247181544L;

    @Override
    public double measure(Instance x, Instance y) {
        if (x.noAttributes() != y.noAttributes())
            throw new RuntimeException("Both instances should contain the same number of values.");
        double totalMax = 0.0;
        for (int i = 0; i < x.noAttributes(); i++) {
            totalMax = Math.max(totalMax, Math.abs(y.value(i) - x.value(i)));
        }
        return totalMax;
    }

}
