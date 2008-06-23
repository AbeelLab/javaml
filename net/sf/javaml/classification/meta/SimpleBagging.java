/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification.meta;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.sf.javaml.classification.AbstractClassifier;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;

/**
 * Bootstrap aggregating (Bagging) meta learner. This is the most basic
 * implementation of Bagging.
 * 
 * {@jmlSource}
 * 
 * @author Thomas Abeel
 * 
 */
public class SimpleBagging extends AbstractClassifier {

    /**
     * 
     */
    private static final long serialVersionUID = 208101377048893813L;

    private Classifier[] classifiers;

    private Random rg;

    public SimpleBagging(Classifier[] classifiers, Random rg) {
        this.classifiers = classifiers;
        this.rg = rg;
    }

    private Dataset reference = null;

    public void buildClassifier(Dataset data) {
        // this.numClasses = data.numClasses();
        this.reference = data;
        for (int i = 0; i < classifiers.length; i++) {
            Dataset sample = DatasetTools.bootstrap(data, data.size(), rg);
            classifiers[i].buildClassifier(sample);
        }
    }

    // public int classifyInstance(Instance instance) {
    // double[] membership = this.distributionForInstance(instance);
    // double max = membership[0];
    // int index = 0;
    //
    // for (int i = 1; i < membership.length; i++) {
    // if (membership[i] > max) {
    // max = membership[i];
    // index = 1;
    // }
    // }
    // return index;
    // }

    @Override
    public Map<Object, Double> distributionForInstance(Instance instance) {
        Map<Object, Double> membership = new HashMap<Object, Double>();
        for (Object o : reference.classes())
            membership.put(o, 0.0);
        for (int i = 0; i < classifiers.length; i++) {
            Object prediction = classifiers[i].classifyInstance(instance);
            membership.put(prediction, membership.get(prediction) + (1.0 / classifiers.length));// [classifiers[i].classifyInstance(instance)]++;
        }
        // for (int i = 0; i < this.numClasses; i++)
        // membership[i] /= classifiers.length;
        return membership;

    }
}
