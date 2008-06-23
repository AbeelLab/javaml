/**
 * %SVN.HEADER%
 * 
 * Based on work by Eibe Frank and Richard Kirkby.
 */
package net.sf.javaml.classification.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import net.sf.javaml.classification.AbstractClassifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.MissingClassFilter;
import net.sf.javaml.utils.ArrayUtils;
import net.sf.javaml.utils.ContingencyTables;
import net.sf.javaml.utils.MathUtils;

/**
 * Class for constructing a tree that considers K randomly chosen attributes at
 * each node. Performs no pruning.
 * 
 * {@jmlSource}
 * 
 * @author Thomas Abeel
 * @author Eibe Frank
 * @author Richard Kirkby
 */
public class RandomTree extends AbstractClassifier {

    private static final long serialVersionUID = 2531824309323125009L;

    /** The subtrees appended to this tree. */
    private RandomTree[] m_Successors;

    /** The attribute to split on. */
    private int m_Attribute = -1;

    /** The split point. */
    private double m_SplitPoint = Double.NaN;

    /** The class distribution from the training data. */
    private double[][] m_Distribution = null;

    /** Class probabilities from the training data. */
    private double[] m_ClassProbs = null;

    /** Minimum number of instances for leaf. */
    private double m_MinNum = 1.0;

    /** The number of attributes considered for a split. */
    private int m_KValue = 1;

    /** The maximum depth of the tree (0 = unlimited) */
    private int m_MaxDepth = 0;

    public RandomTree() {

    }

    private RandomTree(Dataset data) {
        this.sourceReference = data;
    }

    /**
     * Set the value of K. K is the number of attributes to consider for a
     * split.
     * 
     * @param k
     *            Value to assign to K.
     */
    public void setKValue(int k) {

        m_KValue = k;
    }

    private Dataset sourceReference = null;

    /**
     * Builds classifier.
     * 
     * @param data
     *            the data to train with
     * @throws Exception
     *             if something goes wrong or the data doesn't fit
     */
    public void buildClassifier(Dataset data) {
        this.sourceReference = data;
        // Make sure K value is in range
        if (m_KValue > data.noAttributes() - 1)
            m_KValue = data.noAttributes() - 1;
        if (m_KValue < 1)
            m_KValue = (int) MathUtils.log2(data.noAttributes()) + 1;

        MissingClassFilter mc = new MissingClassFilter();
        // Dataset train = mc.filterDataset(data);
        mc.filterDataset(data);

        // Create array of sorted indices and weights
        int[][] sortedIndices = new int[data.noAttributes()][0];
        double[][] weights = new double[data.noAttributes()][0];
        double[] vals = new double[data.size()];
        for (int j = 0; j < data.noAttributes(); j++) {
            // if (j != train.classIndex()) {
            weights[j] = new double[data.size()];
            // Sorted indices are computed for numeric attributes
            for (int i = 0; i < data.size(); i++) {
                Instance inst = data.instance(i);
                vals[i] = inst.value(j);
            }
            sortedIndices[j] = ArrayUtils.sort(vals);
            for (int i = 0; i < data.size(); i++) {
                weights[j][i] = 1.0;// train.instance(sortedIndices[j][i]).weight();
            }
        }

        // Compute initial class counts
        double[] classProbs = new double[data.classes().size()];
        // List<Object>classes=new Vector<Object>();
        // classes.addAll(train.classes());
        for (int i = 0; i < data.size(); i++) {
            Instance inst = data.instance(i);
            classProbs[data.classIndex(inst.classValue())] += 1.0;
        }

        // Create the attribute indices window
        int[] attIndicesWindow = new int[data.noAttributes() - 1];
        int j = 0;
        for (int i = 0; i < attIndicesWindow.length; i++) {
            attIndicesWindow[i] = j++;
        }

        // Build tree
        buildTree(sortedIndices, weights, data, classProbs, m_MinNum, true, attIndicesWindow, new Random(), 0);

    }

