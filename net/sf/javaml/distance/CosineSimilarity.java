/**
 * CosineSimilarity.java, 13-nov-2006
 *
 * This file is part of the Java Machine Learning API
 * 
 * php-agenda is free software; you can redistribute it and/or modify
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

    protected CosineSimilarity(){}
    public double calculateDistance(Instance x, Instance y) {
            if (x.size() != y.size()) {
                throw new RuntimeException("Both instances should contain the same number of values.");
            }
            double prodX = 1;
            double prodY=1;
            double prodSq=1;
            
            for (int i = 0; i < x.size(); i++) {
               prodX*=x.getValue(i);
               prodY*=y.getValue(i);
               prodSq*=Math.sqrt(x.getValue(i)*x.getValue(i)+y.getValue(i)*y.getValue(i));
            }
            return (prodX+prodY)/prodSq;
        
    }

}
