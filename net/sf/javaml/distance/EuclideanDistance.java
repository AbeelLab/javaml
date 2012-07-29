/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Instance;

/**
 * This class implements the Euclidean distance.
 * 
 * The Euclidean distance between two points P=(p1,p2,...,pn) and
 * Q=(q1,q2,...,qn) in the Euclidean n-space is defined as: sqrt((p1-q1)^2 +
 * (p2-q2)^2 + ... + (pn-qn)^2)
 * 
 * The Euclidean distance is a special instance of the NormDistance. The
 * Euclidean distance corresponds to the 2-norm distance.
 * 
 * 
 * 
 * @linkplain http://en.wikipedia.org/wiki/Euclidean_distance
 * @linkplain http://en.wikipedia.org/wiki/Euclidean_space
 * @author Thomas Abeel
 * 
 */
public class EuclideanDistance extends NormDistance {

    private static final long serialVersionUID = 6672471509639068507L;

    public double calculateDistance(Instance x, Instance y) {
        if (x.noAttributes() != y.noAttributes()) {
            throw new RuntimeException("Both instances should contain the same number of values.");
        }
        double sum = 0;
        for (int i = 0; i < x.noAttributes(); i++) {
            //ignore missing values
            if (!Double.isNaN(y.value(i)) && !Double.isNaN(x.value(i)))
                sum += (y.value(i) - x.value(i)) * (y.value(i) - x.value(i));
        }
        return Math.sqrt(sum);
    }
}