    /**
     * Recursively generates a tree.
     * 
     * @param sortedIndices
     *            the indices of the Dataset
     * @param weights
     *            the weights of the Dataset
     * @param data
     *            the data to work with
     * @param classProbs
     *            the class distribution
     * @param header
     *            the header of the data
     * @param minNum
     *            the minimum number of Dataset per leaf
     * @param debug
     *            whether debugging is on
     * @param attIndicesWindow
     *            the attribute window to choose attributes from
     * @param random
     *            random number generator for choosing random attributes
     * @param depth
     *            the current depth
     * @throws Exception
     *             if generation fails
     */
    private void buildTree(int[][] sortedIndices, double[][] weights, Dataset data, double[] classProbs, double minNum,
            boolean debug, int[] attIndicesWindow, Random random, int depth) {

        // Check if node doesn't contain enough Dataset or is pure
        // or maximum depth reached
        m_ClassProbs = new double[classProbs.length];
        System.arraycopy(classProbs, 0, m_ClassProbs, 0, classProbs.length);
        if (MathUtils.lt(ArrayUtils.sum(m_ClassProbs), 2 * m_MinNum)
                || MathUtils.eq(m_ClassProbs[ArrayUtils.maxIndex(m_ClassProbs)], ArrayUtils.sum(m_ClassProbs))
                || ((m_MaxDepth > 0) && (depth >= m_MaxDepth))) {

            // Make leaf
            m_Attribute = -1;
            m_Distribution = new double[1][m_ClassProbs.length];
            for (int i = 0; i < m_ClassProbs.length; i++) {
                m_Distribution[0][i] = m_ClassProbs[i];
            }
            ArrayUtils.normalize(m_ClassProbs);
            return;
        }

        // Compute class distributions and value of splitting
        // criterion for each attribute
        double[] vals = new double[data.noAttributes()];
        double[][][] dists = new double[data.noAttributes()][0][0];
        double[][] props = new double[data.noAttributes()][0];
        double[] splits = new double[data.noAttributes()];

        // Investigate K random attributes
        int attIndex = 0;
        int windowSize = attIndicesWindow.length;
        int k = m_KValue;
        boolean gainFound = false;
        while ((windowSize > 0) && (k-- > 0 || !gainFound)) {

            int chosenIndex = random.nextInt(windowSize);
            attIndex = attIndicesWindow[chosenIndex];

            // shift chosen attIndex out of window
            attIndicesWindow[chosenIndex] = attIndicesWindow[windowSize - 1];
            attIndicesWindow[windowSize - 1] = attIndex;
            windowSize--;

            splits[attIndex] = distribution(props, dists, attIndex, sortedIndices[attIndex], weights[attIndex], data);
            vals[attIndex] = gain(dists[attIndex], priorVal(dists[attIndex]));

            if (MathUtils.gt(vals[attIndex], 0))
                gainFound = true;
        }

        // Find best attribute
        m_Attribute = ArrayUtils.maxIndex(vals);
        m_Distribution = dists[m_Attribute];

        // Any useful split found?
        if (MathUtils.gt(vals[m_Attribute], 0)) {

            // Build subtrees
            m_SplitPoint = splits[m_Attribute];
            int[][][] subsetIndices = new int[m_Distribution.length][data.noAttributes()][0];
            double[][][] subsetWeights = new double[m_Distribution.length][data.noAttributes()][0];
            splitData(subsetIndices, subsetWeights, m_Attribute, m_SplitPoint, sortedIndices, weights, m_Distribution,
                    data);
            m_Successors = new RandomTree[m_Distribution.length];
            for (int i = 0; i < m_Distribution.length; i++) {
                m_Successors[i] = new RandomTree(data);
                m_Successors[i].setKValue(m_KValue);
                m_Successors[i].m_MaxDepth = m_MaxDepth;// setMaxDepth(getMaxDepth());
                m_Successors[i].buildTree(subsetIndices[i], subsetWeights[i], data, m_Distribution[i], m_MinNum, true,
                        attIndicesWindow, random, depth + 1);
            }
        } else {

            // Make leaf
            m_Attribute = -1;
            m_Distribution = new double[1][m_ClassProbs.length];
            for (int i = 0; i < m_ClassProbs.length; i++) {
                m_Distribution[0][i] = m_ClassProbs[i];
            }
        }

        // Normalize class counts
        ArrayUtils.normalize(m_ClassProbs);
    }

    /**
     * Splits Dataset into subsets.
     * 
     * @param subsetIndices
     *            the sorted indices of the subset
     * @param subsetWeights
     *            the weights of the subset
     * @param att
     *            the attribute index
     * @param splitPoint
     *            the splitpoint for numeric attributes
     * @param sortedIndices
     *            the sorted indices of the whole set
     * @param weights
     *            the weights of the whole set
     * @param dist
     *            the distribution
     * @param data
     *            the data to work with
     * @throws Exception
     *             if something goes wrong
     */
    private void splitData(int[][][] subsetIndices, double[][][] subsetWeights, int att, double splitPoint,
            int[][] sortedIndices, double[][] weights, double[][] dist, Dataset data) {

        int j;
        int[] num;

        // For each attribute
        for (int i = 0; i < data.noAttributes(); i++) {
            num = new int[2];
            for (int k = 0; k < 2; k++) {
                subsetIndices[k][i] = new int[sortedIndices[i].length];
                subsetWeights[k][i] = new double[weights[i].length];
            }
            for (j = 0; j < sortedIndices[i].length; j++) {
                Instance inst = data.instance(sortedIndices[i][j]);
                int subset = MathUtils.lt(inst.value(att), splitPoint) ? 0 : 1;
                subsetIndices[subset][i][num[subset]] = sortedIndices[i][j];
                subsetWeights[subset][i][num[subset]] = weights[i][j];
                num[subset]++;

            }
            // Trim arrays
            for (int k = 0; k < num.length; k++) {
                int[] copy = new int[num[k]];
                System.arraycopy(subsetIndices[k][i], 0, copy, 0, num[k]);
                subsetIndices[k][i] = copy;
                double[] copyWeights = new double[num[k]];
                System.arraycopy(subsetWeights[k][i], 0, copyWeights, 0, num[k]);
                subsetWeights[k][i] = copyWeights;
            }
        }
    }

