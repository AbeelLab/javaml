/**
 * %SVN.HEADER%
 */
package junit.utils;

import java.util.Arrays;

import net.sf.javaml.utils.ArrayUtils;

import org.junit.Test;

public class TestArrayUtils {

    @Test
    public void testArrayUtils(){
        double[]arr={1,5,4,3,8,10,2,6};
        int[]order=ArrayUtils.sort(arr);
        System.out.println(Arrays.toString(order));
    }
}
