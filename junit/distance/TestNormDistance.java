/**
 * %SVN.HEADER%
 */
package junit.distance;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import net.sf.javaml.distance.NormDistance;

import org.junit.Test;

public class TestNormDistance {

    @Test
    public void testCD() {
        Instance x = new SparseInstance(10);
        Instance y = new SparseInstance(10);
        
        NormDistance nd=new NormDistance();
        nd.measure(x, y);

    }
}
