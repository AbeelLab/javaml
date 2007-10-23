/**
 * CreatingAnInstance.java
 *
 * This file is part of the Java Machine Learning Library
 * 
 * The Java Machine Learning Library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The Java Machine Learning Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning Library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.tutorials;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.InstanceTools;
import net.sf.javaml.core.SimpleInstance;

/**
 * This tutorial shows the very first step in using Java-ML. It will show you
 * how to create an Instance that can later be used in a Dataset and in the
 * machine learning algorithms.
 * 
 * For now we will only work with the simplest variant of Instance, namely a
 * SimpleInstance. This is an instance that has double values as attributes, a
 * weight and optionally a class value.
 * 
 * @see Instance
 * @see SimpleInstance
 * @see InstanceTools
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

        // If you wish to supply a weight for your instance, you can use the
        // two argument version of the constructor that takes a weight as a
        // second argument. The value for the weight should be between 0 and 1.
        Instance instanceWithWeight = new SimpleInstance(values, 0.5);

        System.out.println("Instance with weight set to 0.5: ");
        System.out.println(instanceWithWeight);
        System.out.println();

        // To create instances that have a class value set, you can use the 3
        // argument constructor which takes the values, weight and class value
        // as parameters.
        Instance instanceWithClassValue = new SimpleInstance(values, 0.5, 1);

        System.out.println("Instance with weight set to 0.5 and class value set to 1: ");
        System.out.println(instanceWithClassValue);
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
