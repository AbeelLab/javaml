/**
 * %SVN.HEADER%
 */
package net.sf.javaml.tools;

import java.util.Random;

import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

/**
 * Provides utility methods for manipulating, creating and modifying instances.
 * 
 * 
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
     * Create an array representation of the instance attributes.
     * 
     * @param inst
     * @return
     */
    public static double[] array(Instance inst) {
        double[] out = new double[inst.noAttributes()];
        for (int i = 0; i < inst.noAttributes(); i++) {
            out[i] = inst.value(i);
        }
        return out;
    }

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
     * Creates a random instance with the given number of attributes. The values
     * of the attributes follow a normal distribution with mean 0 and std 1.
     * 
     * @param length
     *            the number of attributes in the instance.
     * @return a random instance
     */
    public static Instance randomGaussianInstance(int length) {
        double[] values = new double[length];
        for (int i = 0; i < values.length; i++) {
            values[i] = rg.nextGaussian();
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

    // /**
    // * Returns the same instance with a single attribute perturbed.
    // *
    // * @param i
    // * @param attIndex
    // * @return
    // */
    // public static void perturb(Instance i,int attIndex){
    // i.put(attIndex, Math.random());
    //        
    // // double[] values = i.toArray();
    // // values[attIndex]=Math.random();
    // // return new SimpleInstance(values,i);
    // }

}
