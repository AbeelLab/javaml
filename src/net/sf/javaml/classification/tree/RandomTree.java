/**
 * %SVN.HEADER%
 */
package net.sf.javaml.classification.tree;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
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
public class RandomTree implements Classifier {
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

    private RandomTree leftChild = null;

    private RandomTree rightChild = null;

    private Vector<Integer> splitAttributes = null;

    private SortedSet<Object> parentClasses = null;

    private RandomTree(int attributes, Random rg, SortedSet<Object> classes) {
        this.rg = rg;
        this.noSplitAttributes = attributes;
        this.parentClasses = classes;
    }

    public RandomTree(int attributes, Random rg) {
        this(attributes, rg, null);
    }

    @Override
    public void buildClassifier(Dataset data) {
        if (parentClasses == null)
            parentClasses = data.classes();

        if (data.classes().size() == 1) {
            finalClass = data.classes().first();
            data.clear();
            return;
        }
        Dataset left = null, right = null;
        boolean correctSplit = false;
        /* To keep track of how many times we already tried to split the data */
        int iterationCount = 0;
        while (!correctSplit) {
            iterationCount++;

            /*
             * Select the attributes on which to split the data.
             * 
             * When we face problems to split, we start using more attributes,
             * to force a split.
             */
            splitAttributes = new Vector<Integer>();
            for (int i = 0; i < data.noAttributes(); i++)
                splitAttributes.add(i);

            while (splitAttributes.size() / (iterationCount * iterationCount) > noSplitAttributes) {
            	splitAttributes.remove(rg.nextInt(splitAttributes.size()));
            }

            /* calculate mean for each class */
            int count0 = 0, count1 = 0;
            leftCenter = new float[splitAttributes.size()];
            rightCenter = new float[splitAttributes.size()];
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

            for (int i = 0; i < splitAttributes.size(); i++) {
                leftCenter[i] /= count0;
                rightCenter[i] /= count1;
            }

            /* place-holder for instances */
            double[] tmp = new double[splitAttributes.size()];
            /* data sets to construct children */
            left = new DefaultDataset();
            right = new DefaultDataset();
            for (Instance inst : data) {
                for (int i = 0; i < splitAttributes.size(); i++) {
                    tmp[i] = inst.value(splitAttributes.get(i));
                }
                double distLeft = dist(tmp, leftCenter);
                double distRight = dist(tmp, rightCenter);
                if (distLeft > distRight)
                    right.add(inst);
                else
                    left.add(inst);

            }
            correctSplit = left.size() != 0 && right.size() != 0;

            if (!correctSplit) {
                if ((iterationCount * iterationCount) * noSplitAttributes > data.noAttributes()) {
                    /*
                     * This data set can not be split properly. This is most
                     * likely due to ambiguous training data. Randomly select
                     * one of the possible classes as output class.
                     */
                    Vector<Object> possibleClasses = new Vector<Object>();
                    possibleClasses.addAll(data.classes());
                    this.finalClass = possibleClasses.get(rg.nextInt(possibleClasses.size()));
                    data.clear();
                    left = null;
                    right = null;
                    return;

                }
            }
        }
        leftChild = new RandomTree(noSplitAttributes, rg, parentClasses);
        leftChild.buildClassifier(left);
        rightChild = new RandomTree(noSplitAttributes, rg, parentClasses);
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
    public Object classify(Instance instance) {

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
                return rightChild.classify(instance);
            else
                return leftChild.classify(instance);
        }
    }

    @Override
    public Map<Object, Double> classDistribution(Instance instance) {
        HashMap<Object, Double> out = new HashMap<Object, Double>();
        for (Object o : parentClasses) {
            out.put(o, 0.0);
        }
        out.put(classify(instance), 1.0);
        return out;

    }

}
