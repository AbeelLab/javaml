/**
 * %SVN.HEADER%
 */
package junit.core;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import org.junit.Assert;
import org.junit.Test;

public class TestDefaultDataset {
    static final double delta = 0.000001;

    @Test
    public void testDatasetRemove() {
        Dataset data = new DefaultDataset();
        double[] val = new double[] { 0.0 };
        data.add(new DenseInstance(val));
        val[0]++;
        data.add(new DenseInstance(val));
        val[0]++;
        data.add(new DenseInstance(val));
        val[0]++;
        data.add(new DenseInstance(val));
        val[0]++;
        data.add(new DenseInstance(val));
        val[0]++;
        data.add(new DenseInstance(val));
        val[0]++;
        Assert.assertEquals(data.size(),6);
        Assert.assertEquals(data.get(0).value(0), 0.0, delta);
        Assert.assertEquals(data.get(1).value(0), 1.0, delta);
        Assert.assertEquals(data.get(2).value(0), 2.0, delta);
        Assert.assertEquals(data.get(3).value(0), 3.0, delta);
        Assert.assertEquals(data.get(4).value(0), 4.0, delta);
        Assert.assertEquals(data.get(5).value(0), 5.0, delta);
        
        Instance i=data.remove(4);
        Assert.assertEquals(i.value(0), 4.0, delta);
        Assert.assertEquals(data.size(),5);
        
        Assert.assertEquals(data.get(0).value(0), 0.0, delta);
        Assert.assertEquals(data.get(1).value(0), 1.0, delta);
        Assert.assertEquals(data.get(2).value(0), 2.0, delta);
        Assert.assertEquals(data.get(3).value(0), 3.0, delta);
        Assert.assertEquals(data.get(4).value(0), 5.0, delta);
        
    }
}
