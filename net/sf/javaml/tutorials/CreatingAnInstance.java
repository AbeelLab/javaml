/**
 * CreatingAnInstance.java
 *
 * %SVN.HEADER%
 * 
 */
package net.sf.javaml.tutorials;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.InstanceTools;
import net.sf.javaml.core.SimpleInstance;

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
public class CreatingAnInstance {

    public static void main(String[] args) {
        // We will create an instance with 10 attributes.

        // values of the attributes.
        double[] values = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

        // The simplest incarnation of the SimpleInstance constructor will only
        // take a double array as argument an will create an instance with give
        // values as attributes and no class value set. The instance will have a
        // default weight of 1. For unsupervised machine learning techniques
        // this is probably the most convenient constructor.
        Instance instance = new SimpleInstance(values);

        System.out.println("Instance with only values set (notice that the weight is by default set to 1): ");
        System.out.println(instance);
        System.out.println();

        // To create instances that have a class value set, you can use the two
        // argument constructor which takes the values and the class value
        // as parameters.
        Instance instanceWithClassValue = new SimpleInstance(values, 1);

        System.out.println("Instance with class value set to 1: ");
        System.out.println(instanceWithClassValue);
        System.out.println();

        // If you wish to supply a weight for your instance, you can use the
        // three argument version of the constructor that takes the class value
        // as second and the weight as the
        // third argument. The value for the weight should be between 0 and 1.
        Instance instanceWithWeight = new SimpleInstance(values, 1, 0.5);

        System.out.println("Instance with weight set to 0.5 and class value to 1: ");
        System.out.println(instanceWithWeight);
        System.out.println();

        // Finally the InstanceTools class offers a utility method to generate
        // random instances with a certain length. We will create a random
        // instance with 5 attributes. The values of all attributes will be
        // between 0 and 1.

        Instance random = InstanceTools.randomInstance(5);

        System.out.println("Random instance:");
        System.out.println(random);
        System.out.println();

    }

}
