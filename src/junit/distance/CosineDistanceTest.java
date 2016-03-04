/**
 * %SVN.HEADER%
 */
package junit.distance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.CosineDistance;

import org.junit.Test;


public class CosineDistanceTest {
    @Test
    public void testCalculateDistance() {
        double[]data={1,1,1};
        double[]data2={0,2,4};
        Instance a=new DenseInstance(data);
        Instance b=new DenseInstance(data2);
        CosineDistance cs=new CosineDistance();
        assertEquals(cs.measure(a, a),0,0.00001);
        assertTrue(cs.measure(a, b)>0);
    }
}
