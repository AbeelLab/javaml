/**
 * Instance.java
 *
 * %SVN.HEADER%
 */

package net.sf.javaml.core;

import java.io.Serializable;

/**
 * Provides an interface for all instances. An instance is a collection of
 * attributes that belong together.
 * 
 * Instances can be grouped together into a {@link Dataset}.
 * 
 * {jmlSource}
 * 
 * @see Dataset
 * 
 * @author Thomas Abeel
 * 
 */
public interface Instance extends Serializable {

    /**
     * Converts the instance to a value array. If this is not supported by the
     * implementation, <code>null</code> should be returned.
     * 
     * @return the attributes of this instance
     */
    @Deprecated
    public double[] toArray();

    /**
     * Returns the value at a given index. If the implementation does not
     * support positional access, an arbitrary value can be returned.
     * 
     * @param index
     *            the attribute index
     * @return the value of the attribute on the supplied index
     */
    public double getValue(int index);

    /**
     * Returns the complex values at a given index.
     * 
     */
    public Complex getComplex(int index);

    /**
     * Return the class value of this instance. If the method
     * <code>isClassSet</code> returns false, the output of this method is not
     * defined.
     * 
     * @return the class value of this instance
     */
    public int getClassValue();

    /**
     * Return whether the class of this instance is set.
     * 
     * @return true if the class is set, false in other cases
     */
    public boolean isClassSet();

    /**
     * Returns whether this instance is compatible with the supplied instance.
     * For two instances to be compatible they should have the same number of
     * attributes. Preferably the attributes should also be of the same type.
     * 
     * @param i
     *            the instance to check the compatibility with
     * @return true if the two instances are compatible, false if they are
     *         incompatible
     */
    public boolean isCompatible(Instance i);

    /**
     * Returns the weight of this instance.
     * 
     * @return the weight of the instance
     */
    public double getWeight();

    /**
     * This method return the number of attributes this instance has.
     * 
     * @return the number of attributes
     */
    public int size();

}
