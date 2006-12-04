package net.sf.javaml.distance;

import net.sf.javaml.core.Instance;

/**
 * This class provides a generic way to obtain a normalized version of any
 * distance measure.
 * 
 * It way convert any distance measure to the interval [0,1].
 * 
 * Alfa should be typically in somewhere near the value of the variance for the
 * dataset.
 * 
 * This method is based on work from
 * http://people.revoledu.com/kardi/tutorial/Similarity/Normalization.html.
 * 
 * @author Thomas Abeel
 * 
 */
public class NormalizedDistance implements DistanceMeasure {

	private DistanceMeasure dm;

	private double alfa;

	public NormalizedDistance(DistanceMeasure dm) {
		this(dm, 5);
	}

	public NormalizedDistance(DistanceMeasure dm, double alfa) {
		this.dm = dm;
		this.alfa = alfa;
	}

	public double calculateDistance(Instance i, Instance j) {
		double dist = dm.calculateDistance(i, j);
		return (1 - (dist / Math.sqrt(dist * dist + alfa))) / 2;
	}
}
