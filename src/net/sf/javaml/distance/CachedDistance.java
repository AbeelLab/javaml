/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import java.util.HashMap;

import net.sf.javaml.core.Instance;
import be.abeel.util.Pair;

/**
 * This class implements a wrapper around other distance measure to cache
 * previously calculated distances.
 * 
 * This should only be used with time consuming distance measures. For Euclidean
 * distance for example it is faster to recalculate it each time.
 * 
 * @author Thomas Abeel
 * 
 */
public class CachedDistance implements DistanceMeasure {

	/**
     * 
     */
	private static final long serialVersionUID = 8794275694780229816L;
	private DistanceMeasure dm = null;

	public CachedDistance(DistanceMeasure dm) {
		this.dm = dm;
	}

	// row map
	HashMap<Pair<Instance, Instance>, Double> cache = new HashMap<Pair<Instance, Instance>, Double>();

	public double measure(Instance i, Instance j) {
		Pair<Instance, Instance> pair = new Pair<Instance, Instance>(i, j);
		if (cache.containsKey(pair)) {
			return cache.get(pair);
		} else {
			double dist = dm.measure(i, j);
			cache.put(pair, dist);
			return dist;
		}
	}

	public boolean compare(double x, double y) {
		return dm.compare(x, y);
	}

	@Override
	public double getMinValue() {
		return dm.getMinValue();
	}

	@Override
	public double getMaxValue() {
		return dm.getMaxValue();
	}

}
