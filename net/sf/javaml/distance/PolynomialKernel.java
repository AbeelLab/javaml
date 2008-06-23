/**
 * LinearKernel.java
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

public class PolynomialKernel implements DistanceMeasure {

    /**
     * 
     */
    private static final long serialVersionUID = 113839833688979121L;
    private double exponent=1;
    
    public PolynomialKernel(double exponent){
        this.exponent=exponent;
    }
    
    public double measure(Instance i, Instance j) {
        double result;
        result = dotProd(i, j);
        if (exponent != 1.0) {
            result = Math.pow(result, exponent);
        }
        return result;
    }

    /**
     * Calculates a dot product between two instances
     */
    protected final double dotProd(Instance inst1, Instance inst2) {

        double result = 0;

        for (int i = 0; i < inst1.noAttributes(); i++) {
            result += inst1.value(i) * inst2.value(i);
        }
        return result;
    }

    public boolean compare(double x, double y) {
        throw new UnsupportedOperationException("Not implemented");
    }

   

}
