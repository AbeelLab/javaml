/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.util;

import java.util.ArrayList;
import java.util.Collection;

public class Arrays
{

    public Arrays()
    {
    }

    public static int[] toPrimitiveArray(Integer objArr[])
    {
        int primArr[] = new int[objArr.length];
        for(int x = 0; x < objArr.length; x++)
            primArr[x] = objArr[x].intValue();

        return primArr;
    }

    public static int[] toIntArray(Collection c)
    {
        return toPrimitiveArray((Integer[])c.toArray(new Integer[0]));
    }

    public static Collection toCollection(boolean arr[])
    {
        ArrayList collection = new ArrayList(arr.length);
        for(int x = 0; x < arr.length; x++)
            collection.add(new Boolean(arr[x]));

        return collection;
    }

    public static Collection toCollection(byte arr[])
    {
        ArrayList collection = new ArrayList(arr.length);
        for(int x = 0; x < arr.length; x++)
            collection.add(new Byte(arr[x]));

        return collection;
    }

    public static Collection toCollection(char arr[])
    {
        ArrayList collection = new ArrayList(arr.length);
        for(int x = 0; x < arr.length; x++)
            collection.add(new Character(arr[x]));

        return collection;
    }

    public static Collection toCollection(double arr[])
    {
        ArrayList collection = new ArrayList(arr.length);
        for(int x = 0; x < arr.length; x++)
            collection.add(new Double(arr[x]));

        return collection;
    }

    public static Collection toCollection(float arr[])
    {
        ArrayList collection = new ArrayList(arr.length);
        for(int x = 0; x < arr.length; x++)
            collection.add(new Float(arr[x]));

        return collection;
    }

    public static Collection toCollection(int arr[])
    {
        ArrayList collection = new ArrayList(arr.length);
        for(int x = 0; x < arr.length; x++)
            collection.add(new Integer(arr[x]));

        return collection;
    }

    public static Collection toCollection(long arr[])
    {
        ArrayList collection = new ArrayList(arr.length);
        for(int x = 0; x < arr.length; x++)
            collection.add(new Long(arr[x]));

        return collection;
    }

    public static Collection toCollection(short arr[])
    {
        ArrayList collection = new ArrayList(arr.length);
        for(int x = 0; x < arr.length; x++)
            collection.add(new Short(arr[x]));

        return collection;
    }

    public static Collection toCollection(String arr[])
    {
        ArrayList collection = new ArrayList(arr.length);
        for(int x = 0; x < arr.length; x++)
            collection.add(new String(arr[x]));

        return collection;
    }

    public static boolean contains(boolean arr[], boolean val)
    {
        for(int x = 0; x < arr.length; x++)
            if(arr[x] == val)
                return true;

        return false;
    }

    public static boolean contains(byte arr[], byte val)
    {
        for(int x = 0; x < arr.length; x++)
            if(arr[x] == val)
                return true;

        return false;
    }

    public static boolean contains(char arr[], char val)
    {
        for(int x = 0; x < arr.length; x++)
            if(arr[x] == val)
                return true;

        return false;
    }

    public static boolean contains(double arr[], double val)
    {
        for(int x = 0; x < arr.length; x++)
            if(arr[x] == val)
                return true;

        return false;
    }

    public static boolean contains(float arr[], float val)
    {
        for(int x = 0; x < arr.length; x++)
            if(arr[x] == val)
                return true;

        return false;
    }

    public static boolean contains(int arr[], int val)
    {
        for(int x = 0; x < arr.length; x++)
            if(arr[x] == val)
                return true;

        return false;
    }

    public static boolean contains(long arr[], long val)
    {
        for(int x = 0; x < arr.length; x++)
            if(arr[x] == val)
                return true;

        return false;
    }

    public static boolean contains(short arr[], short val)
    {
        for(int x = 0; x < arr.length; x++)
            if(arr[x] == val)
                return true;

        return false;
    }

    public static boolean contains(String arr[], String val)
    {
        for(int x = 0; x < arr.length; x++)
            if(arr[x] == val)
                return true;

        return false;
    }
}