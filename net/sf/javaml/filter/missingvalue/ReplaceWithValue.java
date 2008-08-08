/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.missingvalue;

import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.AbstractFilter;

/**
 * Replaces all missing values with a fixed value.
 * 
 * {@jmlSource}
 * 
 * @author Thomas Abeel
 * 
 */
public class ReplaceWithValue extends AbstractFilter {

    private double d;

    public ReplaceWithValue(double d) {
        this.d = d;
    }

    public void filter(Instance inst) {
        for (int i = 0; i < inst.noAttributes(); i++) {
            if (Double.isNaN(inst.value(i)))
                inst.put(i, d);

        }

    }

}
