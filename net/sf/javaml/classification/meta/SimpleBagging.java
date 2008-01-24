/**
 * SimpleBagging.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.classification.meta;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;

/**
 * Bootstrap aggregating (Bagging) meta learner. This is the most basic implementation of Bagging.
 * 
 * @{jmlSource}
 * 
 * @author Thomas Abeel
 * 
 */
public class SimpleBagging implements Classifier {

    private static final long serialVersionUID = -307153017973773314L;

    private Classifier[] classifiers;

    private int numClasses;

    public SimpleBagging(Classifier[] classifiers) {
        this.classifiers = classifiers;
    }

    public void buildClassifier(Dataset data) {
        this.numClasses = data.numClasses();
        for (int i = 0; i < classifiers.length; i++) {
            Dataset sample = DatasetTools.randomSample(data, data.size());
            classifiers[i].buildClassifier(sample);
        }
    }

    public int classifyInstance(Instance instance) {
        double[] membership = this.distributionForInstance(instance);
        double max = membership[0];
        int index = 0;

        for (int i = 1; i < membership.length; i++) {
            if (membership[i] > max) {
                max = membership[i];
                index = 1;
            }
        }
        return index;
    }

    public double[] distributionForInstance(Instance instance) {
        double[] membership = new double[this.numClasses];
        for (int i = 0; i < classifiers.length; i++)
            membership[classifiers[i].classifyInstance(instance)]++;
        for (int i = 0; i < this.numClasses; i++)
            membership[i] /= classifiers.length;
        return membership;

    }
}
