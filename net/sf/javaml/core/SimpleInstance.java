/**
 * SimpleInstance.java, 23-okt-2006
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

public class SimpleInstance implements Instance {

    private Vector<Number> values = new Vector<Number>();

    private boolean classSet = false;

    private Number classValue = new Integer(0);

    private double weigth = 1;

    public SimpleInstance() {
        this(1);
    }

    public SimpleInstance(double weight) {
        this.weigth = weight;
    }

    public Vector<Number> getVectorForm() {
        return new Vector<Number>(values);
    }

    public Number[] getArrayForm() {
        Number[] tmp = new Number[values.size()];
        values.toArray(tmp);
        return tmp;
    }

    public Number getValue(int index) {
        return values.get(index);
    }

    public void setValue(int index, Number a) {
        values.set(index, a);

    }

    public Number getClassValue() {
        return classValue;
    }

    public void setClassValue(Number a) {
        classValue = a;
        classSet = true;

    }

    public void setClassSet(boolean b) {
        classSet = b;

    }

    public boolean isClassSet() {
        return classSet;
    }

    public boolean isCompatible(Instance instance) {
        boolean tmp=true;
        tmp=instance.size()==this.size();
        for(int i=0;tmp&&i<this.size();i++){
            tmp=this.getValue(i).getClass().equals(instance.getValue(i).getClass());
        }
        return tmp;
        
    }

    public double getWeight() {
        return this.weigth;
    }

    public void setWeight(double d) {
        this.weigth = d;

    }

    public int size() {
       return values.size();
    }

}

