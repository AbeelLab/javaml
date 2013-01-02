/**
 * %SVN.HEADER%
 */
package net.sf.javaml.sampling;

import java.util.ArrayList;
import java.util.List;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.ListTools;
import be.abeel.util.Pair;

/**
 * @author Thomas Abeel
 * 
 */
public enum Sampling {
	/**
	 * Implements regular sub sampling without replacement.
	 * 
	 * This method cannot return samples that are larger than the original data
	 * set.
	 */
	SubSampling,

	/**
	 * Implements normal bootstrapping. This amounts to sub sampling with
	 * replacement.
	 */
	NormalBootstrapping,

	/**
	 * Stratified sub sampling
	 */
	StratifiedSubsampling,

	/**
	 * Stratified bootstrapping
	 */
	StratifiedNormalBootstrapping;

	/**
	 * Convenience method to subsample a data set.
	 * 
	 * @param inputData
	 * @param s
	 * @param size
	 * @return
	 */
	public Pair<Dataset, Dataset> sample(Dataset data, int size, long seed) {
		SamplingMethod s = null;
		switch (this) {
		case NormalBootstrapping:
		case StratifiedNormalBootstrapping:
			s = new NormalBootstrapping();
			break;
		case SubSampling:
		case StratifiedSubsampling:
			s = new SubSampling();
			break;

		}
		assert s != null;
		List<Integer> sampledIxs = null;
		switch (this) {
		case StratifiedNormalBootstrapping:
		case StratifiedSubsampling:
			sampledIxs = stratified(s, data, size, seed);
		case SubSampling:
		case NormalBootstrapping:
			sampledIxs = regular(s, data, size, seed);
		}

		/* Make ixs contain the out of sample indices */
		List<Integer> ixs = ListTools.incfill(data.size());
		ixs.removeAll(sampledIxs);
		Dataset in = new DefaultDataset();
		Dataset out = new DefaultDataset();
		for (int i : sampledIxs)
			in.add(data.get(i).copy());
		for (int i : ixs)
			out.add(data.get(i).copy());
		return new Pair<Dataset, Dataset>(in, out);

	}

	/**
	 * @param s
	 * @param data
	 * @param size
	 * @param seed
	 * @return
	 */
	private List<Integer> regular(SamplingMethod s, Dataset data, int size, long seed) {
		List<Integer> ixs = ListTools.incfill(data.size());
		return s.sample(ixs, size, seed);
	}

	/**
	 * @param s
	 * @param data
	 * @param size
	 * @param seed
	 * @return
	 */
	private List<Integer> stratified(SamplingMethod s, Dataset data, int size, long seed) {
		List<Integer> sampled = new ArrayList<Integer>();
		for (Object o : data.classes()) {
			List<Integer> ixs = new ArrayList<Integer>();
			int index = 0;
			for (Instance i : data) {
				if (i.classValue().equals(o))
					ixs.add(index);
				index++;
			}
			double fraction = ixs.size() / (double) data.size();
			sampled.addAll(s.sample(ixs, (int) Math.ceil(fraction * size), seed));

		}
		return sampled;
	}

	public Pair<Dataset, Dataset> sample(Dataset inputData) {
		return sample(inputData, inputData.size());
	}

	public Pair<Dataset, Dataset> sample(Dataset inputData, int size) {
		return sample(inputData, size, System.currentTimeMillis());

	}
}
