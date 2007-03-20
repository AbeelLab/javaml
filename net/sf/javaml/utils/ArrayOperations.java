/**
 * ArrayOperations.java
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

import java.util.Random;

public class ArrayOperations {
    /**
     * Computes the scalar product of this vector with a scalar
     * 
     * @param s
     *            the scalar
     */
    public static void scalarMultiply(double s, float[] array) {

        if (array != null) {
            int n = array.length;

            for (int i = 0; i < n; i++) {
                array[i] = (float) s * array[i];
            }
        }
    }

    /**
     * Returns the sum of two array.
     * 
     * This method asssumes that the two arrays are of equal length.
     * 
     * @return an array containing the sum.
     */
    public static float[] add(float[] a, float[] b) {

        float[] out = new float[a.length];

        for (int i = 0; i < a.length; i++) {
            out[i] = a[i] + b[i];
        }

        return out;
    }

    /**
     * Returns the norm of the vector
     * 
     * @return the norm of the vector
     */
    public static double norm(float[] array) {

        if (array != null) {
            int n = array.length;
            double sum = 0.0;

            for (int i = 0; i < n; i++) {
                sum += array[i] * array[i];
            }
            return Math.pow(sum, 0.5);
        } else
            return 0.0;
    }

    public static void changeLength(double len, float[] array) {

        double factor = norm(array);
        factor = len / factor;
        scalarMultiply(factor, array);
    }

    /**
     * Sorts a given array of doubles in ascending order and returns an array of
     * integers with the positions of the elements of the original array in the
     * sorted array. NOTE THESE CHANGES: the sort is no longer stable and it
     * doesn't use safe floating-point comparisons anymore. Occurrences of
     * Double.NaN are treated as Double.MAX_VALUE
     * 
     * @param array
     *            this array is not changed by the method!
     * @return an array of integers with the positions in the sorted array.
     */
    public static/* @pure@ */int[] sort(/* @non_null@ */double[] array) {

        int[] index = new int[array.length];
        array = (double[]) array.clone();
        for (int i = 0; i < index.length; i++) {
            index[i] = i;
            if (Double.isNaN(array[i])) {
                array[i] = Double.MAX_VALUE;
            }
        }
        quickSort(array, index, 0, array.length - 1);
        return index;
    }

    /**
     * Implements quicksort according to Manber's "Introduction to Algorithms".
     * 
     * @param array
     *            the array of doubles to be sorted
     * @param index
     *            the index into the array of doubles
     * @param left
     *            the first index of the subset to be sorted
     * @param right
     *            the last index of the subset to be sorted
     */
    // @ requires 0 <= first && first <= right && right < array.length;
    // @ requires (\forall int i; 0 <= i && i < index.length; 0 <= index[i] &&
    // index[i] < array.length);
    // @ requires array != index;
    // assignable index;
    private static void quickSort(/* @non_null@ */double[] array, /* @non_null@ */int[] index, int left, int right) {

        if (left < right) {
            int middle = partition(array, index, left, right);
            quickSort(array, index, left, middle);
            quickSort(array, index, middle + 1, right);
        }
    }

    /**
     * Implements quicksort according to Manber's "Introduction to Algorithms".
     * 
     * @param array
     *            the array of integers to be sorted
     * @param index
     *            the index into the array of integers
     * @param left
     *            the first index of the subset to be sorted
     * @param right
     *            the last index of the subset to be sorted
     */
    // @ requires 0 <= first && first <= right && right < array.length;
    // @ requires (\forall int i; 0 <= i && i < index.length; 0 <= index[i] &&
    // index[i] < array.length);
    // @ requires array != index;
    // assignable index;
    private static void quickSort(/* @non_null@ */int[] array, /* @non_null@ */int[] index, int left, int right) {

        if (left < right) {
            int middle = partition(array, index, left, right);
            quickSort(array, index, left, middle);
            quickSort(array, index, middle + 1, right);
        }
    }

    /**
     * Partitions the instances around a pivot. Used by quicksort and
     * kthSmallestValue.
     * 
     * @param array
     *            the array of integers to be sorted
     * @param index
     *            the index into the array of integers
     * @param l
     *            the first index of the subset
     * @param r
     *            the last index of the subset
     * 
     * @return the index of the middle element
     */
    private static int partition(int[] array, int[] index, int l, int r) {

        double pivot = array[index[(l + r) / 2]];
        int help;

        while (l < r) {
            while ((array[index[l]] < pivot) && (l < r)) {
                l++;
            }
            while ((array[index[r]] > pivot) && (l < r)) {
                r--;
            }
            if (l < r) {
                help = index[l];
                index[l] = index[r];
                index[r] = help;
                l++;
                r--;
            }
        }
        if ((l == r) && (array[index[r]] > pivot)) {
            r--;
        }

        return r;
    }

    /**
     * Partitions the instances around a pivot. Used by quicksort and
     * kthSmallestValue.
     * 
     * @param array
     *            the array of doubles to be sorted
     * @param index
     *            the index into the array of doubles
     * @param l
     *            the first index of the subset
     * @param r
     *            the last index of the subset
     * 
     * @return the index of the middle element
     */
    private static int partition(double[] array, int[] index, int l, int r) {

        double pivot = array[index[(l + r) / 2]];
        int help;

        while (l < r) {
            while ((array[index[l]] < pivot) && (l < r)) {
                l++;
            }
            while ((array[index[r]] > pivot) && (l < r)) {
                r--;
            }
            if (l < r) {
                help = index[l];
                index[l] = index[r];
                index[r] = help;
                l++;
                r--;
            }
        }
        if ((l == r) && (array[index[r]] > pivot)) {
            r--;
        }

        return r;
    }

    public static void fillRandom(float[] array, Random rg) {
        for (int i = 0; i < array.length; i++) {
            array[i] = (float) rg.nextDouble();
        }

    }
    /**
     * Computes the sum of the elements of an array of doubles.
     *
     * @param doubles the array of double
     * @return the sum of the elements
     */
    public static /*@pure@*/ double sum(double[] doubles) {

      double sum = 0;

      for (int i = 0; i < doubles.length; i++) {
        sum += doubles[i];
      }
      return sum;
    }
    /**
     * Substract the second array from the first one and returns the result.
     * 
     * @param a
     *            the first array
     * @param b
     *            the second array
     * @return the substraction of the second minus the first array
     */
    public static float[] substract(float[] a, float[] b) {
        float[] out = new float[a.length];

        for (int i = 0; i < a.length; i++) {
            out[i] = b[i] - a[i];
        }

        return out;
    }
}
