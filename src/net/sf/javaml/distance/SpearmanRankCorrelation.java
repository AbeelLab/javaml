/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Instance;

/**
 * Calculates the Spearman rank correlation of two instances. The value on
 * position 0 of the instance should be the rank of attribute 0. And so on and so forth.
 * 
 * 
 * 
 * 
 * 
 * @version %SVN.REVISION%
 * 
 * @linkplain http://en.wikipedia.org/wiki/Spearman's_rank_correlation_coefficient
 * 
 * @author Thomas Abeel
 * 
 */
public class SpearmanRankCorrelation extends AbstractCorrelation {

    private static final long serialVersionUID = -6347213714272482397L;

    @Override
    public double measure(Instance a, Instance b) {
        if (a.noAttributes() != b.noAttributes())
            throw new IllegalArgumentException("Instances should be compatible.");
        long k = a.noAttributes();
        long denom = k * (k * k - 1);
        double sum = 0.0;
        for (int i = 0; i < a.noAttributes(); i++) {
            double diff = (a.value(i) - b.value(i));
            sum += (diff * diff);
        }
        return 1.0 - (6.0 * (sum / ((double) denom)));
    }

}
