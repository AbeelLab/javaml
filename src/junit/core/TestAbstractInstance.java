/**
 * %SVN.HEADER%
 */
package junit.core;

import org.junit.Assert;
import org.junit.Test;

import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.InstanceTools;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class TestAbstractInstance {

	@Test
	public void testInstanceValueIterator() {
		Instance t=InstanceTools.randomInstance(5);
		int count=0;
		for(double d:t){
			count++;
		}
		Assert.assertEquals(5, count);
	}
}
