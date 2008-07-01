/**
 * Instance.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

import java.io.Serializable;
import java.util.Map;

/**
 * The interface for instances in a data set.
 * 
 * {@jmlSource}
 * 
 * @see Dataset
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public interface Instance extends Map<Integer, Double>, Iterable<Double>, Serializable {
    /**
     * Returns the class value for this instance.
     * 
     * @return class value of this instance, or null if the class is not set
     */
    public Object classValue();

    public void setClassValue(Object value);

    // /**
    // * Returns the number of attributes this instance has.
    // *
    // * @return
    // */
    // public int noAttributes();

    @Override
    @Deprecated
    public int size();

    public double value(int pos);

    /**
     * Subtract an instance from this instance and returns the results.
     * 
     * This method does not modify this instance, but returns the result.
     * 
     * @param the
     *            result of the subtraction
     */
    public Instance minus(Instance min);

    /**
     * Subtract an scalar from this instance and returns the results.
     * 
     * This method does not modify this instance, but returns the result.
     * 
     * @param the
     *            result of the subtraction
     */
    public Instance minus(double value);

    public Instance plus(Instance max);

    public Instance divide(double value);

    public Instance divide(Instance currentRange);

    public Instance plus(double value);

    public int noAttributes();

    public Instance multiply(double value);

    public Instance multiply(Instance value);

    /**
     * Removes attribute from the instance
     * 
     * @param i
     */
    public void removeAttribute(int i);

    /**
     * Take square root of all attributes.
     * 
     * @return instance with a the square root for all attributes
     */
    public Instance sqrt();

    /**
     * Return unique identifier for this instance.
     * 
     * @return
     */
    public int getID();

}
