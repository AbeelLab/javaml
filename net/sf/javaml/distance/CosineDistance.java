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
public class CosineDistance extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = 7818036381569908860L;

    public double measure(Instance x, Instance y) {
        if (x.noAttributes() != y.noAttributes()) {
            throw new RuntimeException("Both instances should contain the same number of values.");
        }
        return 1 - new CosineSimilarity().measure(x, y);

    }

}
