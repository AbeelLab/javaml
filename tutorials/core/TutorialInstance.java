/**
 * %SVN.HEADER%
 */
package tutorials.core;

import junit.framework.Assert;
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
 * For now we will only work with the simplest variants of Instance, namely a
 * {@link net.sf.javaml.core.DenseInstance} and a
 * {@link net.sf.javaml.core.SparseInstance}. Both are implementations of
 * {@link net.sf.javaml.core.Instance} that have double values as attributes and
 * optionally a class value.
 * 
 * {@jmlSource}
 * 
 * @see net.sf.javaml.core.Instance
 * @see net.sf.javaml.core.DenseInstance
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
	public void testDenseInstance() {
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
		Assert.assertEquals(instanceWithClassValue.classValue(), 1);

	}

	/**
	 * Shows how to construct a SparseInstance.
	 * 
	 * 
	 * 
	 */
	@org.junit.Test
	public void testSparseInstance() {
		/*
		 * Here we will create an instance with 10 attributes, but will only set
		 * the attributes with index 1,3 and 7 with a value.
		 */
		/* Create instance with 10 attributes */
		Instance instance = new SparseInstance(10);
		/* Set the values for particular attributes */
		instance.put(1, 1.0);
		instance.put(3, 2.0);
		instance.put(7, 4.0);
		

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
