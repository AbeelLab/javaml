package net.sf.javaml.core;

import java.util.Map;

public interface Instance extends Map<Integer, Double> {
    /**
     * Returns the class value for this instance.
     * 
     * @return class value of this instance, or null if the class is not set
     */
    public Object classValue();

    /**
     * Returns the number of attributes this instance has.
     * 
     * @return
     */
    public int noAttributes();

    public double value(int pos);
}
