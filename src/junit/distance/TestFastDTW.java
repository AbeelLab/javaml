/**
 * %SVN.HEADER%
 */
package junit.distance;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.fastdtw.Abstraction;
import net.sf.javaml.distance.fastdtw.Band;
import net.sf.javaml.distance.fastdtw.FastDTW;
import net.sf.javaml.tools.InstanceTools;

import org.junit.Test;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class TestFastDTW {

    @Test
    public void testfdtw(){
        Abstraction ac=new Abstraction(2);
        Instance rg1=InstanceTools.randomInstance(25);
        Instance rg2=InstanceTools.randomInstance(25);
        ac.measure(rg1, rg2);
        
        Band band=new Band(2);
        band.measure(rg1, rg2);
        
        
        FastDTW fdtw=new FastDTW(2);
        fdtw.measure(rg1, rg2);
    }
}
