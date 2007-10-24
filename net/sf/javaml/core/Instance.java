/**
 * Instance.java
 *
 * %SVN.HEADER%
 */

package net.sf.javaml.core;

import java.io.Serializable;

public interface Instance extends Serializable {

    /**
     * This method will convert the instance to a value array. If this is not
     * supported by the implementation, <code>null</code> should be returned.
     * 
     * @return
     */
    @Deprecated
    public double[] toArray();

    /**
     * This method returns the value at a given index. If the implementation
     * does not support positional access, an arbitrary value can be returned.
     * 
     * @param index
     * @return
     */
    public double getValue(int index);

    /**
     * This method returns the complex values at a given index.
     * 
     */
    public Complex getComplex(int index);

    /**
     * This method return the class value of this instance. If the method
     * <code>isClassMissing</code> returns true, the output of this method is
     * not defined.
     * 
     * @return the class value of this instance
     */
    public int getClassValue();

    public boolean isClassSet();

    public boolean isCompatible(Instance i);

    public double getWeight();

    /**
     * This method return the number of values (attributes) this instance has.
     * 
     * @return the number of values
     */
    public int size();

}
