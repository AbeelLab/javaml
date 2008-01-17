/**
 * RemoveAttributes.java
 *
 * %SVN.HEADER%
 */
package junit.filter;

import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.AbstractFilter;
import net.sf.javaml.filter.DatasetFilter;
import net.sf.javaml.filter.DatasetNormalizeMidrange;
import net.sf.javaml.filter.PrincipalComponentsAnalysis;

import org.junit.Test;

public class PCATest {

    Random rg = new Random(4);

    @Test
    public void testRemove() throws Exception {

        double[] vals1 = { 1, 2, 3.5f, 4, 1 };
        double[] vals2 = { 1, 2, 4, 4, 2 };
        double[] vals3 = { 1, 2, 4.5f, 4, 3 };
        double[] vals4 = { 1, 2, 3.7f, 4, 4 };
        double[] vals5 = { 1, 2, 4.1f, 4, 5 };
        double[] vals6 = { 1, 2, 3.9f, 4, 6 };
       
      

        

        Dataset data = new SimpleDataset();
        Instance inst = new SimpleInstance(vals1);
        data.addInstance(inst);
        inst = new SimpleInstance(vals2);
        data.addInstance(inst);
        inst = new SimpleInstance(vals3);
        data.addInstance(inst);
        inst = new SimpleInstance(vals4);
        data.addInstance(inst);
        inst = new SimpleInstance(vals5);
        data.addInstance(inst);
        inst = new SimpleInstance(vals6);
        data.addInstance(inst);

        DatasetFilter norm = new DatasetNormalizeMidrange(0.5, 1);
        Dataset tmp = norm.filterDataset(data);

        AbstractFilter rem = new PrincipalComponentsAnalysis();
        Dataset out = rem.filterDataset(tmp);
        // System.out.println("JML "+out);
        for (int i = 0; i < out.size(); i++) {
            System.out.println("PCATest-JML: " + out.getInstance(i));
            System.out.println("PCATest-JML back: " + rem.unfilterInstance(out.getInstance(i)));
        }

        

    }

}
