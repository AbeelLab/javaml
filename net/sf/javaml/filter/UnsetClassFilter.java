/**
 * UnsetClassFilter.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleInstance;

/**
 * Filter to remove class information from a dataset or instance.
 * 
 * 
 * {@jmlSource}
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class UnsetClassFilter extends AbstractFilter {

	public Instance filterInstance(Instance inst) {
		// FIXME it is impossible at the moment to give a weight, but to leave
		// out the class information.
		return new SimpleInstance(inst.toArray());
	}

}
