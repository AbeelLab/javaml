/**
 * MathUtils.java
 *
 * This file is part of the Java Machine Learning API
 * 
 * The Java Machine Learning API is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The Java Machine Learning API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning API; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.utils;

/**
 * A class that provides some utility math methods. - Comparing doubles for
 * equality and order with a precision.
 * 
 * @author Thomas Abeel
 * 
 */
public class MathUtils {

    private static final double epsilon = 1e-6;

    /**
     * Tests if a is equal to b.
     * 
     * @param a
     *            a double
     * @param b
     *            a double
     */
    public static boolean eq(double a, double b) {
        return (a - b < epsilon) && (b - a < epsilon);
    }

    /**
     * Tests if a is smaller or equal to b.
     * 
     * @param a
     *            a double
     * @param b
     *            a double
     */
    public static boolean le(double a, double b) {
        return (a - b < epsilon);
    }

    /**
     * Tests if a is greater or equal to b.
     * 
     * @param a
     *            a double
     * @param b
     *            a double
     */
    public static boolean ge(double a, double b) {
        return (b - a < epsilon);
    }

    /**
     * Tests if a is smaller than b.
     * 
     * @param a
     *            a double
     * @param b
     *            a double
     */
    public static boolean lt(double a, double b) {
        return (b - a > epsilon);
    }

    /**
     * Tests if a is greater than b.
     * 
     * @param a
     *            a double
     * @param b
     *            a double
     */
    public static boolean gt(double a, double b) {
        return (a - b > epsilon);
    }

    /**
     * XXX DOC
     * 
     * @param me1
     * @param me2
     * @return
     */
    public static boolean eq(float[] me1, float[] me2) {
        if(me1.length!=me2.length)
            return false;
        for(int i=0;i<me1.length;i++){
            if(me1[i]!=me2[i])
                return false;
        }
        return true;
    }

    /**
     * XXX DOC
     * 
     * @param array
     * @return
     */
    public static float min(float[] array) {
        float min=array[0];
        for(int i=1;i<array.length;i++){
           if(array[i]<min)
                min=array[i];
        }
        return min;
    }

    
}
