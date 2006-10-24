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

    private Vector<Double> values = new Vector<Double>();

    private boolean classSet = false;

    private int classValue = 0;

    private double weigth = 1;

    

    private SimpleInstance(double weight){
    	this.weigth = weight;
    }
    /**
     * Copy constructor, this makes a deep copy of the Instance
     * @param instance
     */
    public SimpleInstance(Instance instance) {
		this(instance.getWeight());
		for(int i=0;i<instance.size();i++){
			values.add(instance.getValue(i));
		}
		this.setClassSet(instance.isClassSet());
		this.setClassValue(instance.getClassValue());
	}

	public SimpleInstance(double[] values) {
        this(values,1.0);
    }

    public SimpleInstance(double[] values, double weight) {
       this.values=new Vector<Double>();
        for(int i=0;i<values.length;i++){
            this.values.add(values[i]);
        }
        this.weigth=weight;
    }

    public Vector<Double> getVectorForm() {
        return new Vector<Double>(values);
    }

    public double[] getArrayForm() {
        double[] tmp = new double[values.size()];
        for(int i=0;i<tmp.length;i++){
            tmp[i]=values.get(i);
        }
        return tmp;
    }

    
    public double getValue(int index) {
        return values.get(index);
    }

    public void setValue(int index, double a) {
        values.set(index, a);

    }

    public int getClassValue() {
        return classValue;
    }

    public void setClassValue(int a) {
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
        return instance.size()==this.size();
        
        
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

