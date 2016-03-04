/**
 * %SVN.HEADER%
 */
package net.sf.javaml.classification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

public abstract class AbstractClassifier implements Classifier {

	private static final long serialVersionUID = -4461661354949399603L;

	protected Set<Object> parentClasses = null;

	@Override
	public Object classify(Instance instance) {
		Map<Object, Double> distribution = classDistribution(instance);
		double max = 0;
		Object out = null;
		for (Object key : distribution.keySet()) {
			if (distribution.get(key) > max) {
				max = distribution.get(key);
				out = key;
			}
		}
		return out;
	}

	@Override
	public Map<Object, Double> classDistribution(Instance instance) {
		HashMap<Object, Double> out = new HashMap<Object, Double>();
		for (Object o : parentClasses) {
			out.put(o, 0.0);
		}
		out.put(classify(instance), 1.0);
		return out;

	}

	@Override
	public void buildClassifier(Dataset data) {
		this.parentClasses = new HashSet<Object>();
		parentClasses.addAll(data.classes());

	}
}
