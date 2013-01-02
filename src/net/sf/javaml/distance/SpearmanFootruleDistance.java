/**
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
public class SpearmanFootruleDistance extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = -6347213714272482397L;

    public double measure(Instance a, Instance b) {
        if (a.noAttributes() != b.noAttributes())
            throw new IllegalArgumentException("Instances should be compatible.");
        long k = a.noAttributes();
        long denom;
        if(k%2==0)
            denom=(k*k)/2;
        else
            denom=((k+1)*(k-1))/2;
        double sum = 0.0;
        for (int i = 0; i < a.noAttributes(); i++) {
            double diff = Math.abs(a.value(i) - b.value(i));
            sum += diff;
        }
        return 1.0 - (sum / ((double) denom));
    }

    public double getMaximumDistance(Dataset data) {
        return 1;
    }

    public double getMinimumDistance(Dataset data) {
        return 0;
    }

}
