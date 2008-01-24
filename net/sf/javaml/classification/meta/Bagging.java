/**
 * Bagging.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.classification.meta;

import java.util.HashSet;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;

/**
 * Bagging meta learner. This implementation can also calculate the out-of-bag
 * error estimate while training at very little extra cost.
 * 
 *  {@jmlSource}
 * 
 * @author Thomas Abeel
 * 
 */
public class Bagging implements Classifier {

    private static final long serialVersionUID = -307153017973773314L;

    private Classifier[] classifiers;

    private int numClasses;

    public Bagging(Classifier[] classifiers) {
        this.classifiers = classifiers;
    }

    private boolean calculateOutOfBagErrorEstimate = false;

    private int positiveClass = 1;

    public void setCalculateOutOfBagErrorEstimate(boolean b) {
        this.calculateOutOfBagErrorEstimate = b;
    }

    /**
     * Sets which class should be considered the positive class for the out of
     * bag error estimate.
     * 
     * @param i
     */
    public void setPositiveClass(int i) {
        this.positiveClass = i;
    }

    private PerformanceMeasure outOfBagEstimate;

    public PerformanceMeasure getOutOfBagEstimate() {
        return outOfBagEstimate;
    }

    public void buildClassifier(Dataset data) {
        this.numClasses = data.numClasses();
        int tp = 0, fp = 0, tn = 0, fn = 0;
        for (int i = 0; i < classifiers.length; i++) {
            Dataset sample = DatasetTools.randomSample(data, data.size());
            classifiers[i].buildClassifier(sample);
            if (calculateOutOfBagErrorEstimate) {
                Dataset outOfBag = getInverse(data, sample);
                for (Instance inst : outOfBag) {
                    int predClass = classifiers[i].classifyInstance(inst);
                    if (predClass == positiveClass) {
                        if (inst.classValue() == positiveClass)
                            tp++;
                        else
                            fp++;
                    } else {
                        if (inst.classValue() == positiveClass)
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
        Dataset out = new SimpleDataset();
        for (Instance i : tmpD)
            out.add(i);
        return out;
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
