/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Instance;

/**
 * Calculates the Pearson Correlation Coeffient between two vectors.
 * 
 * The returned value lies in the interval [-1,1]. A value of 1 shows that a
 * linear equation describes the relationship perfectly and positively, with all
 * data points lying on the same line and with Y increasing with X. A score of
 * ?1 shows that all data points lie on a single line but that Y increases as X
 * decreases. A value of 0 shows that a linear model is inappropriate / that
 * there is no linear relationship between the variables.
 * 
 * http://davidmlane.com/hyperstat/A56626.html
 * http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class PearsonCorrelationCoefficient extends AbstractCorrelation {

    private static final long serialVersionUID = -5805322724874919246L;

    /**
     * Measures the Pearson Correlation Coefficient between the two supplied
     * instances.
     * 
     * @param a
     *            the first instance
     * @param b
     *            the second instance
     */
    public double measure(Instance a, Instance b) {
        if (a.noAttributes() != b.noAttributes())
            throw new RuntimeException("Both instances should have the same length");
        double xy = 0, x = 0, x2 = 0, y = 0, y2 = 0;
        for (int i = 0; i < a.noAttributes(); i++) {
            xy += a.value(i) * b.value(i);
            x += a.value(i);
            y += b.value(i);
            x2 += a.value(i) * a.value(i);
            y2 += b.value(i) * b.value(i);
        }
        int n = a.noAttributes();
        return (xy - (x * y) / n) / Math.sqrt((x2 - (x * x) / n) * (y2 - (y * y) / n));
    }

}
