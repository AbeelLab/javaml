/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Instance;

public class ChebychevDistance extends AbstractDistance {

    private static final long serialVersionUID = -5886297070247181544L;

    @Override
    public double measure(Instance x, Instance y) {
        if (x.noAttributes() != y.noAttributes())
            throw new RuntimeException("Both instances should contain the same number of values.");
        double totalMax = 0.0;
        for (int i = 0; i < x.noAttributes(); i++) {
            totalMax = Math.max(totalMax, Math.abs(y.value(i) - x.value(i)));
        }
        return totalMax;
    }

}
