/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Instance;

/**
 * The kernel method for measuring similarities between instances. The values
 * for this measure lie in the interval [0,1] with 0 for low similarity (high
 * distance) and 1 for high similarity (low distance).
 * 
 * @author Thomas Abeel
 * 
 */
public class RBFKernel extends AbstractSimilarity {

    /**
     * 
     */
    private static final long serialVersionUID = -4365058705250608462L;
    private double gamma = 0.01;

    public RBFKernel() {
        this(0.01);
    }

    /**
     * Create a new RBF kernel with gamma as a parameter
     * 
     * @param gamma
     */
    public RBFKernel(double gamma) {
        this.gamma = gamma;
    }

    /**
     * Calculates a dot product between two instances
     * 
     * @param x
     *            the first instance
     * @param y
     *            the second instance
     * @return the dot product of the two instances.
     */
    private final double dotProduct(Instance x, Instance y) {
        double result = 0;
        for (int i = 0; i < x.noAttributes(); i++) {
            result += x.value(i) * y.value(i);
        }
        return result;
    }

    /**
     * XXX DOC
     */
    public double measure(Instance x, Instance y) {
        if (x.equals(y))
            return 1.0;
        double result = Math.exp(gamma * (2.0 * dotProduct(x, y) - dotProduct(x, x) - dotProduct(y, y)));
        return result;

    }


}
