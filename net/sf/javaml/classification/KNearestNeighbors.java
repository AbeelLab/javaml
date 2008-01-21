/**
 * KNearestNeighbors.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.classification;

import java.util.List;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.utils.ArrayUtils;

/**
 * Implementation of the K nearest neighbor (KNN) classification algorithm.
 * 
 * @author Thomas Abeel
 *
 */
public class KNearestNeighbors implements Classifier {

    private static final long serialVersionUID = 1560149339188819924L;

    private Dataset training;

    private EuclideanDistance euc = new EuclideanDistance();

    private int k = 5;

    private KNearestNeighbors(int k) {
        this.k = k;
    }

    public void buildClassifier(Dataset data) {
        this.training = data;

    }

    public int classifyInstance(Instance instance) {
        return ArrayUtils.maxIndex(distributionForInstance(instance));
    }

    public double[] distributionForInstance(Instance instance) {
        List<Instance> neighbors = DatasetTools.getNearestK(training, euc, instance, k);
        double[] output = new double[training.numClasses()];
        for (Instance i : neighbors) {
            output[i.classValue()]++;
        }
        ArrayUtils.normalize(output);
        return output;

    }

}
