/**
 * %SVN.HEADER%
 */
package junit.distance;

import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.JaccardIndexDistance;

import org.junit.Test;

public class TestJaccard {
    @Test
    public void testTanimoto() {
        double[] vals = { 1, 2, 3, 4, 5 };
        double[] vals2 = { 1, 2 };
        Instance a = new DenseInstance(vals);
        Instance b = new DenseInstance(vals2);
        JaccardIndexDistance td = new JaccardIndexDistance();
        System.out.println(td.measure(a, b));
    }
}
