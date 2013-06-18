/**
 * %SVN.HEADER%
 */
package junit.utils;

import net.sf.javaml.utils.GammaFunction;

import org.junit.Test;

public class TestGamma {

	@Test
	public void testGamma(){
		for(int i=1;i<1000;i++){
			
			double d=GammaFunction.gamma(i);
			System.out.println(i+"\t"+d);
			if(Double.isNaN(d)){
				break;
			}
			
		}
	}
}
