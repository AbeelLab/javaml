package net.sf.javaml.tools.sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implements regular subsampling without replacement.
 * 
 * This method cannot return samples that are larger than the original data set.
 * 
 * @author Thomas
 * 
 */
public class SubSampling extends SamplingMethod {

	@Override
	public List<Integer> sample(List<Integer> set, int size, long seed) {
		Random rg = new Random(seed);
		List<Integer> out = new ArrayList<Integer>();
		out.addAll(set);
		while (out.size() > size) {
			out.remove(rg.nextInt(out.size()));
		}
		return out;
	}

}
