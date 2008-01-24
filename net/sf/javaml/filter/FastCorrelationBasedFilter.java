/**
 * FastCorrelationBasedFilter.java
 *
 * %SVN.HEADER%
 *
 * Based on work by Ravi Bhim, ASU.
 */
package net.sf.javaml.filter;

import java.util.HashSet;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.exception.TrainingRequiredException;
import net.sf.javaml.utils.MathUtils;

/**
 * This {@link net.sf.javaml.filter.DatasetFilter} uses the FCBF algorithm to
 * remove some attributes from the instances of the data set. instances.
 * 
 * {@jmlSource}
 * 
 * @see net.sf.javaml.core.Instance
 * @see net.sf.javaml.core.Dataset
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class FastCorrelationBasedFilter extends AbstractFilter {

    public FastCorrelationBasedFilter() {
        this(0.0);
    }

    private double threshold;

    public FastCorrelationBasedFilter(double threshold) {
        this.threshold = threshold;
    }

    private AbstractFilter remove = null;

    public void build(Dataset data) {
        int[] toRemove = fcbf(data);
        remove = new RemoveAttributes(toRemove);
    }

    public Dataset filterDataset(Dataset data) {
        if (remove == null)
            throw new TrainingRequiredException();
        return remove.filterDataset(data);

    }

    public Instance filterInstance(Instance instance) {
        if (remove != null)
            return remove.filterInstance(instance);
        else
            throw new RuntimeException("Filter needs to be trained on dataset first");
    }

    /* Variables used by the 'fcbf' function */
    private double[] suList = null;

    private double[] suListDup = null;

    private int[] suOrder = null;

    private int[] valid = null;

    /**
     * The FCBF function applies the FCBF algorithm and outputs the list of
     * features that can be pruned.
     * 
     * @param delta
     *            Symmetrical Uncertainity threshold for filtering out features.
     */
    private int[] fcbf(Dataset data) {
        /*
         * STEP 1 a) Initialize the 'sulist', i.e the SU values of all the
         * features with respect to the class. b) Get the length of the valid
         * attributes. c) Scan over 'suduplist' to get 'suorder', i.e indexes of
         * features in descending order of their SU values.
         */

        int numAttrs = data.instance(0).size();
        meta_diff = getDiff(data);
        int len = 0;

        /* Memory initialization */
        suList = new double[numAttrs];
        suListDup = new double[numAttrs];

        /* Initializing 'suList' */
        for (int i = 0; i < numAttrs; i++) {
            /* Calculating Symmetrical Uncertainty with respect to the class */
            suList[i] = suListDup[i] = SU(data, i, numAttrs);
            if (suList[i] > threshold)
                len++;
        }

        suOrder = new int[len];
        valid = new int[len];

        double max;
        int maxIndex;
        for (int i = 0; i < len; i++) {
            max = 0;
            maxIndex = -1;

            for (int j = 0; j < numAttrs; j++)
                if (suListDup[j] > max) {
                    max = suListDup[j];
                    maxIndex = j;
                }

            suOrder[i] = maxIndex;
            suListDup[maxIndex] = (double) 0; /*
                                                 * Removing the max element in
                                                 * order to get the next maximum
                                                 * element in the next iteration
                                                 */
        }

        /* Step 2: Heart of the FCBF algorithm */

        /* Initializing the valid array */
        for (int i = 0; i < len; i++)
            valid[i] = 1; /* All features are valid initially */

        int fp, fq, fqd;

        fp = suOrder[0]; /*
                             * The feature with the highest SU value with
                             * respect to the class
                             */

        while (fp != -1) {
            fq = getNextElement(fp, len);

            if (fq != -1) {
                while (true) {
                    fqd = fq;
                    if (SU(data, fp, fq) >= suList[fq]) {
                        setInvalid(fq, len); // i.e valid[fq]=0
                        fq = getNextElement(fqd, len);
                    } else
                        fq = getNextElement(fq, len);

                    if (fq == -1)
                        break;
                }
            }
            fp = getNextElement(fp, len);
        }

        Vector<Integer> validIndices = new Vector<Integer>();
        for (int i = 0; i < len; i++)
            if (valid[i] != 0)
                validIndices.add(suOrder[i]);

        Vector<Integer> toRemove = new Vector<Integer>();
        for (int i = 0; i < numAttrs; i++) {
            toRemove.add(i);
        }
        toRemove.removeAll(validIndices);
        int[] out = new int[toRemove.size()];
        for (int i = 0; i < out.length; i++)
            out[i] = toRemove.get(i);

        return out;

    } // end of fcbf()

    private int[] getDiff(Dataset data) {
        int[] out = new int[data.instance(0).size() + 1];
        for (int i = 0; i < out.length - 1; i++) {
            HashSet<Double> set = new HashSet<Double>();
            for (Instance in : data)
                set.add(in.value(i));
            out[i] = set.size();
        }
        out[data.instance(0).size()] = data.numClasses();
        return out;
    }

    /**
     * This function returns the next valid element(attribute index) in
     * 'suOrder' after 'fp'
     * 
     * @param fp
     * @param len
     * @return Returns the next valid attribute index, or -1 if there are none.
     */
    private int getNextElement(int fp, int len) {
        int fpIndex = 0;

        for (int i = 0; i < len; i++)
            if (suOrder[i] == fp) {
                fpIndex = i;
                break;
            }

        for (int i = fpIndex + 1; i < len; i++)
            if (valid[i] == 1)
                return suOrder[i];

        /* No valid NextElement found */
        return -1;
    }

    /**
     * Sets the memory location in 'valid' whose corresponding 'suOrder' value
     * is 'fq'
     * 
     * @param fq
     * @param len
     *            Length of the valid feature list
     */
    private void setInvalid(int fq, int len) {
        for (int i = 0; i < len; i++)
            if (suOrder[i] == fq) {
                valid[i] = 0;
                return;
            }
    }

    /**
     * Contains for each attribute the number of different values.
     */
    private int[] meta_diff;

    /**
     * This function returns the entropy of an attribute pointed by 'index'
     * (ranges from 0 to numAttrs+1)
     * 
     * @param index
     *            Index of the attribute
     * @return Returns the entropy of the feature/attribute.
     */
    private double entropy(Dataset data, int attrIndex) {
        double ans = 0, temp;

        for (int i = 0; i < meta_diff[attrIndex]; i++) {
            temp = partialProb(data, attrIndex, i);
            if (temp != (double) 0)
                ans += temp * (Math.log(temp) / Math.log((double) 2.0));
        }
        return -ans;
    }

    /**
     * This function returns the partial probability value of the
     * attribute(mentioned by 'attrIndex' taking the value 'attrValue'
     * 
     * @param attrIndex
     *            Index of the attribute
     * @param attrValue
     *            Taking this particular value
     * @return Partial Probability
     */
    private double partialProb(Dataset data, int attrIndex, int attrValue) {
        int count = 0;

        for (int i = 0; i < data.size(); i++) {
            if (attrIndex == data.instance(0).size()) {
                if (MathUtils.eq(data.instance(i).classValue(), attrValue)) {
                    count++;
                }
            } else if (MathUtils.eq(data.instance(i).value(attrIndex), attrValue))
                count++;
        }
        if (count != 0)
            return ((double) count / (double) data.size());
        else
            return (double) 0;
    }

    /**
     * This function computes the conditional entropy of the attribute One
     * (mentioned by indexOne), given the attribute Two (mentioned by indexTwo)
     * 
     * @param indexOne
     *            Index of attribute One
     * @param indexTwo
     *            Index of attribute Two
     * @return Conditional Probability of One given Two
     */
    private double condEntropy(Dataset data, int indexOne, int indexTwo) {
        double ans = 0, temp, temp_ans, cond_temp;

        for (int j = 0; j < meta_diff[indexTwo]; j++) {
            temp = partialProb(data, indexTwo, j);
            temp_ans = 0;

            for (int i = 0; i < meta_diff[indexOne]; i++) {
                cond_temp = partialCondProb(data, indexOne, i, indexTwo, j);
                if (cond_temp != (double) 0)
                    temp_ans += cond_temp * (Math.log(cond_temp) / Math.log((double) 2.0));
            }
            ans += temp * temp_ans;
        }
        return -ans;
    }

    /**
     * This function computes the probability of feature/attribute One(given by
     * indexOne) taking value 'valueOne', given feature Two(given by indexTwo)
     * taking value 'valueTwo'
     * 
     * @param indexOne
     *            Index of feature One
     * @param valueOne
     *            Value of feature One
     * @param indexTwo
     *            Index of feature Two
     * @param valueTwo
     *            Value of feature Two
     * @return
     */
    private double partialCondProb(Dataset data, int indexOne, int valueOne, int indexTwo, int valueTwo) {
        int num = 0, den = 0;

        for (int i = 0; i < data.size(); i++) {
            double tmpOne, tmpTwo;

            if (indexOne == data.instance(0).size())
                tmpOne = data.instance(i).classValue();
            else
                tmpOne = data.instance(i).value(indexOne);

            if (indexTwo == data.instance(0).size())
                tmpTwo = data.instance(i).classValue();
            else
                tmpTwo = data.instance(i).value(indexTwo);

            if (MathUtils.eq(tmpTwo, valueTwo)) {
                den++;
                if (MathUtils.eq(tmpOne, valueOne))
                    num++;
            }
        }

        if (den != 0)
            return (double) num / (double) den;
        else
            return (double) 0;
    }

    /**
     * This function computes the information gain of feature 'indexOne' given
     * feature 'indexTwo' IG(indexOne,indexTwo) => ENTROPY(indexOne) -
     * condEntropy(indexOne,indexTwo)
     * 
     * @param indexOne
     *            feature One
     * @param indexTwo
     *            feature Two
     * @return Returns the Information Gain
     */
    private double informationGain(Dataset data, int indexOne, int indexTwo) {
        return entropy(data, indexOne) - condEntropy(data, indexOne, indexTwo);
    }

    /**
     * This function computes the Symmetrical Uncertainity of the features
     * pointed by 'indexOne' and 'indexTwo'
     * 
     * @param indexOne
     *            Feature One
     * @param indexTwo
     *            Feature Two
     * @return Returns Symmetrical Uncertainty
     */
    private double SU(Dataset data, int indexOne, int indexTwo) {
        double ig, e1, e2;

        ig = informationGain(data, indexOne, indexTwo);
        e1 = entropy(data, indexOne);
        e2 = entropy(data, indexTwo);

        if ((e1 + e2) != (double) 0)
            return ((double) 2 * (ig / (e1 + e2)));
        else
            return (double) 1;
    }

    public Dataset unfilterDataset(Dataset data) {
        throw new UnsupportedOperationException("One-way filter only");
    }

    public Instance unfilterInstance(Instance inst) {
        throw new UnsupportedOperationException("One-way filter only");
    }

}
