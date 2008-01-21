/**
 * SimpleInstance.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

import net.sf.javaml.utils.MathUtils;
/**
 * 
 * 
 * {@jmlSource}
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 *
 */
public class SimpleInstance implements Instance {

    /**
     * 
     */
    private static final long serialVersionUID = -4602519840193131451L;

    private int hashCode = 0;

    private double[] values = null;;

    private boolean classSet = false;

    private int classValue = 0;

    private double weight = 1;

    /**
     * Copy constructor, this makes a deep copy of the Instance
     * 
     * @param instance
     */
    @Deprecated
    public SimpleInstance(Instance instance) {
        this(instance.toArray(), instance.weight(), instance.isClassSet(), instance.classValue());

    }

    /**
     * Copy constructor, this makes a deep copy of the Instance
     * 
     * @param instance
     */
    public SimpleInstance(SimpleInstance instance) {
        this(instance.values, instance.weight(), instance.isClassSet(), instance.classValue());

    }

    @Override
    public boolean equals(Object obj) {
        Instance tmp = (Instance) obj;
        boolean equal = values.length == tmp.size();
        int pos = 0;
        while (equal && pos < values.length) {
            equal = equal && MathUtils.eq(values[pos], tmp.value(pos));
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

    public SimpleInstance(double[] values) {
        this(values, 0);
    }

    public SimpleInstance(double[] values, int classValue) {
        this(values, 1.0, true, classValue);
    }

    public SimpleInstance(double[] values, int classValue, double weight) {
        this(values, weight, true, classValue);
    }

    /**
     * Create a new instance that has the weight and class value of the old
     * instance, but has new values.
     * 
     * @param values
     *            the new values
     * @param old
     *            the instance from which to take the weight, and class
     *            information.
     */
    public SimpleInstance(double[] values, Instance old) {
        this(values, old.weight(), old.isClassSet(), old.classValue());
    }

    private SimpleInstance(double[] values, double weight, boolean classSet, int classValue) {
        this.values = new double[values.length];
        System.arraycopy(values, 0, this.values, 0, values.length);
        this.weight = weight;
        this.classSet = classSet;
        this.classValue = classValue;
        this.hashCode = this.values.hashCode();

    }

    public double value(int index) {
        return values[index];
    }

    public int classValue() {
        return classValue;
    }

    public boolean isClassSet() {
        return classSet;
    }

    public boolean isCompatible(Instance instance) {
        return instance.size() == this.size();
    }

    public double weight() {
        return this.weight;
    }

    public void setWeight(float d) {
        this.weight = d;

    }

    public int size() {
        return values.length;
    }

    public double[] toArray() {
        double[] out = new double[values.length];
        System.arraycopy(this.values, 0, out, 0, this.values.length);
        return out;
    }

    public Complex getComplex(int index) {
        return new Complex(value(index), 0);
    }

    public Instance copy() {
        return new SimpleInstance(this);
    }

}
