/**
 * RemoveTest.java
 *
 * %SVN.HEADER%
 * 
 */
package junit.filter;

import junit.framework.Assert;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.AbstractFilter;

import org.junit.Test;

public class RemoveTest {
    @Test
    public void testRemove() {
        double[] vals = { 1, 2, 3, 4, 5, 6 };
        Instance inst = new SimpleInstance(vals);
        Dataset data = new SimpleDataset();
        for (int i = 0; i < 10; i++) {
            data.addInstance(inst);

        }
        int[] indices = { 0, 1, 2 };
        AbstractFilter rem = new net.sf.javaml.filter.RemoveAttributes(indices);
        Dataset out = rem.filterDataset(data);
        // System.out.println(out);
        Assert.assertTrue(out.getInstance(0).getValue(0) == 4);
        Assert.assertTrue(out.getInstance(0).getValue(1) == 5);
        Assert.assertTrue(out.getInstance(0).getValue(2) == 6);
    }

}
