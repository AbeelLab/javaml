/**
 * Instance.java, 11-okt-06
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

package net.sf.javaml.core;

import java.util.Vector;

public interface Instance {

    public Vector<Number> getVectorForm();

    public Number[] getArrayForm();

    public Number getValue(int index);

    public void setValue(int index, Number a);

    /**
     * This method return the class value of this instance. If the method
     * <code>isClassMissing</code> returns true, the output of this method is
     * not defined.
     * 
     * @return the class value of this instance
     */
    public Number getClassValue();

    /**
     * Set the class value of this instance. This will also set the status of the class of this instance (set,
     * unset) to <code>set</code>.
     * 
     * @param a the actual value for the classvalue
     */
    public void setClassValue(Number a);

    /**
     * Set whether the class value of this instance is known or not. 
     * 
     * @param b
     *            When b is true than the class value is known, if b is false,
     *            the class value is unknown.
     */
    public void setClassSet(boolean b);

    public boolean isClassSet();

    public boolean isCompatible(Instance i);

    public double getWeight();

    public void setWeight(double d);
    /**
     * This method return the number of values (attributes) this instance has.
     * @return the number of values
     */
    public int size();

}
