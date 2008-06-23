/**
 * %SVN.HEADER%
 */
package net.sf.javaml.classification;

import java.util.Map;

import net.sf.javaml.core.Instance;

public abstract class AbstractClassifier implements Classifier {
    @Override
    public Object classifyInstance(Instance instance) {
        Map<Object, Double> distribution = distributionForInstance(instance);
        double max = 0;
        Object out = null;
        for (Object key : distribution.keySet()) {
            if (distribution.get(key) > max) {
                max = distribution.get(key);
                out = key;
            }
        }
        // System.out.println("pred: "+out);
        // System.out.println("--");

        return out;
    }
}
