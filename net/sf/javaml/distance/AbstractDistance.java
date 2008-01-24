/**
 * AbstractDistance.java
 *
 * %SVN.HEADER%
 * 
 */
package net.sf.javaml.distance;

/**
 * Abstract super class for all distance measures. A distance measure quantifies
 * the distance between two instances. High values denote more distant
 * instances.
 * 
 * @see net.sf.javaml.distance.DistanceMeasure
 * @see net.sf.javaml.distance.AbstractSimilarity
 * @see net.sf.javaml.distance.AbstractCorrelation
 * 
 * {@jmlSource}
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public abstract class AbstractDistance implements DistanceMeasure {

    public boolean compare(double x, double y) {
        return x < y;
    }

}
