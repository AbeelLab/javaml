/**
 * UnsetClassFilter.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Instance;

/**
 * Filter to remove class information from a data set or instance.
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

    public void filterInstance(Instance inst) {
        inst.setClassValue(null);
    }

}
