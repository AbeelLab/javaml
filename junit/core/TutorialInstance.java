/**
 * %SVN.HEADER%
 */
package junit.core;

import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;

import org.junit.Test;

/**
 * This tutorial shows the very first step in using Java-ML. It will show you
 * how to create an {@link net.sf.javaml.core.Instance} that can later be used
 * in a {@link net.sf.javaml.core.Dataset} and in the machine learning
 * algorithms.
 * 
 * For now we will only work with the simplest variant of Instance, namely a
 * {@link net.sf.javaml.core.SimpleInstance}. This is an
 * {@link net.sf.javaml.core.Instance} that has double values as attributes, a
 * weight and optionally a class value.
 * 
 * {@jmlSource}
 * 
 * @see net.sf.javaml.core.Instance
 * @see net.sf.javaml.core.SimpleInstance
 * @see net.sf.javaml.core.InstanceTools
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialInstance {

    /**
     * Shows how to construct an instance.
     * 
     * Here we will construct an instance with 10 attributes.
     * 
     */
    @org.junit.Test
    public void testConstructor() {
        /* values of the attributes. */
        double[] values = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

        /*
         * The simplest incarnation of the SimpleInstance constructor will only
         * take a double array as argument an will create an instance with give
         * values as attributes and no class value set. The instance will have a
         * default weight of 1. For unsupervised machine learning techniques
         * this is probably the most convenient constructor.
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

    /**
     * Shows the use of the <code>remove(int index)</code> method on an Instance
     */
    @Test
    public void testRemoveAttribute() {
        double[] values = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Instance instance = new DenseInstance(values);
        System.out.println(instance);
        instance.removeAttribute(4);
        System.out.println(instance);

        instance = new SparseInstance(10);
        instance.put(1, 1.0);
        instance.put(2, 2.0);
        instance.put(4, 4.0);
        instance.put(5, 5.0);
        instance.put(6, 6.0);
        instance.put(8, 8.0);
        System.out.println(instance);
        instance.removeAttribute(5);
        System.out.println(instance);

    }

}
