/**
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
 * 
 * @author Thomas Abeel
 * 
 */
public abstract class AbstractCorrelation implements DistanceMeasure {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2504319617417736626L;

	/**
	 * Comparisons for correlation measures are done with the absolute value of
	 * the real values
	 */
	public boolean compare(double x, double y) {
		return Math.abs(x) > Math.abs(y);
	}

	@Override
	public double getMinValue() {
		return 1;
	}

	@Override
	public double getMaxValue() {
		return 0;
	}

}
