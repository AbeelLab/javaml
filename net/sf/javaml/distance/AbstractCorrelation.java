/**
 * AbstractCorrelation.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

/**
 * Abstract super class for all correlation measures. A correlation measure
 * quantifies the correlation between two instances. Values are in the range
 * [-1,1]. One means perfect correlation, zero means no correlation and minus
 * one means perfect inverse correlation.
 * 
 * @see net.sf.javaml.distance.DistanceMeasure
 * @see net.sf.javaml.distance.AbstractSimilarity
 * @see net.sf.javaml.distance.AbstractDistance
 * 
 * {@jmlSource}
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public abstract class AbstractCorrelation implements DistanceMeasure {

    public boolean compare(double x, double y) {
        return Math.abs(x) > Math.abs(y);
    }

}
