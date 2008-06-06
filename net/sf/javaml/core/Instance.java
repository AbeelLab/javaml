package net.sf.javaml.core;

import java.util.Map;

public interface Instance extends Map<Integer, Double> {
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
}
