/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Instance;

/**
 * This similarity based distance measure actually measures the angle between
 * two vectors. 
 * 
 * The value returned lies in the interval [0,1].
 * 
 * @author Thomas Abeel
 * 
 */
public class CosineSimilarity extends AbstractSimilarity {

    /**
     * 
     */
    private static final long serialVersionUID = 330926456281777694L;

    @Override
    public double measure(Instance x, Instance y) {
        if (x.noAttributes() != y.noAttributes()) {
            throw new RuntimeException("Both instances should contain the same number of values.");
        }
        double sumTop = 0;
        double sumOne = 0;
        double sumTwo = 0;
        for (int i = 0; i < x.noAttributes(); i++) {
            sumTop += x.value(i) * y.value(i);
            sumOne += x.value(i) * x.value(i);
            sumTwo += y.value(i) * y.value(i);
        }
        double cosSim = sumTop / (Math.sqrt(sumOne) * Math.sqrt(sumTwo));
        if (cosSim < 0)
            cosSim = 0;//This should not happen, but does because of rounding errorsl
        return cosSim;

    }

   
}
