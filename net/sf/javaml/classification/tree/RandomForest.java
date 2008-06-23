/**
 * %SVN.HEADER%
 */
package net.sf.javaml.classification.tree;

import java.util.Map;
import java.util.Random;

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

    private Random rg;

    public RandomForest(int treeCount, boolean calculateOutOfBagErrorEstimate, int numAttributes, Random rg) {
        this.treeCount = treeCount;
        this.rg = rg;
        this.calculateOutOfBagErrorEstimate = calculateOutOfBagErrorEstimate;
        this.numAttributes = numAttributes;
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
        bagger = new Bagging(trees, rg);
        bagger.setCalculateOutOfBagErrorEstimate(calculateOutOfBagErrorEstimate);
        bagger.buildClassifier(data);

    }

    @Override
    public Object classifyInstance(Instance instance) {
        return bagger.classifyInstance(instance);
    }

    @Override
    public Map<Object, Double> distributionForInstance(Instance instance) {
        return bagger.distributionForInstance(instance);
    }

}
