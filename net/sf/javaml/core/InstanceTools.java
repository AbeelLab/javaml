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
 * {@jmlSource}
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
        return new DenseInstance(values);
    }

    /**
     * Checks if there are any missing values in the instance. Missing values
     * are denoted by Double.NaN.
     * 
     * @param inst
     *            the instance to check for missing values
     * @return true is the instance contains missing values, false in other
     *         cases.
     */
    public static boolean hasMissingValues(Instance inst) {
        for (int i = 0; i < inst.noAttributes(); i++) {
            if (Double.isNaN(inst.value(i)))
                return true;

        }
        return false;
    }
    
//    /**
//     * Returns the same instance with a single attribute perturbed.
//     * 
//     * @param i
//     * @param attIndex
//     * @return
//     */
//    public static void perturb(Instance i,int attIndex){
//        i.put(attIndex, Math.random());
//        
////        double[] values = i.toArray();
////        values[attIndex]=Math.random();
////        return new SimpleInstance(values,i);
//    }

}
