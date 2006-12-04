/**
 * EuclidianDistance.java, 24-okt-2006
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
 * Copyright (c) 2006, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Instance;

/**
 * This class implements the Euclidean distance.
 * 
 * The Euclidean distance between two points P=(p1,p2,...,pn) and
 * Q=(q1,q2,...,qn) in the Euclidean n-space is defined as: sqrt((p1-q1)^2 +
 * (p2-q2)^2 + ... + (pn-qn)^2)
 * 
 * The Euclidean distance is a special instance of the NormDistance. The
 * Euclidean distance corresponds to the 2-norm distance.
 * 
 * @linkplain http://en.wikipedia.org/wiki/Euclidean_distance
 * @linkplain http://en.wikipedia.org/wiki/Euclidean_space
 * @author Thomas Abeel
 * 
 */
public class EuclideanDistance extends NormDistance {

    public double calculateDistance(Instance x, Instance y) {
        if (x.size() != y.size()) {
            throw new RuntimeException("Both instances should contain the same number of values.");
        }
        double sum = 0;
        for (int i = 0; i < x.size(); i++) {
            sum += (y.getValue(i) - x.getValue(i)) * (y.getValue(i) - x.getValue(i));
        }
        return Math.sqrt(sum);
    }
}
