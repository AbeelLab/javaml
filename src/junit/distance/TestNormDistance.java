/**
 * %SVN.HEADER%
 */
package junit.distance;

import junit.framework.Assert;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import net.sf.javaml.distance.NormDistance;
import net.sf.javaml.tools.InstanceTools;

import org.junit.Test;

public class TestNormDistance {

	
	 @Test
	    public void testNorm() {
	        Instance x = InstanceTools.randomInstance(10);
	        Instance y = InstanceTools.randomInstance(10);
	        
	        NormDistance nd=new NormDistance(1.0/2);
	        double d=nd.measure(x, y);
	        Assert.assertFalse(Double.isInfinite(d));

	    }
	
    @Test
    public void testCD() {
        Instance x = new SparseInstance(10);
        Instance y = new SparseInstance(10);
        
        NormDistance nd=new NormDistance();
        nd.measure(x, y);

    }
}
