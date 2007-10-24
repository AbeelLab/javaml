/**
 * InstanceTools.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

import java.util.Random;

/**
 * Provides utility methods for manipulating, creating and modifying instances.
 * 
 * @see Instance
 * @see SimpleInstance
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class InstanceTools {

    /**
     * Random generator to create random instances.
     */
    private static Random rg = new Random(System.currentTimeMillis());

    /**
     * Creates a random instance with the given number of attributes. The values
     * of all attributes are between 0 and 1.
     * 
     * @param length
     *            the number of attributes in the instance.
     * @return a random instance
     */
    public static Instance randomInstance(int length) {
        double[] values = new double[length];
        for (int i = 0; i < values.length; i++) {
            values[i] = rg.nextDouble();
        }
        return new SimpleInstance(values);
    }
}
