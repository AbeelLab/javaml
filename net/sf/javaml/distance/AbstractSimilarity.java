/**
 * AbstractSimilarity.java
 * 
 * %SVN.HEADER%
 * 
 */
package net.sf.javaml.distance;

/**
 * Abstract super class for all similarity measures. A similarity measure
 * quantifies the distance between two instances. Low values denote more distant
 * instances.
 * 
 * @see net.sf.javaml.distance.DistanceMeasure
 * @see net.sf.javaml.distance.AbstractDistance
 * @see net.sf.javaml.distance.AbstractCorrelation
 * 
 * @{jmlSource}
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public abstract class AbstractSimilarity implements DistanceMeasure {

    public boolean compare(double x, double y) {
        return x > y;
    }

}
