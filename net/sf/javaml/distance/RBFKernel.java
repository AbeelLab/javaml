/**
 * RBFKernel.java, 4-dec-2006
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

public class RBFKernel implements DistanceMeasure {

    private float variance=1;
    private NormDistance nd=new EuclideanDistance();
    public RBFKernel(){
        this(1);
    }
    public RBFKernel(float variance){
        this.variance=variance;
    }
    public double calculateDistance(Instance x, Instance y) {
       double norm=nd.calculateDistance(x,y);
       return Math.pow(Math.E,-(norm/this.variance));
     
    }

}
