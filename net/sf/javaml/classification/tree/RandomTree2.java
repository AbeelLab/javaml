/**
 * %SVN.HEADER%
 */
package net.sf.javaml.classification.tree;

import java.util.Map;
import java.util.Random;
import java.util.Vector;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;

/**
 * Simple and fast implementation of the RandomTree classifier.
 * 
 * Currently only works for binary problems.
 * 
 * @author Thomas Abeel
 * 
 */
public class RandomTree2 implements Classifier {
    /**
     * 
     */
    private static final long serialVersionUID = -6421557885832628441L;

    /* Number of attributes to use to split this node */
    private int noSplitAttributes = -1;

    private Random rg = null;

    /* Mean of the second class of this split */
    private float[] rightCenter = null;

    /* Mean of the first class of this split */
    private float[] leftCenter = null;

    private Object finalClass = null;

    private RandomTree2 leftChild = null;

    private RandomTree2 rightChild = null;

    private Vector<Integer> splitAttributes = null;

    public RandomTree2(int attributes, Random rg) {
        this.rg = rg;
        this.noSplitAttributes = attributes;
    }

    @Override
    public void buildClassifier(Dataset data) {
        if (data.classes().size() == 1) {
            finalClass = data.classes().first();
            data.clear();
            return;
        }

        /* determine attributes to split on */
        splitAttributes = new Vector<Integer>();
        for (int i = 0; i < data.noAttributes(); i++)
            splitAttributes.add(i);
        while (splitAttributes.size() > noSplitAttributes) {
            splitAttributes.remove(rg.nextInt(splitAttributes.size()));
        }

        /* calculate mean for each class */
        int count0 = 0, count1 = 0;
        leftCenter = new float[noSplitAttributes];
        rightCenter = new float[noSplitAttributes];
        for (Instance inst : data) {
            if (data.classIndex(inst.classValue()) == 0) {
                count0++;
                for (int j = 0; j < splitAttributes.size(); j++) {
                    leftCenter[j] += inst.value(splitAttributes.get(j));
                }
            } else {
                count1++;
                for (int j = 0; j < splitAttributes.size(); j++) {
                    rightCenter[j] += inst.value(splitAttributes.get(j));
                }
            }
        }

        for (int i = 0; i < noSplitAttributes; i++) {
            leftCenter[i] /= count0;
            rightCenter[i] /= count1;
        }

        /* place-holder for instances */
        double[] tmp = new double[noSplitAttributes];
        /* data sets to construct children */
        Dataset left = new DefaultDataset();
        Dataset right = new DefaultDataset();
        for (Instance inst : data) {
            for (int i = 0; i < noSplitAttributes; i++) {
                tmp[i] = inst.value(splitAttributes.get(i));
            }
            double distLeft = dist(tmp, leftCenter);
            double distRight = dist(tmp, rightCenter);
            if (distLeft > distRight)
                right.add(inst);
            else
                left.add(inst);

        }

        leftChild = new RandomTree2(noSplitAttributes, rg);
        leftChild.buildClassifier(left);
        rightChild = new RandomTree2(noSplitAttributes, rg);
        rightChild.buildClassifier(right);

    }

    private double dist(double[] a, float[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.abs(a[i] - b[i]);
        }
        return sum;
    }

    @Override
    public Object classifyInstance(Instance instance) {

        if (finalClass != null)
            return finalClass;
        else {
            assert (rightCenter != null);
            assert (leftCenter != null);
            assert (leftChild != null);
            assert (rightChild != null);
            assert (splitAttributes != null);
            double[] tmp = new double[noSplitAttributes];
            for (int i = 0; i < noSplitAttributes; i++) {
                tmp[i] = instance.value(splitAttributes.get(i));

            }
            double distLeft = dist(tmp, leftCenter);
            double distRight = dist(tmp, rightCenter);
            if (distLeft > distRight)
                return rightChild.classifyInstance(instance);
            else
                return leftChild.classifyInstance(instance);
        }
    }

    @Override
    public Map<Object, Double> distributionForInstance(Instance instance) {
        throw new UnsupportedOperationException("Random trees do not provide class distribution information");
    }

}
