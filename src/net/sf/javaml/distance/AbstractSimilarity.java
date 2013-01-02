/**
 * %SVN.HEADER%
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
 * 
 * @author Thomas Abeel
 * 
 */
public abstract class AbstractSimilarity implements DistanceMeasure {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8279234668623952242L;

	public boolean compare(double x, double y) {
        return x > y;
    }

	@Override
	public double getMinValue()	{
	  return 1;
	}
	
	@Override
	public double getMaxValue()	{
	  return 0;
	}
}
