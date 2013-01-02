/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Filter to remove class information from a data set or instance.
 * 
 * 
 * 
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class UnsetClassFilter extends AbstractFilter {

    @Override
    public void filter(Instance inst) {
        inst.setClassValue(null);
    }

    @Override
    public void filter(Dataset d) {
        super.filter(d);
        d.classes().clear();
    }

}
