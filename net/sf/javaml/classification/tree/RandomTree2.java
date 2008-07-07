/**
 * %SVN.HEADER%
 */
package net.sf.javaml.classification.tree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.smartcardio.ATR;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;

/**
 * Based on Central Axis projection tree growing algorithm from Ho,1995, Random
 * Decision Forests
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
    private int noSplitAttributes = 5;

    private Random rg = new Random(System.currentTimeMillis());

    /* Mean of the second class of this split */
    private double[] vectorB = null;

    /* Mean of the first class of this split */
    private double[] origin = null;

    private double optimalThreshold;

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
        origin = new double[noSplitAttributes];
        vectorB = new double[noSplitAttributes];
        for (Instance i : data) {
            if (data.classIndex(i.classValue()) == 0) {
                count0++;
                for (int j = 0; j < splitAttributes.size(); j++) {
                    origin[j] += i.value(splitAttributes.get(j));
                }
            } else {
                count1++;
                for (int j = 0; j < splitAttributes.size(); j++) {
                    vectorB[j] += i.value(splitAttributes.get(j));
                }
            }
        }
        /* take mean */
        for (int i = 0; i < noSplitAttributes; i++) {
            origin[i] /= count0;
            vectorB[i] /= count1;
        }
        /* translate to new origin for vector operations */
        for (int i = 0; i < noSplitAttributes; i++) {
            vectorB[i] -= origin[i];
        }
        // System.out.println("norm B=" + norm(vectorB));
        Map<Instance, Double> distanceFromOrigin = new HashMap<Instance, Double>();
        SortedSet<Double> sortedDistances = new TreeSet<Double>();
        int lower = 0, higher = 0;
        for (Instance inst : data) {
            double[] A = new double[noSplitAttributes];
            for (int i = 0; i < noSplitAttributes; i++) {
                A[i] = inst.value(splitAttributes.get(i)) - origin[i];
            }
            double dotproduct = dotproduct(vectorB, A);
            double normB = norm(vectorB);
            double[] C = new double[noSplitAttributes];
            for (int i = 0; i < noSplitAttributes; i++) {
                C[i] = vectorB[i] * (dotproduct / (normB * normB));
            }


            System.out.println("C"+ Arrays.toString(C));
            double normC = Math.signum(C[0])*norm(C);
//            double normC =norm(C);
            distanceFromOrigin.put(inst, norm(C));
            sortedDistances.add(norm(C));
            if (normC < normB)
                lower++;
            else
                higher++;

        }

        optimalThreshold = 0;
        double optimalError = Double.MAX_VALUE;
        for (Double threshold : sortedDistances) {
            double[] leftCount = new double[2];
            int[] count = new int[2];
            double[] rightCount = new double[2];
            for (Instance i : data) {
                count[data.classIndex(i.classValue())]++;
                if (distanceFromOrigin.get(i) < threshold) {
                    leftCount[data.classIndex(i.classValue())]++;
                } else {
                    rightCount[data.classIndex(i.classValue())]++;
                }
            }
            double error = Math.min(leftCount[0] / count[0] + rightCount[1] / count[1], rightCount[0] / count[0]
                    + leftCount[1] / count[1]);

            if (error < optimalError) {
                optimalError = error;
                optimalThreshold = threshold;
            }

        }
        /* split training data and train children */
        Dataset left = new DefaultDataset();
        Dataset right = new DefaultDataset();
        while (data.size() > 0) {
            Instance inst = data.remove(0);
            if (distanceFromOrigin.get(inst) < optimalThreshold)
                left.add(inst);
            else
                right.add(inst);
        }
        // System.out.println("left size=" + left.size());
        // System.out.println("right size=" + right.size());
        leftChild = new RandomTree2(noSplitAttributes, rg);
        leftChild.buildClassifier(left);
        rightChild = new RandomTree2(noSplitAttributes, rg);
        rightChild.buildClassifier(right);
        // System.out.println("tree build for " + (left.size() + right.size()));
    }

    private double norm(double[] vector) {
        double out = 0;
        for (int i = 0; i < vector.length; i++) {
            out += vector[i] * vector[i];
        }
        return Math.sqrt(out);
    }

    private double dotproduct(double[] vector, double[] tmp) {
        double out = 0;
        for (int i = 0; i < vector.length; i++) {
            out += vector[i] * tmp[i];
        }
        return out;

    }

    @Override
    public Object classifyInstance(Instance instance) {

        if (finalClass != null)
            return finalClass;
        else {
            assert (vectorB != null);
            assert (origin != null);
            assert (leftChild != null);
            assert (rightChild != null);
            assert (splitAttributes != null);
            double[] tmp = new double[splitAttributes.size()];
            for (int i = 0; i < tmp.length; i++)
                tmp[i] -= origin[i];
            double dotproduct = dotproduct(vectorB, tmp);
            double norm = norm(vectorB);
            double[] newVector = new double[noSplitAttributes];
            for (int i = 0; i < noSplitAttributes; i++) {
                newVector[i] = vectorB[i] * (dotproduct / (norm * norm));
            }
            if (norm(newVector) < optimalThreshold)
                return leftChild.classifyInstance(instance);
            else
                return rightChild.classifyInstance(instance);
        }
    }

    @Override
    public Map<Object, Double> distributionForInstance(Instance instance) {
        throw new UnsupportedOperationException("Random trees do not provide class distribution information");
    }

}
