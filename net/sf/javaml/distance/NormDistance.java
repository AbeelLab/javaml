/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Instance;

/**
 * The norm distance or
 * 
 * 
 * This class implements the Norm distance. This is a generalization of the
 * Euclidean distance, in this respect that the power we use becomes a parameter
 * instead of being fixed to two.
 * 
 * The x-Norm distance between two points P=(p1,p2,...,pn) and Q=(q1,q2,...,qn)
 * in the Euclidean n-space is defined as: ((p1-q1)^x + (p2-q2)^x + ... +
 * (pn-qn)^x)^(1/x)
 * 
 * Special instances are x=1, the Manhattan- or taxicab norm. Or x=infinity
 * gives the Chebychev distance.
 * 
 * The default is the Euclidean distance where x=2.
 * 
 * @linkplain http://en.wikipedia.org/wiki/Norm_%28mathematics%29
 * 
 * @author Thomas Abeel
 * 
 */
public class NormDistance extends AbstractDistance {
    /**
     * 
     */
    private static final long serialVersionUID = 3431231902618783080L;

    /**
     * XXX add doc
     */
    public NormDistance() {
        this(2);
    }

    /**
     * XXX add doc
     */
    private double power;

    /**
     * XXX add doc
     */
    public NormDistance(double power) {
        this.power = power;
    }

    /**
     * XXX add doc
     */
    @Override
    public double measure(Instance x, Instance y) {
        assert (x.noAttributes() == y.noAttributes());
        double sum = 0;
        for (int i = 0; i < x.noAttributes(); i++) {
            sum += Math.pow(Math.abs(y.value(i) - x.value(i)), power);
        }

        return Math.pow(sum, 1 / power);
    }

}
