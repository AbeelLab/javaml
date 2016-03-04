/**
 * %SVN.HEADER%
 */
package junit.utils;

import java.util.Arrays;

import junit.framework.Assert;

import net.sf.javaml.utils.ArrayUtils;

import org.junit.Test;

public class TestArrayUtils {

    @Test
    public void testArrayUtils(){
        double[]arr={1,5,4,3,8,10,2,6};
        int[]order=ArrayUtils.sort(arr);
        System.out.println(Arrays.toString(order));
    }
    
    @Test
    public void testReverse(){
        double[]arr={1,2,3,4,5};
       ArrayUtils.reverse(arr);
        System.out.println(Arrays.toString(arr));
        Assert.assertEquals(5.0, arr[0]);
        Assert.assertEquals(4.0, arr[1]);
        Assert.assertEquals(3.0, arr[2]);
        Assert.assertEquals(2.0, arr[3]);
        Assert.assertEquals(1.0, arr[4]);
        
        int[]arr2=new int[]{1,2,3,4};
        ArrayUtils.reverse(arr2);
         System.out.println(Arrays.toString(arr2));
         Assert.assertEquals(4, arr2[0]);
         Assert.assertEquals(3, arr2[1]);
         Assert.assertEquals(2, arr2[2]);
         Assert.assertEquals(1, arr2[3]);
    }
}
