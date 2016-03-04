/**
 * %SVN.HEADER%
 */
package junit.distance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.CosineSimilarity;

import org.junit.Test;

public class CosineSimilarityTest {

    @Test
    public void testCalculateDistance() {
        double[]data={1,1,1};
        double[]data2={0,2,4};
        Instance a=new DenseInstance(data);
        Instance b=new DenseInstance(data2);
        CosineSimilarity cs=new CosineSimilarity();
        assertEquals(cs.measure(a, a),1,0.00001);
        assertTrue(cs.measure(a, b)<1);
    }

}
