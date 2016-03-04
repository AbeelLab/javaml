/**
 * %SVN.HEADER%
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
 * @author Thomas Abeel
 * 
 */
public abstract class AbstractDistance implements DistanceMeasure {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5437432787091767310L;

	public boolean compare(double x, double y) {
		return x < y;
	}

	@Override
	public double getMinValue() {
		return 0;
	}

	@Override
	public double getMaxValue() {
		return Double.POSITIVE_INFINITY;
	}
}
