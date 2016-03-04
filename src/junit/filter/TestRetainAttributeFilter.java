/**
 * %SVN.HEADER%
 */
package junit.filter;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.RetainAttributes;
import net.sf.javaml.tools.InstanceTools;
import net.sf.javaml.tools.data.FileHandler;

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
        Assert.assertTrue(j.noAttributes() == 3);
        Assert.assertTrue(i.value(1) == j.value(0));
        Assert.assertTrue(i.value(2) == j.value(1));
        Assert.assertTrue(i.value(3) == j.value(2));

    }

    @Test
    public void testRetainSparse() {

        try {
            Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/sparse.tsv"), 0, " ", ":");
            int[] ret = new int[1000];
            for (int i = 0; i < 1000; i++)
                ret[i] = i;
            RetainAttributes r = new RetainAttributes(ret);
            r.filter(data);
            System.out.println(data.instance(0));
            System.out.println(data.instance(1));
            System.out.println(data.instance(2));
            System.out.println(data.instance(3));
            System.out.println(data.instance(4));
            
            
            File io = new File("smallsparse.tsv");
            FileHandler.exportDataset(data, io);
            Dataset data2 = FileHandler.loadSparseDataset(io, 0, "\t", ":");
            Assert.assertEquals(data.size(), data2.size());
            Assert.assertTrue(data.noAttributes()>=data2.noAttributes());
            io.delete();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
