/**
 * RandomForest.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.classification.tree;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.classification.meta.Bagging;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

public class RandomForest implements Classifier {

    /**
     * 
     */
    private static final long serialVersionUID = 5832370995432897745L;

    private Bagging bagger;

    private int treeCount;

    private int numAttributes;

    public void setNumAttributes(int k) {
        this.numAttributes = k;

    }

    private boolean calculateOutOfBagErrorEstimate = false;

    public RandomForest(int treeCount, boolean calculateOutOfBagErrorEstimate) {
        this.treeCount = treeCount;
        this.calculateOutOfBagErrorEstimate = calculateOutOfBagErrorEstimate;
    }

    public PerformanceMeasure getOutOfBagErrorEstimate() {
        return bagger.getOutOfBagEstimate();
    }

    public void buildClassifier(Dataset data) {
        RandomTree[] trees = new RandomTree[treeCount];
        for (int i = 0; i < trees.length; i++) {
            trees[i] = new RandomTree();
            trees[i].setKValue(numAttributes);
        }
        bagger = new Bagging(trees);
        bagger.setCalculateOutOfBagErrorEstimate(calculateOutOfBagErrorEstimate);
        bagger.buildClassifier(data);

    }

    public int classifyInstance(Instance instance) {
        return bagger.classifyInstance(instance);
    }

    public double[] distributionForInstance(Instance instance) {
        return bagger.distributionForInstance(instance);
    }

}
