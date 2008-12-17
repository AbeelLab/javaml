/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import java.io.Serializable;

import net.sf.javaml.core.Instance;

/**
 * A distance measure is an algorithm to calculate the distance, similarity or
 * correlation between two instances.
 * 
 * There are three types of distance measures: distance, similarity and
 * correlation measures.
 * 
 * Some distance measures are normalized, i.e. in the interval [0,1], but this
 * is not required by the interface.
 * 
 * {@jmlSource}
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
    public double measure(Instance x, Instance y);


	/**
	 * Returns whether the first distance, similarity or correlation is better
	 * than the second distance, similarity or correlation. 
	 * 
	 * Both values should be calculated using the same measure.
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
