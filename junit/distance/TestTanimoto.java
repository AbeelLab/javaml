/**
 * TestTanimoto.java
 *
 * %SVN.HEADER%
 */
package junit.distance;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.TanimotoDistance;

import org.junit.Test;

public class TestTanimoto {
    @Test
    public void testTanimoto() {
        double[]vals={1,2,3,4,5};
        double[]vals2={1,2};
        Instance a=new SimpleInstance(vals);
        Instance b=new SimpleInstance(vals2);
        TanimotoDistance td=new TanimotoDistance();
        System.out.println(td.calculateDistance(a, b));
    }
}
