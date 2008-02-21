/**
 * TestTanimoto.java
 *
 * %SVN.HEADER%
 */
package junit.distance;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.JaccardIndexDistance;

import org.junit.Test;

public class TestJaccard {
    @Test
    public void testTanimoto() {
        double[]vals={1,2,3,4,5};
        double[]vals2={1,2};
        Instance a=new SimpleInstance(vals);
        Instance b=new SimpleInstance(vals2);
        JaccardIndexDistance td=new JaccardIndexDistance();
        System.out.println(td.calculateDistance(a, b));
    }
}
