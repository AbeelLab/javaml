/**
 * DistanceMeasure.java
 *
 *
 * %SVN.HEADER%
 *  
 */
package net.sf.javaml.distance;

import java.io.Serializable;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * A distance measure is an algorithm to calculate the distance between to
 * instances. Objects that are close together or are very similar should have
 * low distance values, while object that are far apart or are not similar
 * should have high distance values.
 * 
 * There are three types of distance measures: distance, similarity and
 * correlation measures.
 * 
 * Some distance measures are normalized, i.e. in the interval [0,1], but this
 * is not required by the interface.
 * 
 * @{jmlSource}
 * 
 * @see net.sf.javaml.distance.AbstractDistance
 * @see net.sf.javaml.distance.AbstractSimilarity
 * @see net.sf.javaml.distance.AbstractCorrelation
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public interface DistanceMeasure extends Serializable {
    /**
     * Calculates the distance between two instances.
     * 
     * @param i
     *            the first instance
     * @param j
     *            the second instance
     * @return the distance between the two instances
     */
    public double calculateDistance(Instance i, Instance j);

    /**
     * Maximal distance, minimal similarity for distance measures this value
     * should be high, for similarity measures this value should be low.
     * 
     * For some distance measures this will be a very expensive operation as all
     * possible distances have to be calculated.
     * 
     * It is allowed to return an upper bound of the distance, although it may
     * not actually be reached within the data set.
     * 
     * 
     * @param data
     *            the data set to calculate the maximum distance for.
     * @return the maximum distance within this data set
     */
    public double getMaximumDistance(Dataset data);

    /**
     * Minimal distance, maximal similarity for distance measures this value
     * should be low, for similarity measures this value should be high.
     * 
     * For some distance measures this will be a very expensive operation as all
     * possible distances have to be calculated.
     * 
     * It is allowed to return a lower bound of the distance, although it may
     * not actually be reached within the data set.
     * 
     * 
     * @param data
     *            the data set to calculate the minimum distance for.
     * @return the minimum distance within this data set
     */
    public double getMinimumDistance(Dataset data);

    /**
     * Returns whether the first distance, similarity or correlation is better
     * than the second distance, similarity or correlation.
     * 
     * Both values should bee calculated using the same measure.
     * 
     * For similarity measures the higher the similarity the better the measure,
     * for distance measures it is the lower the better and for correlation
     * measure the absolute value must be higher.
     * 
     * @param x
     *            the first distance, similarity or correlation
     * @param y
     *            the second distance, similarity or correlation
     * @return true if the first distance is better than the second, false in
     *         other cases.
     */
    public boolean compare(double x, double y);
}
