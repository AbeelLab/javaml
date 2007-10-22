/**
 * InstanceTools.java
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
package net.sf.javaml.core;

import java.util.Random;
/**
 * Provides utility methods for manipulating, creating and modifying instances.
 * 
 * @see Instance
 * @see SimpleInstance
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
