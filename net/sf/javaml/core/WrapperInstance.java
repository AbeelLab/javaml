/**
 * WrapperInstance.java
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

/**
 * The most basic instance imaginable.
 * 
 * Only provides the very basics of an instance.
 * 
 * @author Thomas Abeel
 * 
 */
public class WrapperInstance implements Instance {

    private static final long serialVersionUID = 5643121190524658288L;

    /**
     * Pointer to the array with the data of the instance.
     */
    private double[] data;

    public int getClassValue() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * Creates an instance from a array.
     * 
     * @param data
     */
    public WrapperInstance(double[] data) {
        this.data = data;
    }

    public double getValue(int index) {
        return data[index];
    }

    public double getWeight() {
        return 1;
    }

    public boolean isClassSet() {
        return false;
    }

    public boolean isCompatible(Instance i) {
        return i.size() == this.size();
    }

    public int size() {
        return data.length;
    }

    public double[] toArray() {
        return data;
    }

}
