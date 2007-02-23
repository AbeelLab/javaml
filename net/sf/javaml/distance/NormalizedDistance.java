/**
 * NormalizedDistance.java
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
 * This class provides a generic way to obtain a normalized version of any
 * distance measure.
 * 
 * It will convert any distance measure to the interval [0,1].
 * 
 * Alfa should be typically somewhere near the value of the variance for the
 * dataset.
 * 
 * This method is based on work from
 * http://people.revoledu.com/kardi/tutorial/Similarity/Normalization.html.
 * 
 * @author Thomas Abeel
 * 
 */
public class NormalizedDistance implements DistanceMeasure {

	private DistanceMeasure dm;

	private double alfa;

	public NormalizedDistance(DistanceMeasure dm) {
		this(dm, 5);
	}

	public NormalizedDistance(DistanceMeasure dm, double alfa) {
		this.dm = dm;
		this.alfa = alfa;
	}

	public double calculateDistance(Instance i, Instance j) {
		double dist = dm.calculateDistance(i, j);
		return (1 - (dist / Math.sqrt(dist * dist + alfa))) / 2;
	}
}
