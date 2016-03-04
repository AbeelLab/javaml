/**
 * %SVN.HEADER%
 */
package junit.filter;

import java.util.HashSet;
import java.util.Set;

import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.RemoveAttributes;

import org.junit.Assert;
import org.junit.Test;

public class TestRemoveAttributes {

    @Test
    public void testRemoveAttributes() {
        Instance i = new DenseInstance(new double[] { 1, 2, 3, 4, 5});
        Set<Integer>toRemove=new HashSet<Integer>();
        toRemove.add(1);
        toRemove.add(4);
        RemoveAttributes ra=new RemoveAttributes(toRemove);
        ra.filter(i);
        Assert.assertTrue(i.noAttributes()==3);
        Assert.assertTrue(i.value(0)==1);
        Assert.assertTrue(i.value(1)==3);
        Assert.assertTrue(i.value(2)==4);
        
        

    }
    
    @Test
    public void testRemoveAttributes2() {
        Set<Integer>toRemove=new HashSet<Integer>();
        toRemove.add(1);
        toRemove.add(3);
        toRemove.add(0);
        Instance i = new DenseInstance(new double[] { 1, 2, 3, 4, 5});
        RemoveAttributes ra=new RemoveAttributes(toRemove);
        ra.filter(i);
        Assert.assertTrue(i.noAttributes()==2);
        Assert.assertTrue(i.value(0)==3);
        Assert.assertTrue(i.value(1)==5);
        
        
        

    }
}
