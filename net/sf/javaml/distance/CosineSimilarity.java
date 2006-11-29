/**
 * CosineSimilarity.java, 13-nov-2006
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

public class CosineSimilarity implements DistanceMeasure {

    public double calculateDistance(Instance x, Instance y) {
        if (x.size() != y.size()) {
            throw new RuntimeException("Both instances should contain the same number of values.");
        }
        double sumTop = 0;
        double sumOne = 0;
        double sumTwo = 0;

        for (int i = 0; i < x.size(); i++) {
            sumTop += x.getValue(i) * y.getValue(i);
            sumOne += x.getValue(i) * x.getValue(i);
            sumTwo += y.getValue(i) * y.getValue(i);
        }
        // CosineSimilarity is actually a similarity measure. To return a
        // distance the distance
        // we need to substract it from its maximum value.
        double division = sumTop / (Math.sqrt(sumOne) * Math.sqrt(sumTwo));
        if ( division>1){
        	division = 1;
        }
        double cosSim = 1 - division;
        return cosSim;

    }

}
