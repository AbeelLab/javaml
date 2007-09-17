/**
 * GeneInstance.java
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
package net.sf.javaml.tools.bioinformatics;

import net.sf.javaml.core.Complex;
import net.sf.javaml.core.Instance;

public class GeneInstance implements Instance {

    /**
     * 
     */
    private static final long serialVersionUID = -8665589309601582182L;
    private String name;

    public GeneInstance(String name) {
        this.name = name;
    }

    public boolean equals(Object o){
        return this.name.equals(o.toString());
    }
    public int hashCode(){
        return this.name.hashCode();
    }
    public String toString(){
        return name;
    }
    public int getClassValue() {
        return 0;
    }

    public double getValue(int index) {
        return 0;
    }

    public double getWeight() {
        return 1;
    }

    public boolean isClassSet() {
        return false;
    }

    public boolean isCompatible(Instance i) {
        return i instanceof GeneInstance;
    }

    public int size() {
        return 0;
    }

    public double[] toArray() {
        return null;
    }

    public Complex getComplex(int index) {
        return new Complex(getValue(index),0);
    }

}
