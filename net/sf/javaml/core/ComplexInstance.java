/**
 * ComplexInstance.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

import net.sf.javaml.utils.MathUtils;

/**
 * Provides an implementation of the {@link Instance} interface that has
 * {@link Complex} numbers as attributes.
 * 
 * {@jmlSource}
 * 
 * @see Complex
 * @see Instance
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class ComplexInstance implements Instance {

    private static final long serialVersionUID = 2364870063195994478L;

    /**
     * Cached hashCode value of this instance.
     */
    private int hashCode = 0;

    /**
     * The attributes of this instance
     */
    private Complex[] values = null;;

    /**
     * Flag to indicate whether the class value of this instance is set
     */
    private boolean classSet = false;

    /**
     * The class value of this instance, this is only relevant if classSet==true
     */
    private int classValue = 0;

    /**
     * The weight of this instance
     */
    private double weight = 1;

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * {@inheritDoc}
     */
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

    public ComplexInstance(Complex[] values, double weight) {
        this(values, weight, false, 0);
    }

    public ComplexInstance(Complex[] values, double weight, int classValue) {
        this(values, weight, true, classValue);
    }

    private ComplexInstance(Complex[] values, double weight, boolean classSet, int classValue) {
        this.values = new Complex[values.length];
        System.arraycopy(values, 0, this.values, 0, values.length);
        this.weight = weight;
        this.classSet = classSet;
        this.classValue = classValue;
        this.hashCode = this.values.hashCode();

    }

    /**
     * Returns the real value of the attribute with the given index.
     * 
     * @param index
     *            the index of the attribute
     * @return the real part of the attribute on the supplied index
     */
    public double value(int index) {
        return values[index].re;
    }

    /**
     * {@inheritDoc}
     */
    public int classValue() {
        return classValue;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isClassSet() {
        return classSet;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCompatible(Instance instance) {
        return instance.size() == this.size();
    }

    /**
     * {@inheritDoc}
     */
    public double weight() {
        return this.weight;
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return values.length;
    }

    /**
     * {@inheritDoc}
     */
    public Complex getComplex(int index) {
        return values[index];
    }

    /**
     * {@inheritDoc}
     */
    public double[] toArray() {
        return null;
    }

    public Instance copy() {
       return new ComplexInstance(this.values,this.weight,this.classSet,this.classValue);
    }

}
