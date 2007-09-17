/**
 * Instance.java
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

package net.sf.javaml.core;

import java.io.Serializable;

public interface Instance extends Serializable {

    /**
     * This method will convert the instance to a value array. If this is not
     * supported by the implementation, <code>null</code> should be returned.
     * 
     * @return
     */
    @Deprecated
    public double[] toArray();

    /**
     * This method returns the value at a given index. If the implementation
     * does not support positional access, an arbitrary value can be returned.
     * 
     * @param index
     * @return
     */
    public double getValue(int index);

    /**
     * This method returns the complex values at a given index.
     * 
     */
    public Complex getComplex(int index);
    
    /**
     * This method return the class value of this instance. If the method
     * <code>isClassMissing</code> returns true, the output of this method is
     * not defined.
     * 
     * @return the class value of this instance
     */
    public int getClassValue();

    public boolean isClassSet();

    public boolean isCompatible(Instance i);

    public double getWeight();

    /**
     * This method return the number of values (attributes) this instance has.
     * 
     * @return the number of values
     */
    public int size();

}
