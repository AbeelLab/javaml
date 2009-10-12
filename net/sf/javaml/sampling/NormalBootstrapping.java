/**
 * %SVN.HEADER%
 */
package net.sf.javaml.sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implements normal bootstrapping. This amounts to subsampling with
 * replacement.
 * 
 * @author Thomas Abeel
 * 
 */
class NormalBootstrapping extends SamplingMethod {

	@Override
	List<Integer> sample(List<Integer> set, int size, long seed) {
		Random rg = new Random(seed);
		List<Integer> out = new ArrayList<Integer>();
		while (out.size() < size) {
			out.add(set.get(rg.nextInt(set.size())));
		}
		return out;
	}

}
