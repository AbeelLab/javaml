/**
 * %SVN.HEADER%
 */
package tutorials.core;

import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

/**
 * This tutorial shows the very first step in using Java-ML. It will show you
 * how to create an {@link net.sf.javaml.core.Instance} that can later be used
 * in a {@link net.sf.javaml.core.Dataset} and in the machine learning
 * algorithms.
 * 
 * In this class we only work with the {@link net.sf.javaml.core.DenseInstance}.
 * This type of instance has a value for each attribute and has an optional
 * class label.
 * 
 * {@jmlSource}
 * 
 * @see net.sf.javaml.core.Instance
 * @see net.sf.javaml.core.DenseInstance
 * @see net.sf.javaml.tools.InstanceTools
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialDenseInstance {

    /**
     * Shows how to construct an instance.
     * 
     * Here we will construct an instance with 10 attributes.
     * 
     */
    public static void main(String[] args) {
        /* values of the attributes. */
        double[] values = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

        /*
         * The simplest incarnation of the DenseInstance constructor will only
         * take a double array as argument an will create an instance with given
         * values as attributes and no class value set. For unsupervised machine
         * learning techniques this is probably the most convenient constructor.
         */
        Instance instance = new DenseInstance(values);

        System.out.println("Instance with only values set: ");
        System.out.println(instance);
        System.out.println();
        /*
         * To create instances that have a class value set, you can use the two
         * argument constructor which takes the values and the class value as
         * parameters.
         */
        Instance instanceWithClassValue = new DenseInstance(values, 1);

        System.out.println("Instance with class value set to 1: ");
        System.out.println(instanceWithClassValue);
        System.out.println();

    }

}
