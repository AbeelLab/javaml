/**
 * %SVN.HEADER%
 */
package junit.filter;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.InstanceTools;
import net.sf.javaml.filter.RetainAttributes;

import org.junit.Assert;
import org.junit.Test;

public class TestRetainAttributeFilter {

    @Test
    public void testRetain() {

        Instance i = InstanceTools.randomInstance(10);
        Instance j = i.copy();
        RetainAttributes r = new RetainAttributes(new int[] { 1, 2, 3 });
        r.filter(j);
        System.out.println(i);
        System.out.println(j);
        Assert.assertTrue(j.noAttributes()==3);
        Assert.assertTrue(i.value(1)==j.value(0));
        Assert.assertTrue(i.value(2)==j.value(1));
        Assert.assertTrue(i.value(3)==j.value(2));

    }
}
