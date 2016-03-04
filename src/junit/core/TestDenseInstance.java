/**
 * %SVN.HEADER%
 */
package junit.core;

import junit.framework.Assert;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;

import org.junit.Test;

public class TestDenseInstance {
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
    @Test
    public void testConstructors() {
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
        /* Check whether the values actually correspond to what we put in there */
        for (int i = 0; i < values.length; i++)
            Assert.assertEquals(instance.value(i), values[i]);

        /*
         * To create instances that have a class value set, you can use the two
         * argument constructor which takes the values and the class value as
         * parameters.
         */
        Instance instanceWithClassValue = new DenseInstance(values, 1);

        System.out.println("Instance with class value set to 1: ");
        System.out.println(instanceWithClassValue);
        System.out.println();
        /* Check whether attribute is actually one */
        Assert.assertEquals(instanceWithClassValue.classValue(), 1);

    }
}
