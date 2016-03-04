/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Instance;

/**
 * Specialized similarity that takes the maximum product of two feature values.
 * If this value is zero, the similarity is undefined. This similarity measure
 * is used mainly with features extracted from cluster models.
 * 
 * @author Michael Wurst
 * @author Thomas Abeel
 */
public class MaxProductSimilarity extends AbstractSimilarity {
    /**
     * 
     */
    private static final long serialVersionUID = 3737968543405527283L;

    /**
     * XXX doc
     */
    public double measure(Instance x, Instance y) {

        double max = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < x.noAttributes(); i++) {

            double v = x.value(i) * y.value(i);

            if (v > max)
                max = v;

        }

        if (max > 0.0)
            return max;
        else
            return Double.NaN;
    }

}
