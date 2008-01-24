/**
 * TestGainRatio.java
 *
 * %SVN.HEADER%
 */
package junit.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.attribute.eval.RELIEF;
import net.sf.javaml.filter.attribute.eval.SimpleRELIEF;
import net.sf.javaml.filter.normalize.DatasetNormalizeMidrange;

import org.junit.Test;

public class TestRELIEF {
    @Test
    public void testRELIEF() {
        // double[] vals = { 1, 2, 3, 4, 5, 6 };

        Dataset data = new SimpleDataset();
        for (int i = 0; i < 100; i++) {
            double[] vals = { vary(5 * (i / 50), 1), vary(1, 0),Math.random() };
            Instance inst = new SimpleInstance(vals, i /50);

            data.add(inst);

        }
        DatasetNormalizeMidrange dnm=new DatasetNormalizeMidrange(0.5,1);
        dnm.build(data);
        Dataset test=dnm.filterDataset(data);
        
        System.out.println(test);
        RELIEF ga = new RELIEF();
        //ga.setNumNeigbors(4);
        SimpleRELIEF r2=new SimpleRELIEF();
        ga.build(test);
        r2.build(test);
        System.out.println(ga.evaluateAttribute(0));
        System.out.println(ga.evaluateAttribute(1));
        System.out.println(ga.evaluateAttribute(2));
        
        System.out.println(r2.evaluateAttribute(0));
        System.out.println(r2.evaluateAttribute(1));
        System.out.println(r2.evaluateAttribute(2));
       

    }

    private double vary(double i, double j) {
        return i + j * Math.random();
    }
}
