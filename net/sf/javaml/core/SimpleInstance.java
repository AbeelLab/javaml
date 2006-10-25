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

    private double weight = 1;

    /**
     * Copy constructor, this makes a deep copy of the Instance
     * @param instance
     */
    public SimpleInstance(Instance instance) {
        this(instance.getArrayForm(),instance.getWeight(),instance.isClassSet(),instance.getClassValue());
		
	}

	public SimpleInstance(double[] values) {
        this(values,1.0);
    }

    public SimpleInstance(double[] values, double weight) {
       this(values,weight,false,0);
    }
    
    public SimpleInstance(double [] values,double weight,boolean classSet,int classValue){
        this.values=new Vector<Double>();
        for(int i=0;i<values.length;i++){
            this.values.add(values[i]);
        }
        this.weight=weight;
        this.classSet=classSet;
        this.classValue=classValue;
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

   

    public int getClassValue() {
        return classValue;
    }

  

    public boolean isClassSet() {
        return classSet;
    }

    public boolean isCompatible(Instance instance) {
        return instance.size()==this.size();
        
        
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double d) {
        this.weight = d;

    }

    public int size() {
       return values.size();
    }

}

