/**
 * RBFKernel.java
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

public class RBFKernel implements DistanceMeasure {

    private double gamma = 0.01;

    public RBFKernel() {
        this(0.01);
    }

    /**
     * Create a new RBF kernel with gamma as a parameter
     * 
     * @param gamma
     */
    public RBFKernel(double gamma) {
        this.gamma = gamma;
    }

    /**
     * Calculates a dot product between two instances
     * 
     * @param x
     *            the first instance
     * @param y
     *            the second instance
     * @return the dot product of the two instances.
     */
    private final double dotProduct(Instance x, Instance y) {
        double result = 0;
        for (int i = 0; i < x.size(); i++) {
            result += x.getValue(i) * y.getValue(i);
        }
        return result;
    }

    public double calculateDistance(Instance x, Instance y) {
        if (x.equals(y))
            return 1.0;
        double result = Math.exp(gamma * (2.0 * dotProduct(x, y) - dotProduct(x, x) - dotProduct(y, y)));
        return result;

    }

}
