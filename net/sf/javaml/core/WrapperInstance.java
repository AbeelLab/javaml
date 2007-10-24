/**
 * WrapperInstance.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

/**
 * The most basic instance.
 * 
 * Only provides the very basics of an instance.
 * 
 * {@jmlSource}
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class WrapperInstance implements Instance {

    private static final long serialVersionUID = 5643121190524658288L;

    /**
     * Pointer to the array with the data of the instance.
     */
    private double[] data;

    public int getClassValue() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * Creates an instance from a array.
     * 
     * @param data
     */
    public WrapperInstance(double[] data) {
        this.data = data;
    }

    public double getValue(int index) {
        return data[index];
    }

    public double getWeight() {
        return 1;
    }

    public boolean isClassSet() {
        return false;
    }

    public boolean isCompatible(Instance i) {
        return i.size() == this.size();
    }

    public int size() {
        return data.length;
    }

    public double[] toArray() {
        return data;
    }

    public Complex getComplex(int index) {
        return new Complex(getValue(index), 0);
    }

}
