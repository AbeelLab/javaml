/*
 * SMOPlatt.java 
 * -----------------------
 * Copyright (C) 2005-2006  Thomas Abeel
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Author: Thomas Abeel
 * Created on: 15-jan-2007 - 17:33:07
 */
package net.sf.javaml.classification.svm;

import java.util.HashSet;
import java.util.Random;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;

/**
 * An implementation of the SMO algorithm for SVM training from "Sequential
 * Minimal Optimization: A fast algorithm for training support vector machines",
 * J. Platt, 1998.
 * 
 * 
 * @author Thomas Abeel
 * 
 */

public class SMOPlatt implements Classifier {
    /**
     * A local reference to the dataset used for training, this is only used for
     * training and will be <code>null</code> afterwards.
     */
    private Dataset data;

    /**
     * The complexity parameter of the SVM. This is the penalty parameter for
     * the error term.
     */
    private double c;

    /**
     * The tolerance for checking the KKT conditions
     */
    private static final double TOL = 0.001;

    /**
     * The class value that indicates positive samples.
     */
    private int positiveClassIndex = 0;

    /**
     * The distance measure to be used in this instance of SMO. In Support
     * Vector Machines the distance measure is often called the kernel.
     */
    private DistanceMeasure kernel = null;

    /**
     * Constructs a new Support Vector Machine that will be trained using the
     * SMO algorithm.
     * 
     * The constructor takes the complexity penalty and the index of the
     * positive class as parameters.
     * 
     * @param c
     *            the complexity value
     * @param positiveClassIndex
     *            the index of the positive class, all other indices will be
     *            considered negative samples.
     */
    public SMOPlatt(double c, int positiveClassIndex, DistanceMeasure dm) {
        this.c = c;
        this.positiveClassIndex = positiveClassIndex;
    }

    /**
     * The set of unbound samples. This set contains the indices of the data
     * samples.
     */
    private HashSet<Integer> notBound = null;

    /**
     * The error cache
     * 
     */
    private double[]errorCache=null;
    /**
     * The Lagrange multipliers.
     */
    private double[] alpha = null;

    /**
     * Building the classification model from a dataset. The pseudocode and full
     * explanation to this technique can be founds in two papers:
     * <p>
     * (i) "Sequential Minimal Optimization: A fast algorithm for training
     * support vector machines", John C. Platt, 1998.
     * <p>
     * (ii) "Fast training of support vector machines using Sequential Minimal
     * Optimization", John C. Platt, 1998
     * <p>
     * Samples in the dataset that do not contain a label will be considered
     * negative samples.
     */
    public void buildClassifier(Dataset data) {
        // a local reference to the dataset
        this.data = data;
        this.notBound = new HashSet<Integer>();
        // number of samples that have been changed during the last iteration.
        int numChanged = 0;
        // indicates whether all samples in the dataset should be checked or not
        boolean examineAll = true;

        // Initialize the expected class values, this is the a representation of
        // the class labels of the dataset. The classValue is -1 for negative
        // samples and +1 for positive samples. Samples that do not contain a
        // class label will be considered negative.
        classValues = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            boolean isClassSet = data.getInstance(i).isClassSet();
            int classValue = data.getInstance(i).getClassValue();
            if (isClassSet && classValue == this.positiveClassIndex) {
                classValues[i] = 1;
            } else {
                classValues[i] = -1;
            }

        }// TODO put above procedure in seperate method

        // Initialize the vector with Lagrange multipliers.
        alpha = new double[data.size()];
        errorCache=new double[data.size()];

        // This loop will end when we have no changes recorded after a loop that
        // examined all samples.
        while (numChanged > 0 || examineAll) {
            numChanged = 0;
            if (examineAll) {
                for (int i = 0; i < data.size(); i++) {
                    if (examineExample(i)) {
                        numChanged++;
                    }
                }

            } else {
                for (Integer i : notBound) {
                    if (examineExample(i)) {
                        numChanged++;
                    }

                }

            }
            if (examineAll) {
                examineAll = false;
            } else if (numChanged == 0) {
                examineAll = true;
            }

        }

    }

    // TODO use System.currentTimeMillis for true randomness
    private static final Random rg = new Random(1);

    /**
     * Examines instance.
     * 
     * @param i
     *            index of instance to examine
     * @return true if examination was successfull
     */
    private boolean examineExample(int index1) {
        if (violatesKKT(index1)) {
            // heuristic for second choice
            int index2 = heuristicSecondChoice(index1);
            if (tryOptimization(index1, index2))
                return true;
            // loop not bound
            for (Integer i : notBound) {
                if (tryOptimization(index1, i)) {
                    return true;
                }
            }
            // loop all
            for (int i = rg.nextInt(data.size()); i < data.size(); i++, i %= data.size()) {
                if (tryOptimization(index1, i)) {
                    return true;
                }
            }

        }
        return false;

    }

    private int heuristicSecondChoice(int index1) {
        // TODO Auto-generated method stub
        return 0;
    }

    // this method should also update everything
    private boolean tryOptimization(int index1, int index2) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Check if the instance with the given index violates the KKT conditions.
     * 
     * @param i
     *            the index of the instance to be checked
     * @return true when the KKT is violated, false in other cases.
     */
    private boolean violatesKKT(int i) {
        double error;
        if(notBound.contains(i)){
            error=errorCache[i];
        }else{
            error=svmOutput(i) - classValues[i];
        }
        double rad = (error) * classValues[i];
        return (rad < -TOL && alpha[i] < c) || (rad > TOL && alpha[i] > 0);
    }

    /**
     * The set of indices for the support vectors. All the samples that have
     * their index in this set are support vectors.
     */
    private HashSet<Integer> supportVectors;

    /**
     * The threshold for the SVM.
     */
    private double threshold = 0;

    /**
     * Calculates the output of the SVM for the sample with the given index.
     * 
     * @param index
     *            the index of the sample
     * @return the output of the SVM
     */
    private double svmOutput(int index) {
        double result = 0;
        for (Integer i : supportVectors) {
            result += classValues[i] * alpha[i]
                    * kernel.calculateDistance(data.getInstance(index), data.getInstance(i));
        }

        result -= threshold;

        return result;
    }

    /**
     * The transformed class values. One for positives samples, -1 for all
     * negative samples.
     */
    private float[] classValues;

    /**
     * Classify an instance according to this Support Vector Machine. This
     * method will return 1 for positive classified instances and 0 for negative
     * ones.
     */
    public int classifyInstance(Instance instance) {
        // TODO implement
        return 0;
    }

    public double[] distributionForInstance(Instance instance) {
        // TODO implement
        return null;
    }

}
