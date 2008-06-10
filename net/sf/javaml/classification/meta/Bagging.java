/**
 * Bagging.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.classification.meta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import net.sf.javaml.classification.AbstractClassifier;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;

/**
 * Bagging meta learner. This implementation can also calculate the out-of-bag
 * error estimate while training at very little extra cost.
 * 
 * {@jmlSource}
 * 
 * @author Thomas Abeel
 * 
 */
public class Bagging extends AbstractClassifier {

    /**
     * 
     */
    private static final long serialVersionUID = 5571842927861670307L;

    private Classifier[] classifiers;

    // private int numClasses;

    private Dataset dataReference = null;

    private Random rg;

    public Bagging(Classifier[] classifiers, Random rg) {
        this.classifiers = classifiers;
        this.rg = rg;
    }

    private boolean calculateOutOfBagErrorEstimate = false;

    private Object positiveClass = 1;

    public void setCalculateOutOfBagErrorEstimate(boolean b) {
        this.calculateOutOfBagErrorEstimate = b;
    }

    /**
     * Sets which class should be considered the positive class for the out of
     * bag error estimate.
     * 
     * @param i
     */
    public void setPositiveClass(Object i) {
        this.positiveClass = i;
    }

    private PerformanceMeasure outOfBagEstimate;

    public PerformanceMeasure getOutOfBagEstimate() {
        return outOfBagEstimate;
    }

    public void buildClassifier(Dataset data) {
        // this.numClasses = data.classes().size();
        this.dataReference = data;
        int tp = 0, fp = 0, tn = 0, fn = 0;
        for (int i = 0; i < classifiers.length; i++) {
            Dataset sample = DatasetTools.bootstrap(data, data.size(), rg);
            classifiers[i].buildClassifier(sample);
            if (calculateOutOfBagErrorEstimate) {
                Dataset outOfBag = getInverse(data, sample);
                for (Instance inst : outOfBag) {
                    Object predClass = classifiers[i].classifyInstance(inst);
                    if (predClass == positiveClass) {
                        if (inst.classValue().equals(positiveClass))
                            tp++;
                        else
                            fp++;
                    } else {
                        if (inst.classValue().equals(positiveClass))
                            fn++;
                        else
                            tn++;
                    }
                }
            }

        }
        outOfBagEstimate = new PerformanceMeasure(tp, tn, fp, fn);

    }

    /*
     * Get the inverse of the sample set.
     */
    public static Dataset getInverse(Dataset data, Dataset sample) {
        HashSet<Instance> tmpD = new HashSet<Instance>();
        for (Instance i : data) {
            tmpD.add(i);
        }
        for (Instance i : sample) {
            tmpD.remove(i);
        }
        Dataset out = new DefaultDataset();
        for (Instance i : tmpD)
            out.add(i);
        return out;
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
        for (Object o : dataReference.classes())
            membership.put(o, 0.0);
        for (int i = 0; i < classifiers.length; i++) {
            Object prediction = classifyInstance(instance);
            membership.put(prediction, membership.get(prediction) + (1.0 / classifiers.length));// [classifiers[i].classifyInstance(instance)]++;
        }
        // for (int i = 0; i < this.numClasses; i++)
        // membership[i] /= classifiers.length;
        return membership;

    }
}
