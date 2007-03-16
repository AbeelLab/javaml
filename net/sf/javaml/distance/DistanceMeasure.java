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
public interface DistanceMeasure {
    public double calculateDistance(Instance i, Instance j);

}
