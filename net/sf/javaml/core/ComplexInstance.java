/**
 * ComplexInstance.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

import net.sf.javaml.utils.MathUtils;

public class ComplexInstance implements Instance {

    private static final long serialVersionUID = 2364870063195994478L;

    private int hashCode = 0;

    private Complex[] values = null;;

    private boolean classSet = false;

    private int classValue = 0;

    private double weight = 1;

    @Override
    public boolean equals(Object obj) {
        Instance tmp = (Instance) obj;
        boolean equal = values.length == tmp.size();
        int pos = 0;
        while (equal && pos < values.length) {
            equal = equal && MathUtils.eq(values[pos], tmp.getComplex(pos));
            pos++;
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("[" + values[0]);
        for (int i = 1; i < values.length; i++) {
            out.append(";" + values[i]);
        }
        out.append(";w:" + this.weight);
        if (this.classSet) {
            out.append(";C:" + this.classValue);
        }
        out.append("]");
        return out.toString();
    }

    public ComplexInstance(Complex[] values) {
        this(values, 1.0f);
    }

    public ComplexInstance(Complex[] values, float weight) {
        this(values, weight, false, 0);
    }

    public ComplexInstance(Complex[] values, double weight, boolean classSet, int classValue) {
        this.values = new Complex[values.length];
        System.arraycopy(values, 0, this.values, 0, values.length);

        this.weight = weight;
        this.classSet = classSet;
        this.classValue = classValue;
        this.hashCode = this.values.hashCode();

    }

    public double getValue(int index) {
        return values[index].re;
    }

    public int getClassValue() {
        return classValue;
    }

    public boolean isClassSet() {
        return classSet;
    }

    public boolean isCompatible(Instance instance) {
        return instance.size() == this.size();
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(float d) {
        this.weight = d;

    }

    public int size() {
        return values.length;
    }

    public Complex getComplex(int index) {
        return values[index];
    }

    public double[] toArray() {
        throw new RuntimeException("Impossible to implement");
    }

}
