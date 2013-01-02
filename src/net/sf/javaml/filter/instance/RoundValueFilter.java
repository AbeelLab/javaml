/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.instance;

import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.InstanceFilter;

/**
 * Filter to replace all values with their rounded equivalent
 * 
 * @author Thomas Abeel
 * 
 */
public class RoundValueFilter implements InstanceFilter {

    @Override
    public void filter(Instance inst) {
        for (Integer i : inst.keySet()) {
            inst.put(i, (double) (int) (inst.get(i) + 0.5));
        }

    }

}
