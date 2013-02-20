/**
 * %SVN.HEADER%
 */
package net.sf.javaml.sampling;

import java.util.ArrayList;
import java.util.List;

import be.abeel.util.MTRandom;

/**
 * Implements regular subsampling without replacement.
 * 
 * This method cannot return samples that are larger than the original data set.
 * 
 * @author Thomas
 * 
 */
class SubSampling extends SamplingMethod {

	@Override
	List<Integer> sample(List<Integer> set, int size, long seed) {
		/* Regular Java Random is not Random enough */
		MTRandom rg = new MTRandom(seed);
		List<Integer> out = new ArrayList<Integer>();
		out.addAll(set);
		while (out.size() > size) {
			int ri=rg.nextInt(out.size());
			out.remove(ri);
		}
		return out;
	}

}
