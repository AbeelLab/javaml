/**
 * %SVN.HEADER%
 */
package net.sf.javaml.classification;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.javaml.classification.AbstractClassifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * ZeroR classifier implementation. This classifier will determine the class
 * distribution in the training data and will always return this as the
 * predicted class distribution.
 * 
 * @author Thomas Abeel
 * 
 */
public class ZeroR extends AbstractClassifier {

	private static final long serialVersionUID = -5506945214184891019L;

	private Map<Object, Double> mapping = null;

	@Override
	public void buildClassifier(Dataset data) {
		Map<Object, Double> mapping = new HashMap<Object, Double>();
		for (Instance i : data) {
			if (i.classValue() != null) {
				if (!mapping.containsKey(i.classValue()))
					mapping.put(i.classValue(), 0.0);
				mapping.put(i.classValue(), mapping.get(i.classValue()) + 1);

			}

		}
		this.mapping = Collections.unmodifiableMap(mapping);

	}

	@Override
	public Map<Object, Double> classDistribution(Instance instance) {
		return mapping;
	}

}
