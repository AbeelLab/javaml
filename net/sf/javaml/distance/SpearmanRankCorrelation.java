/**
 * SpearmanRankCorrelation.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * TODO WRITE DOC
 * @author Thomas Abeel
 *
 */
public class SpearmanRankCorrelation extends AbstractCorrelation {

    /**
     * 
     */
    private static final long serialVersionUID = -6347213714272482397L;

    public double calculateDistance(Instance a, Instance b) {
        if (a.size() != b.size())
            throw new IllegalArgumentException("Instances should be compatible.");
        int k = a.size();
        int denom = k * (k * k - 1);
        double sum = 0.0;
        for (int i = 0; i < a.size(); i++) {
            double diff = (a.value(i) - b.value(i));
            sum += (diff * diff);
        }
        return 1.0 - (6.0 * (sum / ((double) denom)));
    }

    public double getMaximumDistance(Dataset data) {
        return 1;
    }

    public double getMinimumDistance(Dataset data) {
        return 0;
    }

}
