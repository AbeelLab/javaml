/**
 * %SVN.HEADER%
 */
package junit.distance;

import static org.junit.Assert.assertEquals;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.EuclideanDistance;

import org.junit.Test;

public class EuclideanTest {

    @Test
    public void testCalculateDistance() {
        double[]data={1,Double.NaN,1};
        double[]data2={0,2,4};
        Instance a=new DenseInstance(data);
        Instance b=new DenseInstance(data2);
        EuclideanDistance d=new EuclideanDistance();
        assertEquals(d.measure(a, a),0,0.00001);
        assertEquals(d.measure(b, b),0,0.00001);
        
        
    }

}
