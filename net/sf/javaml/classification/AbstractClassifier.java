/**
 * %SVN.HEADER%
 */
package net.sf.javaml.classification;

import java.util.Map;

import net.sf.javaml.core.Instance;

public abstract class AbstractClassifier implements Classifier {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4461661354949399603L;

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
}
