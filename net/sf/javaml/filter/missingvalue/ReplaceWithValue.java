/**
 * ReplaceWithValue.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.missingvalue;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.AbstractFilter;

/**
 * Replaces all missing values with a fixed value.
 * 
 * {@jmlSource}
 * 
 * @author Thomas Abeel
 * 
 */
public class ReplaceWithValue extends AbstractFilter {

	private double d;

	public ReplaceWithValue(double d) {
		this.d = d;
	}

	public void build(Dataset data) {
		// do nothing, this is not required

	}

	public Dataset filterDataset(Dataset data) {
		Dataset out = new SimpleDataset();
		for (Instance in : data) {
			out.add(filterInstance(in));
		}
		return out;

	}

	public Instance filterInstance(Instance inst) {
		double[] vals = new double[inst.size()];
		for (int i = 0; i < inst.size(); i++) {
			if (Double.isNaN(inst.value(i)))
				vals[i] = d;
			else
				vals[i] = inst.value(i);
		}
		return new SimpleInstance(vals,inst);
	}

}
