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
import net.sf.javaml.core.MathUtils;
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
    private double c = 1;

    /**
     * The tolerance for checking the KKT conditions
     */
    private static final double TOL = 1.0e-3;

    /**
     * The epsilon value to correct for rounding errors
     */
    private static final double EPSILON = 1.0e-12;

    /**
     * The class value that indicates positive samples.
     */
    private int positiveClassValue = 0;

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
     * @param positiveClassValue
     *            the index of the positive class, all other indices will be
     *            considered negative samples.
     */
    public SMOPlatt(double c, int positiveClassValue, DistanceMeasure dm) {
        this.c = c;
        this.positiveClassValue = positiveClassValue;
        this.kernel = dm;
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
    private double[] errorCache = null;

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
            if (isClassSet && classValue == this.positiveClassValue) {
                classValues[i] = 1;
            } else {
                classValues[i] = -1;
            }

        }// TODO put above procedure in seperate method

        // Initialize the vector with Lagrange multipliers.
        alpha = new double[data.size()];
        // Initialize the vector with the cached errors
        errorCache = new double[data.size()];
        // Initialize the supportVectors
        supportVectors = new HashSet<Integer>();
        // Initialize the not bound samples
        notBound = new HashSet<Integer>();
        // This loop will end when we have no changes recorded after a loop that
        // examined all samples.
        int count = 0;
        while (numChanged > 0 || examineAll) {
            count++;
            System.out.println("Iterations " + count + "\t: " + supportVectors.size());
            numChanged = 0;
            if (examineAll) {
                System.out.println("\t Checking all samples");
                for (int i = 0; i < data.size(); i++) {
                    // System.out.println("\tall-"+i+"\tchanged: "+numChanged);
                    if (examineExample(i)) {
                        numChanged++;
                    }
                }

            } else {
                System.out.println("\t Checking not bound samples");
                for (Integer i : notBound) {
                    // System.out.println("\tnotBound-"+i);
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
        System.out.println("Total number of iterations: " + count);

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
    private boolean examineExample(int index2) {
        double E2;
        if (notBound.contains(index2)) {
            // System.out.println("\t\tnotBound sample");
            E2 = errorCache[index2];
        } else {
            E2 = svmOutput(index2) - classValues[index2];

        }

        double rad = E2 * classValues[index2];
        // System.out.println("\t\tsvm="+svmOutput(index2)+"\tE2="+E2+"\trad="+rad);
        // check whether index2 violates the KKT conditions.
        if ((rad < -TOL && alpha[index2] < c) || (rad > TOL && alpha[index2] > 0)) {
            // System.out.println("\t\tviolates KKT");
            // heuristic for second choice
            int index1 = heuristicSecondChoice(index2);
            // System.out.println("\t\tsecond choice="+index1);
            if (tryOptimization(index1, index2, E2))
                return true;
            // System.out.println("\t\tsecond choice FAILED");
            // if the heuristic fails, loop not bound samples
            for (Integer i : notBound) {
                if (tryOptimization(index1, i, E2)) {
                    return true;
                }
            }
            // if all non bound samples fail, loop all
            for (int i = rg.nextInt(data.size()), count = 0; count < data.size(); i++, i %= data.size(), count++) {
                // System.out.println("\t\t\tTrying for second choice "+i);
                if (tryOptimization(i, index2, E2)) {
                    return true;
                }
            }

        }
        return false;

    }

    private int heuristicSecondChoice(int index1) {
        int returnIndex = index1;
        if (notBound.contains(index1)) {
            double maxDiff = 0;
            double E1 = errorCache[index1];
            for (Integer i : notBound) {
                double tmpDiff = Math.abs(E1 - errorCache[i]);
                if (tmpDiff > maxDiff) {
                    tmpDiff = maxDiff;
                    returnIndex = i;
                }
            }
        }
        return returnIndex;
    }

    // this method should also update everything
    private boolean tryOptimization(int index1, int index2, double E2) {
        if (index1 == index2)
            return false;
        double alph1 = alpha[index1];
        double alph2 = alpha[index2];
        double y1 = classValues[index1];
        double y2 = classValues[index2];
        double E1;
        if (notBound.contains(index1))
            E1 = errorCache[index1];
        else
            E1 = svmOutput(index1) - y1;
        double s = y1 * y2;
        // calculate L and H
        double L, H;
        if (MathUtils.eq(y1, y2)) {
            L = Math.max(0, alph2 + alph1 - c);
            H = Math.min(c, alph2 + alph1);
        } else {
            L = Math.max(0, alph2 - alph1);
            H = Math.min(c, c + alph2 - alph1);
        }
        double k11 = kernel.calculateDistance(data.getInstance(index1), data.getInstance(index2));
        double k12 = kernel.calculateDistance(data.getInstance(index1), data.getInstance(index2));
        double k22 = kernel.calculateDistance(data.getInstance(index2), data.getInstance(index2));
        double eta = k11 + k22 - 2 * k12;
        double a1, a2;
        if (eta > 0) {
            a2 = alph2 + y2 * (E1 - E2) / eta;
            if (a2 < L) {
                a2 = L;
            } else if (a2 > H) {
                a2 = H;
            }
        } else {
            // objective functions
            double f1 = svmOutput(index1);
            double f2 = svmOutput(index2);
            double v1 = f1 + threshold - y1 * alph1 * k11 - y2 * alph2 * k12;
            double v2 = f2 + threshold - y1 * alph1 * k12 - y2 * alph2 * k22;
            double gamma = alph1 + s * alph2;
            double Lobj = (gamma - s * L) + L - 0.5 * k11 * (gamma - s * L) * (gamma - s * L) - 0.5 * k22 * L * L - s
                    * k12 * (gamma - s * L) * L - y1 * (gamma - s * L) * v1 - y2 * L * v2;
            double Hobj = (gamma - s * H) + H - 0.5 * k11 * (gamma - s * H) * (gamma - s * H) - 0.5 * k22 * H * H - s
                    * k12 * (gamma - s * H) * H - y1 * (gamma - s * H) * v1 - y2 * H * v2;
            if (Lobj > Hobj + EPSILON) {
                a2 = L;
            } else if (Lobj < Hobj - EPSILON) {
                a2 = H;
            } else {
                a2 = alph2;
            }
        }
        if (Math.abs(a2 - alph2) < EPSILON * (a2 + alph2 + EPSILON)) {
            return false;
        }
        // TODO insert code for rounding error mistakes from SMO from weka
        a1 = alph1 + s * (alph2 - a2);

        // ****************
        // | update stuff |
        // ****************
        // update the support vectors
        if (a1 > 0) {
            supportVectors.add(index1);
        } else {
            supportVectors.remove(index1);
        }
        if (a2 > 0) {
            supportVectors.add(index2);
        } else {
            supportVectors.remove(index2);
        }
        // Update the not bound set
        if ((a1 > 0) && (a1 < c)) {
            notBound.add(index1);
        } else {
            notBound.remove(index1);
        }
        if ((a2 > 0) && (a2 < c)) {
            notBound.add(index2);
        } else {
            notBound.remove(index2);
        }
        // update threshold
        double b1, b2;
        b1 = E1 + y1 * (a1 - alph1) * k11 + y2 * (a2 - alph2) * k12 + threshold;
        b2 = E2 + y1 * (a1 - alph1) * k12 + y2 * (a2 - alph2) * k22 + threshold;
        threshold = (b1 + b2) / 2;
        // Update error cache using new Lagrange multipliers
        for (Integer j : notBound) {
            if ((j != index1) && (j != index2)) {
                errorCache[j] += y1 * (a1 - alph1)
                        * kernel.calculateDistance(data.getInstance(index1), data.getInstance(j)) + y2 * (a2 - alph2)
                        * kernel.calculateDistance(data.getInstance(index2), data.getInstance(j));
            }
        }

        // Update error cache for i1 and i2
        errorCache[index1] += y1 * (a1 - alph1) * k11 + y2 * (a2 - alph2) * k12;
        errorCache[index2] += y1 * (a1 - alph1) * k12 + y2 * (a2 - alph2) * k22;

        // Update array with Lagrange multipliers
        alpha[index1] = a1;
        alpha[index2] = a2;
        return true;
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
        double result = 0;
        for (Integer i : supportVectors) {
            result += classValues[i] * alpha[i] * kernel.calculateDistance(instance, data.getInstance(i));
        }

        if (result > 0)
            return 1;
        else
            return 0;
    }

    /**
     * Returns the distribution of classification. This is always a array with
     * size two first on index 0 the measure that the instance is false, and on
     * index 1 the measure the instance is true.
     */
    public double[] distributionForInstance(Instance instance) {
        double[] out = new double[2];
        out[classifyInstance(instance)]++;
        return out;
    }
}
