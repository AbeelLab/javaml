/*
 * AbstractInstance.java 
 * -----------------------
 * Copyright (C) 2008  Thomas Abeel
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Author: Thomas Abeel
 */
package net.sf.javaml.core;

public abstract class AbstractInstance implements Instance {

    private Object classValue;

    protected AbstractInstance() {
        this(null);
    }

    protected AbstractInstance(Object classValue) {
        this.classValue = classValue;
    }

    @Override
    public Object classValue() {
        return classValue;
    }

    @Override
    public void setClassValue(Object classValue) {
        this.classValue = classValue;
    }

    @Override
    public Instance minus(Instance min) {
        Instance out = new SparseInstance();
        for (Integer i : Sets.intersection(this.keySet(), min.keySet())) {
            out.put(i, this.get(i) - min.get(i));
        }
        return out;

    }

    @Override
    public Instance minus(double min) {
        Instance out = new SparseInstance();
        for (Integer i : this.keySet()) {
            out.put(i, this.get(i) - min);
        }
        return out;

    }

    @Override
    public Instance divide(double min) {
        Instance out = new SparseInstance();
        for (Integer i : this.keySet()) {
            out.put(i, this.get(i) / min);
        }
        return out;
    }
    @Override
    public Instance multiply(double value) {
        Instance out = new SparseInstance();
        for (Integer i : this.keySet()) {
            out.put(i, this.get(i) * value);
        }
        return out;
    }

    @Override
    public Instance divide(Instance min) {
        Instance out = new SparseInstance();
        for (Integer i : Sets.intersection(this.keySet(), min.keySet())) {
            out.put(i, this.get(i) / min.get(i));
        }
        return out;
    }

    @Override
    public Instance plus(double min) {
        Instance out = new SparseInstance();
        for (Integer i : this.keySet()) {
            out.put(i, this.get(i) + min);
        }
        return out;
    }

    @Override
    public Instance plus(Instance min) {
        Instance out = new SparseInstance();
        for (Integer i : Sets.intersection(this.keySet(), min.keySet())) {
            out.put(i, this.get(i) + min.get(i));
        }
        return out;
    }
}
