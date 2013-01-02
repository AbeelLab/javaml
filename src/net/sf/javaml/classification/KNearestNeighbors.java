/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.exception.TrainingRequiredException;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * Implementation of the K nearest neighbor (KNN) classification algorithm.
 * 
 * @author Thomas Abeel
 * 
 */
public class KNearestNeighbors extends AbstractClassifier {

	private static final long serialVersionUID = 1560149339188819924L;

	private Dataset training;

	private int k;

	private DistanceMeasure dm;

	/**
	 * Instantiate the k-nearest neighbors algorithm with a specified number of
	 * neighbors.
	 * 
	 * @param k
	 *            the number of neighbors to use
	 */
	public KNearestNeighbors(int k) {
		this(k, new EuclideanDistance());
	}

	/**
	 * Instantiate the k-nearest neighbors algorithm with a specified number of
	 * neighbors.
	 * 
	 * @param k
	 *            the number of neighbors to use
	 */
	public KNearestNeighbors(int k, DistanceMeasure dm) {
		this.k = k;
		this.dm = dm;
	}

	@Override
	public void buildClassifier(Dataset data) {
		this.training = data;
	}

	@Override
	public Map<Object, Double> classDistribution(Instance instance) {
		if (training == null)
			throw new TrainingRequiredException();
		/* Get nearest neighbors */
		Set<Instance> neighbors = training.kNearest(k, instance, dm);
		/* Build distribution map */
		HashMap<Object, Double> out = new HashMap<Object, Double>();
		for (Object o : training.classes())
			out.put(o, 0.0);
		for (Instance i : neighbors) {
			out.put(i.classValue(), out.get(i.classValue()) + 1);
		}

		double min = k;
		double max = 0;
		for (Object key : out.keySet()) {
			double val = out.get(key);
			if (val > max)
				max = val;
			if (val < min)
				min = val;
		}
		/* Normalize distribution map */
		if (max != min) {
			for (Object key : out.keySet()) {
				out.put(key, (out.get(key) - min) / (max - min));
			}
		}

		return out;
	}
}