    /**
     * Computes class distribution for an attribute.
     * 
     * @param props
     * @param dists
     * @param att
     *            the attribute index
     * @param sortedIndices
     *            the sorted indices of the data
     * @param weights
     * @param data
     *            the data to work with
     * @throws Exception
     *             if something goes wrong
     */
    private double distribution(double[][] props, double[][][] dists, int att, int[] sortedIndices, double[] weights,
            Dataset data) {

        double splitPoint = Double.NaN;
        double[][] dist = null;

        // For numeric attributes
        double[][] currDist = new double[2][data.classes().size()];
        dist = new double[2][data.classes().size()];

        // Move all Dataset into second subset
        for (int j = 0; j < sortedIndices.length; j++) {
            Instance inst = data.instance(sortedIndices[j]);
            // if (inst.isMissing(att)) {
            // break;
            // }
            currDist[1][data.classIndex(inst.classValue())] += weights[j];
        }
        double priorVal = priorVal(currDist);
        for (int j = 0; j < currDist.length; j++) {
            System.arraycopy(currDist[j], 0, dist[j], 0, dist[j].length);
        }

        // Try all possible split points
        double currSplit = data.instance(sortedIndices[0]).value(att);
        double currVal, bestVal = -Double.MAX_VALUE;
        for (int i = 0; i < sortedIndices.length; i++) {
            Instance inst = data.instance(sortedIndices[i]);
            if (MathUtils.gt(inst.value(att), currSplit)) {
                currVal = gain(currDist, priorVal);
                if (MathUtils.gt(currVal, bestVal)) {
                    bestVal = currVal;
                    splitPoint = (inst.value(att) + currSplit) / 2.0;
                    for (int j = 0; j < currDist.length; j++) {
                        System.arraycopy(currDist[j], 0, dist[j], 0, dist[j].length);
                    }
                }
            }
            currSplit = inst.value(att);
            currDist[0][data.classIndex(inst.classValue())] += weights[i];
            currDist[1][data.classIndex(inst.classValue())] -= weights[i];
        }

        // Compute weights
        props[att] = new double[dist.length];
        for (int k = 0; k < props[att].length; k++) {
            props[att][k] = ArrayUtils.sum(dist[k]);
        }
        if (MathUtils.eq(ArrayUtils.sum(props[att]), 0)) {
            for (int k = 0; k < props[att].length; k++) {
                props[att][k] = 1.0 / (double) props[att].length;
            }
        } else {
            ArrayUtils.normalize(props[att]);
        }
        // Return distribution and split point
        dists[att] = dist;
        return splitPoint;
    }

    /**
     * Computes value of splitting criterion before split.
     * 
     * @param dist
     *            the distributions
     * @return the splitting criterion
     */
    private double priorVal(double[][] dist) {

        return ContingencyTables.entropyOverColumns(dist);
    }

    /**
     * Computes value of splitting criterion after split.
     * 
     * @param dist
     *            the distributions
     * @param priorVal
     *            the splitting criterion
     * @return the gain after the split
     */
    private double gain(double[][] dist, double priorVal) {

        return priorVal - ContingencyTables.entropyConditionedOnRows(dist);
    }

    //
    // public Object classifyInstance(Instance instance) {
    //
    // return ArrayUtils.maxIndex(distributionForInstance(instance));
    //
    // }

    /**
     * Computes class distribution of an instance using the decision tree.
     * 
     * @param instance
     *            the instance to compute the distribution for
     * @return the computed class distribution
     * @throws Exception
     *             if computation fails
     */
    @Override
    public Map<Object, Double> distributionForInstance(Instance instance) {
        Map<Object, Double> returnedDist = null;
        if (m_Attribute > -1) {
            // For numeric attributes
            if (MathUtils.lt(instance.value(m_Attribute), m_SplitPoint)) {
                returnedDist = m_Successors[0].distributionForInstance(instance);
            } else {
                returnedDist = m_Successors[1].distributionForInstance(instance);
            }
        }
        if ((m_Attribute == -1) || (returnedDist == null)) {
            // Node is a leaf or successor is empty
            List<Object> classes = new Vector<Object>();
            classes.addAll(sourceReference.classes());
            HashMap<Object, Double> out = new HashMap<Object, Double>();
            for (int i = 0; i < m_ClassProbs.length; i++)
                out.put(classes.get(i), m_ClassProbs[i]);
            // return m_ClassProbs;
            return out;
        } else {
            return returnedDist;
        }
    }
}
