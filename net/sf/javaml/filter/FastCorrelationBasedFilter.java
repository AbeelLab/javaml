/**
 * FastCorrelationBasedFilter.java
 *
 * This file is part of the Java Machine Learning API
 * 
 * The Java Machine Learning API is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The Java Machine Learning API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning API; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 *
 * This class was originally written by Ravi Bhim, ASU.
 * 
 * This file was modified by Thomas Abeel to fit the Java-ML project
 */
package net.sf.javaml.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.utils.MathUtils;

/**
 * This class is used to compute the required mathematical computations for the
 * FCBF algorithm.
 * 
 * @author Ravi Bhim,ASU.
 * @author Thomas Abeel
 */

public class FastCorrelationBasedFilter implements Filter {

    public FastCorrelationBasedFilter() {
        this(0.0);
    }

    private double threshold;

    public FastCorrelationBasedFilter(double threshold) {
        this.threshold = threshold;
    }

    private Filter remove = null;

    public Dataset filterDataset(Dataset data) {
        int[] toRemove = fcbf(data);
        remove = new RemoveAttributes(toRemove);
        return remove.filterDataset(data);

    }

    public Instance filterInstance(Instance instance) {
        return remove.filterInstance(instance);
    }

    public Instance unfilterInstance(Instance instance) {
        return remove.unfilterInstance(instance);
    }

    /* Variables used by the 'fcbf' function */
    private double[] suList = null;

    private double[] suListDup = null;

    private int[] suOrder = null;

    private int[] valid = null;

    /**
     * The fcbc function applies the FCBF algorithm and outputs the list of
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

        int numAttrs = data.getInstance(0).size();
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

//        System.out.println("suOrder: " + Arrays.toString(suOrder));
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

//        System.out.println("suOrder" + Arrays.toString(suOrder));
//        System.out.println("suList" + Arrays.toString(suList));
//        System.out.println("valid" + Arrays.toString(valid));
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
        int[] out = new int[data.getInstance(0).size() + 1];
        for (int i = 0; i < out.length - 1; i++) {
            HashSet<Double> set = new HashSet<Double>();
            for (Instance in : data)
                set.add(in.getValue(i));
            out[i] = set.size();
        }
        out[data.getInstance(0).size()] = data.getNumClasses();
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
            if (attrIndex == data.getInstance(0).size()) {
                if (MathUtils.eq(data.getInstance(i).getClassValue(), attrValue)) {
                    count++;
                }
            } else if (MathUtils.eq(data.getInstance(i).getValue(attrIndex), attrValue))
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

            if (indexOne == data.getInstance(0).size())
                tmpOne = data.getInstance(i).getClassValue();
            else
                tmpOne = data.getInstance(i).getValue(indexOne);

            if (indexTwo == data.getInstance(0).size())
                tmpTwo = data.getInstance(i).getClassValue();
            else
                tmpTwo = data.getInstance(i).getValue(indexTwo);

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

}
