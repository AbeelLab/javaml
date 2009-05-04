/**
 * %SVN.HEADER%
 */
package net.sf.javaml.utils;

import java.util.Random;

public class ArrayUtils {
    /**
     * Computes the scalar product of this vector with a scalar
     * 
     * @param s
     *            the scalar
     */
    public static void scalarMultiply(double s, double[] array) {

        if (array != null) {
            int n = array.length;

            for (int i = 0; i < n; i++) {
                array[i] = s * array[i];
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
    public static double[] add(double[] a, double[] b) {

        double[] out = new double[a.length];

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
    public static double norm(double[] array) {

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

    public static void changeLength(double len, double[] array) {

        double factor = norm(array);
        factor = len / factor;
        scalarMultiply(factor, array);
    }

    /**
     * Sorts a given array of doubles in ascending order and returns an array of
     * integers with the positions of the elements of the original array in the
     * sorted array.
     * 
     * NOTE: this array is not changed by the method!
     * 
     * @param array
     *            this array is not changed by the method!
     * @return an array of integers with the positions in the sorted array.
     */
    public static int[] sort(double[] array) {

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

    // /**
    // * Implements quicksort according to Manber's "Introduction to
    // Algorithms".
    // *
    // * @param array
    // * the array of integers to be sorted
    // * @param index
    // * the index into the array of integers
    // * @param left
    // * the first index of the subset to be sorted
    // * @param right
    // * the last index of the subset to be sorted
    // */
    // // @ requires 0 <= first && first <= right && right < array.length;
    // // @ requires (\forall int i; 0 <= i && i < index.length; 0 <= index[i]
    // &&
    // // index[i] < array.length);
    // // @ requires array != index;
    // // assignable index;
    // private static void quickSort(/* @non_null@ */int[] array, /* @non_null@
    // */int[] index, int left, int right) {
    //
    // if (left < right) {
    // int middle = partition(array, index, left, right);
    // quickSort(array, index, left, middle);
    // quickSort(array, index, middle + 1, right);
    // }
    // }

    // /**
    // * Partitions the instances around a pivot. Used by quicksort and
    // * kthSmallestValue.
    // *
    // * @param array
    // * the array of integers to be sorted
    // * @param index
    // * the index into the array of integers
    // * @param l
    // * the first index of the subset
    // * @param r
    // * the last index of the subset
    // *
    // * @return the index of the middle element
    // */
    // private static int partition(int[] array, int[] index, int l, int r) {
    //
    // double pivot = array[index[(l + r) / 2]];
    // int help;
    //
    // while (l < r) {
    // while ((array[index[l]] < pivot) && (l < r)) {
    // l++;
    // }
    // while ((array[index[r]] > pivot) && (l < r)) {
    // r--;
    // }
    // if (l < r) {
    // help = index[l];
    // index[l] = index[r];
    // index[r] = help;
    // l++;
    // r--;
    // }
    // }
    // if ((l == r) && (array[index[r]] > pivot)) {
    // r--;
    // }
    //
    // return r;
    // }

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

    public static void fillRandom(double[] array, Random rg) {
        for (int i = 0; i < array.length; i++) {
            array[i] = rg.nextDouble();
        }

    }

    /**
     * Computes the sum of the elements of an array of doubles.
     * 
     * @param doubles
     *            the array of double
     * @return the sum of the elements
     */
    public static double sum(double[] doubles) {

        double sum = 0;

        for (int i = 0; i < doubles.length; i++) {
            sum += doubles[i];
        }
        return sum;
    }

    /**
     * Subtract the second array from the first one and returns the result.
     * 
     * @param a
     *            the first array
     * @param b
     *            the second array
     * @return the subtraction of the first minus the second array
     */
    public static double[] substract(double[] a, double[] b) {
        double[] out = new double[a.length];

        for (int i = 0; i < a.length; i++) {
            out[i] = a[i] - b[i];
        }

        return out;
    }

    /**
     * Return the maximum value in this array.
     * 
     * @param array
     *            the array to search the highest value for
     * @return the hightest value in the array
     */
    public static double max(double[] array) {
        double max = array[0];
        for (double d : array)
            if (d > max)
                max = d;
        return max;
    }

    /**
     * Normalizes the doubles in the array by their sum.
     * 
     * @param doubles
     *            the array of double
     * @exception IllegalArgumentException
     *                if sum is Zero or NaN
     */
    public static void normalize(double[] doubles) {
        normalize(doubles, sum(doubles));
    }

    /**
     * Normalizes the doubles in the array using the given value.
     * 
     * @param doubles
     *            the array of double
     * @param sum
     *            the value by which the doubles are to be normalized
     * @exception IllegalArgumentException
     *                if sum is zero or NaN
     */
    public static void normalize(double[] doubles, double sum) {

        if (Double.isNaN(sum)) {
            throw new IllegalArgumentException("Can't normalize array. Sum is NaN.");
        }
        if (sum == 0) {
            return;
        }
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] /= sum;
        }
    }

    public static int maxIndex(double[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++)
            if (array[i] > array[maxIndex])
                maxIndex = i;
        return maxIndex;
    }

    /**
     * Return the minimum value in this array.
     * 
     * @param array
     *            the array to search the lowest value for
     * @return the lowest value in the array
     */
    public static double min(double[] array) {
        double min = array[0];
        for (double d : array)
            if (d < min)
                min = d;
        return min;
    }

    /**
     * Add a value to each value in an array.
     * 
     * @param array
     * @param value
     */
    public static void add(double[] array, double value) {
        for (int i = 0; i < array.length; i++)
            array[i] += value;
    }

    public static void abs(double[] array) {
        for (int i = 0; i < array.length; i++)
            array[i] = Math.abs(array[i]);
    }

    public static double[] multiply(double[] a, double[] b) {
        double[] out = a.clone();
        for (int i = 0; i < out.length; i++)
            out[i] *= b[i];
        return out;
    }

    public static double[] sum(double[] a, double[] b) {
        double[] out = a.clone();
        for (int i = 0; i < out.length; i++)
            out[i] += b[i];
        return out;
    }

    public static double[] divide(double[] a, double[] b) {
        double[] out = a.clone();
        for (int i = 0; i < out.length; i++)
            out[i] /= b[i];
        return out;

    }

    public static void flipSign(double[] values) {
       for(int i=0;i<values.length;i++)
           values[i]=-values[i];
    }

	public static void reverse(int[] ranking) {
		for(int i=0;i<ranking.length/2;i++){
			int tmp=ranking[i];
			ranking[i]=ranking[ranking.length-1-i];
			ranking[ranking.length-1-i]=tmp;
		}
	}
	public static void reverse(double[] ranking) {
		for(int i=0;i<ranking.length/2;i++){
			double tmp=ranking[i];
			ranking[i]=ranking[ranking.length-1-i];
			ranking[ranking.length-1-i]=tmp;
		}
	}
}
