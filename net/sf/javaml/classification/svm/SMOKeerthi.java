/**
 * SMOKeerthi.java, 17-jan-07
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
 */
package net.sf.javaml.classification.svm;

import java.util.HashSet;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.MathUtils;
import net.sf.javaml.distance.DistanceMeasure;

public class SMOKeerthi {

    /** The Lagrange multipliers. */
    protected double[] m_alpha;

    /** The thresholds. */
    protected double m_b, m_bLow, m_bUp;

    /** The indices for m_bLow and m_bUp */
    protected int m_iLow, m_iUp;

    /** The training data. */
    protected Dataset m_data;

    /** Weight vector for linear machine. */
    protected double[] m_weights;

    /**
     * Variables to hold weight vector in sparse form. (To reduce storage
     * requirements.)
     */
    protected double[] m_sparseWeights;

    protected int[] m_sparseIndices;

    /** Kernel to use * */
    protected DistanceMeasure m_kernel;

    /** The transformed class values. */
    protected double[] m_class;

    /** The current set of errors for all non-bound examples. */
    protected double[] m_errors;

    /* The five different sets used by the algorithm. */
    /** {i: 0 < m_alpha[i] < C} */
    protected HashSet<Integer> m_I0;

    /** {i: m_class[i] = 1, m_alpha[i] = 0} */
    protected HashSet<Integer> m_I1;

    /** {i: m_class[i] = -1, m_alpha[i] =C} */
    protected HashSet<Integer> m_I2;

    /** {i: m_class[i] = 1, m_alpha[i] = C} */
    protected HashSet<Integer> m_I3;

    /** {i: m_class[i] = -1, m_alpha[i] = 0} */
    protected HashSet<Integer> m_I4;

    /** The set of support vectors */
    protected HashSet<Integer> m_supportVectors; // {i: 0 < m_alpha[i]}

    // /** Stores logistic regression model for probability estimate */
    // protected Logistic m_logistic = null;

    /** Stores the weight of the training instances */
    protected double m_sumOfWeights = 0;

    /** Epsilon for rounding. */
    protected double m_eps = 1.0e-12;

    // /**
    // * Fits logistic regression model to SVM outputs analogue to John Platt's
    // * method.
    // *
    // * @param insts
    // * the set of training instances
    // * @param cl1
    // * the first class' index
    // * @param cl2
    // * the second class' index
    // * @param numFolds
    // * the number of folds for cross-validation
    // * @param random
    // * for randomizing the data
    // * @throws Exception
    // * if the sigmoid can't be fit successfully
    // */
    // protected void fitLogistic(Dataset insts, int cl1, int cl2, int numFolds,
    // Random random) throws Exception {
    //
    // // Create header of instances object
    // FastVector atts = new FastVector(2);
    // atts.addElement(new Attribute("pred"));
    // FastVector attVals = new FastVector(2);
    // attVals.addElement(insts.classAttribute().value(cl1));
    // attVals.addElement(insts.classAttribute().value(cl2));
    // atts.addElement(new Attribute("class", attVals));
    // Instances data = new Instances("data", atts, insts.numInstances());
    // data.setClassIndex(1);
    //
    // // Collect data for fitting the logistic model
    // if (numFolds <= 0) {
    //
    // // Use training data
    // for (int j = 0; j < insts.numInstances(); j++) {
    // Instance inst = insts.instance(j);
    // double[] vals = new double[2];
    // vals[0] = SVMOutput(-1, inst);
    // if (inst.classValue() == cl2) {
    // vals[1] = 1;
    // }
    // data.add(new Instance(inst.weight(), vals));
    // }
    // } else {
    //
    // // Check whether number of folds too large
    // if (numFolds > insts.numInstances()) {
    // numFolds = insts.numInstances();
    // }
    //
    // // Make copy of instances because we will shuffle them around
    // insts = new Instances(insts);
    //
    // // Perform three-fold cross-validation to collect
    // // unbiased predictions
    // insts.randomize(random);
    // insts.stratify(numFolds);
    // for (int i = 0; i < numFolds; i++) {
    // Instances train = insts.trainCV(numFolds, i, random);
    // SerializedObject so = new SerializedObject(this);
    // BinarySMO smo = (BinarySMO) so.getObject();
    // smo.buildClassifier(train, cl1, cl2, false, -1, -1);
    // Instances test = insts.testCV(numFolds, i);
    // for (int j = 0; j < test.numInstances(); j++) {
    // double[] vals = new double[2];
    // vals[0] = smo.SVMOutput(-1, test.instance(j));
    // if (test.instance(j).classValue() == cl2) {
    // vals[1] = 1;
    // }
    // data.add(new Instance(test.instance(j).weight(), vals));
    // }
    // }
    // }
    //
    // // Build logistic regression model
    // m_logistic = new Logistic();
    // m_logistic.buildClassifier(data);
    // }

    private double m_tol = 0.001;

    /**
     * Method for building a SVM from a dataset.
     * 
     * @param data
     *            the dataset
     * @param cl1
     *            the classValue for the positive samples.
     * @param kernel
     *            the distance measure used to calculate the similarity of data
     *            samples
     */
    public void buildClassifier(Dataset data, int cl1, DistanceMeasure kernel) {
        this.m_data = data;
        // Initialize some variables
        m_bUp = -1;
        m_bLow = 1;
        m_b = 0;
        m_alpha = null;
        m_data = null;
        m_weights = null;
        m_errors = null;
        // m_logistic = null;
        m_I0 = null;
        m_I1 = null;
        m_I2 = null;
        m_I3 = null;
        m_I4 = null;
        m_sparseWeights = null;
        m_sparseIndices = null;

        // Store the sum of weights
        // m_sumOfWeights = insts.sumOfWeights();

        // Set class values
        m_class = new double[this.m_data.size()];
        m_iUp = -1;
        m_iLow = -1;
        for (int i = 0; i < m_class.length; i++) {
            Instance tmp = m_data.getInstance(i);
            if (tmp.isClassSet() && tmp.getClassValue() == cl1) {
                m_class[i] = 1;
                m_iUp = i;

            } else {
                m_class[i] = -1;
                m_iLow = i;
            }
        }

        // Check whether one or both classes are missing
        if ((m_iUp == -1) || (m_iLow == -1)) {
            if (m_iUp != -1) {
                m_b = -1;
            } else if (m_iLow != -1) {
                m_b = 1;
            } else {
                m_class = null;
                return;
            }

            m_supportVectors = new HashSet<Integer>();
            m_alpha = new double[0];
            m_class = new double[0];

            // // Fit sigmoid if requested
            // if (fitLogistic) {
            // fitLogistic(insts, cl1, cl2, numFolds, new Random(randomSeed));
            // }
            // return;
        }

        // Set the reference to the data

        // Initialize alpha array to zero
        m_alpha = new double[m_data.size()];

        // Initialize sets
        m_supportVectors = new HashSet<Integer>();
        m_I0 = new HashSet<Integer>();
        m_I1 = new HashSet<Integer>();
        m_I2 = new HashSet<Integer>();
        m_I3 = new HashSet<Integer>();
        m_I4 = new HashSet<Integer>();

        // Clean out some instance variables
        m_sparseWeights = null;
        m_sparseIndices = null;

        // init kernel
        this.m_kernel = kernel;

        // Initialize error cache
        m_errors = new double[m_data.size()];
        m_errors[m_iLow] = 1;
        m_errors[m_iUp] = -1;

        // Build up I1 and I4
        for (int i = 0; i < m_class.length; i++) {
            if (m_class[i] == 1) {
                m_I1.add(i);
            } else {
                m_I4.add(i);
            }
        }

        // Loop to find all the support vectors
        int numChanged = 0;
        boolean examineAll = true;
        while ((numChanged > 0) || examineAll) {
            numChanged = 0;
            if (examineAll) {
                for (int i = 0; i < m_alpha.length; i++) {
                    if (examineExample(i)) {
                        numChanged++;
                    }
                }
            } else {

                // This code implements Modification 1 from Keerthi et al.'s
                // paper
                for (int i = 0; i < m_alpha.length; i++) {
                    if ((m_alpha[i] > 0) && (m_alpha[i] < m_C * m_data.getInstance(i).getWeight())) {
                        if (examineExample(i)) {
                            numChanged++;
                        }

                        // Is optimality on unbound vectors obtained?
                        if (m_bUp > m_bLow - 2 * m_tol) {
                            numChanged = 0;
                            break;
                        }
                    }
                }

                // This is the code for Modification 2 from Keerthi et al.'s
                // paper
                /*
                 * boolean innerLoopSuccess = true; numChanged = 0; while
                 * ((m_bUp < m_bLow - 2 * m_tol) && (innerLoopSuccess == true)) {
                 * innerLoopSuccess = takeStep(m_iUp, m_iLow, m_errors[m_iLow]); }
                 */
            }

            if (examineAll) {
                examineAll = false;
            } else if (numChanged == 0) {
                examineAll = true;
            }
        }

        // Set threshold
        m_b = (m_bLow + m_bUp) / 2.0;

        // Save memory
        // m_kernel.clean();

        m_errors = null;
        m_I0 = m_I1 = m_I2 = m_I3 = m_I4 = null;

        // If machine is linear, delete training data
        // and store weight vector in sparse format
        // if (m_KernelIsLinear) {
        //
        // // We don't need to store the set of support vectors
        // m_supportVectors = null;
        //
        // // We don't need to store the class values either
        // m_class = null;
        //
        // // Clean out training data
        // if (!m_checksTurnedOff) {
        // m_data = new Instances(m_data, 0);
        // } else {
        // m_data = null;
        // }
        //
        // // Convert weight vector
        // double[] sparseWeights = new double[m_weights.length];
        // int[] sparseIndices = new int[m_weights.length];
        // int counter = 0;
        // for (int i = 0; i < m_weights.length; i++) {
        // if (m_weights[i] != 0.0) {
        // sparseWeights[counter] = m_weights[i];
        // sparseIndices[counter] = i;
        // counter++;
        // }
        // }
        // m_sparseWeights = new double[counter];
        // m_sparseIndices = new int[counter];
        // System.arraycopy(sparseWeights, 0, m_sparseWeights, 0, counter);
        // System.arraycopy(sparseIndices, 0, m_sparseIndices, 0, counter);
        //
        // // Clean out weight vector
        // m_weights = null;
        //
        // // We don't need the alphas in the linear case
        // m_alpha = null;
        // }

//        // Fit sigmoid if requested
//        if (fitLogistic) {
//            fitLogistic(insts, cl1, cl2, numFolds, new Random(randomSeed));
//        }

    }

    /**
     * Computes SVM output for given instance.
     * 
     * @param index
     *            the instance for which output is to be computed
     * @param inst
     *            the instance
     * @return the output of the SVM for the given instance
     * @throws Exception
     *             in case of an error
     */
    public double SVMOutput(Instance inst)  {

        double result = 0;

        // Is the machine linear?
        for (Integer i : m_supportVectors) {
            result += m_class[i] * m_alpha[i] * m_kernel.calculateDistance(m_data.getInstance(i), inst);
        }

        result -= m_b;

        return result;
    }

    /**
     * Prints out the classifier.
     * 
     * @return a description of the classifier as a string
     */
    public String toString() {

        StringBuffer text = new StringBuffer();
        int printed = 0;

        if ((m_alpha == null) && (m_sparseWeights == null)) {
            return "BinarySMO: No model built yet.\n";
        }
        try {
            text.append("BinarySMO\n\n");

            for (int i = 0; i < m_alpha.length; i++) {
                if (m_supportVectors.contains(i)) {
                    double val = m_alpha[i];
                    if (m_class[i] == 1) {
                        if (printed > 0) {
                            text.append(" + ");
                        }
                    } else {
                        text.append(" - ");
                    }
                    text.append(val + " * <");
                    Instance inst = m_data.getInstance(i);
                    for (int j = 0; j < inst.size(); j++) {
                        text.append(inst.toString());

                    }
                    text.append("> * X]\n");
                    printed++;
                }
            }

            if (m_b > 0) {
                text.append(" - " + m_b);
            } else {
                text.append(" + " + (-m_b));
            }

            text.append("\n\nNumber of support vectors: " + m_supportVectors.size());
            // Kernels do not support evaluation of number of evaluations
            // int numEval = 0;
            // int numCacheHits = -1;
            // if (m_kernel != null) {
            // numEval = m_kernel.numEvals();
            // numCacheHits = m_kernel.numCacheHits();
            // }
            // text.append("\n\nNumber of kernel evaluations: " + numEval);
            // if (numCacheHits >= 0 && numEval > 0) {
            // double hitRatio = 1 - numEval * 1.0 / (numCacheHits + numEval);
            // text.append(" (" + Utils.doubleToString(hitRatio * 100, 7,
            // 3).trim() + "%
            // cached)");
            // }

        } catch (Exception e) {
            e.printStackTrace();

            return "Can't print BinarySMO classifier.";
        }

        return text.toString();
    }

    /**
     * Examines instance.
     * 
     * @param i2
     *            index of instance to examine
     * @return true if examination was successfull
     * @throws Exception
     *             if something goes wrong
     */
    protected boolean examineExample(int i2) {

        double y2, F2;
        int i1 = -1;

        y2 = m_class[i2];
        if (m_I0.contains(i2)) {
            F2 = m_errors[i2];
        } else {
            F2 = SVMOutput(m_data.getInstance(i2)) + m_b - y2;
            m_errors[i2] = F2;

            // Update thresholds
            if ((m_I1.contains(i2) || m_I2.contains(i2)) && (F2 < m_bUp)) {
                m_bUp = F2;
                m_iUp = i2;
            } else if ((m_I3.contains(i2) || m_I4.contains(i2)) && (F2 > m_bLow)) {
                m_bLow = F2;
                m_iLow = i2;
            }
        }

        // Check optimality using current bLow and bUp and, if
        // violated, find an index i1 to do joint optimization
        // with i2...
        boolean optimal = true;
        if (m_I0.contains(i2) || m_I1.contains(i2) || m_I2.contains(i2)) {
            if (m_bLow - F2 > 2 * m_tol) {
                optimal = false;
                i1 = m_iLow;
            }
        }
        if (m_I0.contains(i2) || m_I3.contains(i2) || m_I4.contains(i2)) {
            if (F2 - m_bUp > 2 * m_tol) {
                optimal = false;
                i1 = m_iUp;
            }
        }
        if (optimal) {
            return false;
        }

        // For i2 unbound choose the better i1...
        if (m_I0.contains(i2)) {
            if (m_bLow - F2 > F2 - m_bUp) {
                i1 = m_iLow;
            } else {
                i1 = m_iUp;
            }
        }
        if (i1 == -1) {
            throw new RuntimeException("<SMOKeerthi> This should never happen!");
        }
        return takeStep(i1, i2, F2);
    }

    private double m_C;

    /** Precision constant for updating sets */
    protected static double m_Del = 1000 * Double.MIN_VALUE;

    /**
     * Method solving for the Lagrange multipliers for two instances.
     * 
     * @param i1
     *            index of the first instance
     * @param i2
     *            index of the second instance
     * @param F2
     * @return true if multipliers could be found
     * @throws Exception
     *             if something goes wrong
     */
    protected boolean takeStep(int i1, int i2, double F2) {

        double alph1, alph2, y1, y2, F1, s, L, H, k11, k12, k22, eta, a1, a2, f1, f2, v1, v2, Lobj, Hobj;
        double C1 = m_C * m_data.getInstance(i1).getWeight();
        double C2 = m_C * m_data.getInstance(i2).getWeight();

        // Don't do anything if the two instances are the same
        if (i1 == i2) {
            return false;
        }

        // Initialize variables
        alph1 = m_alpha[i1];
        alph2 = m_alpha[i2];
        y1 = m_class[i1];
        y2 = m_class[i2];
        F1 = m_errors[i1];
        s = y1 * y2;

        // Find the constraints on a2
        if (y1 != y2) {
            L = Math.max(0, alph2 - alph1);
            H = Math.min(C2, C1 + alph2 - alph1);
        } else {
            L = Math.max(0, alph1 + alph2 - C1);
            H = Math.min(C2, alph1 + alph2);
        }
        if (L >= H) {
            return false;
        }

        // Compute second derivative of objective function
        k11 = m_kernel.calculateDistance(m_data.getInstance(i1), m_data.getInstance(i1));
        k12 = m_kernel.calculateDistance(m_data.getInstance(i1), m_data.getInstance(i2));
        k22 = m_kernel.calculateDistance(m_data.getInstance(i2), m_data.getInstance(i2));
        eta = 2 * k12 - k11 - k22;

        // Check if second derivative is negative
        if (eta < 0) {

            // Compute unconstrained maximum
            a2 = alph2 - y2 * (F1 - F2) / eta;

            // Compute constrained maximum
            if (a2 < L) {
                a2 = L;
            } else if (a2 > H) {
                a2 = H;
            }
        } else {

            // Look at endpoints of diagonal
            f1 = SVMOutput(m_data.getInstance(i1));
            f2 = SVMOutput(m_data.getInstance(i2));
            v1 = f1 + m_b - y1 * alph1 * k11 - y2 * alph2 * k12;
            v2 = f2 + m_b - y1 * alph1 * k12 - y2 * alph2 * k22;
            double gamma = alph1 + s * alph2;
            Lobj = (gamma - s * L) + L - 0.5 * k11 * (gamma - s * L) * (gamma - s * L) - 0.5 * k22 * L * L - s * k12
                    * (gamma - s * L) * L - y1 * (gamma - s * L) * v1 - y2 * L * v2;
            Hobj = (gamma - s * H) + H - 0.5 * k11 * (gamma - s * H) * (gamma - s * H) - 0.5 * k22 * H * H - s * k12
                    * (gamma - s * H) * H - y1 * (gamma - s * H) * v1 - y2 * H * v2;
            if (Lobj > Hobj + m_eps) {
                a2 = L;
            } else if (Lobj < Hobj - m_eps) {
                a2 = H;
            } else {
                a2 = alph2;
            }
        }
        if (Math.abs(a2 - alph2) < m_eps * (a2 + alph2 + m_eps)) {
            return false;
        }

        // To prevent precision problems
        if (a2 > C2 - m_Del * C2) {
            a2 = C2;
        } else if (a2 <= m_Del * C2) {
            a2 = 0;
        }

        // Recompute a1
        a1 = alph1 + s * (alph2 - a2);

        // To prevent precision problems
        if (a1 > C1 - m_Del * C1) {
            a1 = C1;
        } else if (a1 <= m_Del * C1) {
            a1 = 0;
        }

        // Update sets
        if (a1 > 0) {
            m_supportVectors.add(i1);
        } else {
            m_supportVectors.remove(i1);
        }
        if ((a1 > 0) && (a1 < C1)) {
            m_I0.add(i1);
        } else {
            m_I0.remove(i1);
        }
        if ((y1 == 1) && (a1 == 0)) {
            m_I1.add(i1);
        } else {
            m_I1.remove(i1);
        }
        if ((y1 == -1) && (a1 == C1)) {
            m_I2.add(i1);
        } else {
            m_I2.remove(i1);
        }
        if ((y1 == 1) && (a1 == C1)) {
            m_I3.add(i1);
        } else {
            m_I3.remove(i1);
        }
        if ((y1 == -1) && (a1 == 0)) {
            m_I4.add(i1);
        } else {
            m_I4.remove(i1);
        }
        if (a2 > 0) {
            m_supportVectors.add(i2);
        } else {
            m_supportVectors.remove(i2);
        }
        if ((a2 > 0) && (a2 < C2)) {
            m_I0.add(i2);
        } else {
            m_I0.remove(i2);
        }
        if ((y2 == 1) && (a2 == 0)) {
            m_I1.add(i2);
        } else {
            m_I1.remove(i2);
        }
        if ((y2 == -1) && (a2 == C2)) {
            m_I2.add(i2);
        } else {
            m_I2.remove(i2);
        }
        if ((y2 == 1) && (a2 == C2)) {
            m_I3.add(i2);
        } else {
            m_I3.remove(i2);
        }
        if ((y2 == -1) && (a2 == 0)) {
            m_I4.add(i2);
        } else {
            m_I4.remove(i2);
        }

        // // Update weight vector to reflect change a1 and a2, if linear SVM
        // if (m_KernelIsLinear) {
        // Instance inst1 = m_data.instance(i1);
        // for (int p1 = 0; p1 < inst1.numValues(); p1++) {
        // if (inst1.index(p1) != m_data.classIndex()) {
        // m_weights[inst1.index(p1)] += y1 * (a1 - alph1) *
        // inst1.valueSparse(p1);
        // }
        // }
        // Instance inst2 = m_data.instance(i2);
        // for (int p2 = 0; p2 < inst2.numValues(); p2++) {
        // if (inst2.index(p2) != m_data.classIndex()) {
        // m_weights[inst2.index(p2)] += y2 * (a2 - alph2) *
        // inst2.valueSparse(p2);
        // }
        // }
        // }

        // Update error cache using new Lagrange multipliers
        for (Integer j : m_I0) {
            if ((j != i1) && (j != i2)) {
                m_errors[j] += y1 * (a1 - alph1)
                        * m_kernel.calculateDistance(m_data.getInstance(i1), m_data.getInstance(j)) + y2 * (a2 - alph2)
                        * m_kernel.calculateDistance(m_data.getInstance(i2), m_data.getInstance(j));
            }
        }

        // Update error cache for i1 and i2
        m_errors[i1] += y1 * (a1 - alph1) * k11 + y2 * (a2 - alph2) * k12;
        m_errors[i2] += y1 * (a1 - alph1) * k12 + y2 * (a2 - alph2) * k22;

        // Update array with Lagrange multipliers
        m_alpha[i1] = a1;
        m_alpha[i2] = a2;

        // Update thresholds
        m_bLow = -Double.MAX_VALUE;
        m_bUp = Double.MAX_VALUE;
        m_iLow = -1;
        m_iUp = -1;
        for (Integer j : m_I0) {
            if (m_errors[j] < m_bUp) {
                m_bUp = m_errors[j];
                m_iUp = j;
            }
            if (m_errors[j] > m_bLow) {
                m_bLow = m_errors[j];
                m_iLow = j;
            }
        }
        if (!m_I0.contains(i1)) {
            if (m_I3.contains(i1) || m_I4.contains(i1)) {
                if (m_errors[i1] > m_bLow) {
                    m_bLow = m_errors[i1];
                    m_iLow = i1;
                }
            } else {
                if (m_errors[i1] < m_bUp) {
                    m_bUp = m_errors[i1];
                    m_iUp = i1;
                }
            }
        }
        if (!m_I0.contains(i2)) {
            if (m_I3.contains(i2) || m_I4.contains(i2)) {
                if (m_errors[i2] > m_bLow) {
                    m_bLow = m_errors[i2];
                    m_iLow = i2;
                }
            } else {
                if (m_errors[i2] < m_bUp) {
                    m_bUp = m_errors[i2];
                    m_iUp = i2;
                }
            }
        }
        if ((m_iLow == -1) || (m_iUp == -1)) {
            throw new RuntimeException("<SMOKeerthi> This should never happen!");
        }

        // Made some progress.
        return true;
    }

    /**
     * Quick and dirty check whether the quadratic programming problem is
     * solved.
     * 
     * @throws Exception
     *             if checking fails
     */
    protected void checkClassifier() throws Exception {

        double sum = 0;
        for (int i = 0; i < m_alpha.length; i++) {
            if (m_alpha[i] > 0) {
                sum += m_class[i] * m_alpha[i];
            }
        }
        System.err.println("Sum of y(i) * alpha(i): " + sum);

        for (int i = 0; i < m_alpha.length; i++) {
            double output = SVMOutput(m_data.getInstance(i));
            if (MathUtils.eq(m_alpha[i], 0)) {
                if (MathUtils.lt(m_class[i] * output, 1)) {
                    System.err.println("KKT condition 1 violated: " + m_class[i] * output);
                }
            }
            if (MathUtils.gt(m_alpha[i], 0) && MathUtils.lt(m_alpha[i], m_C * m_data.getInstance(i).getWeight())) {
                if (!MathUtils.eq(m_class[i] * output, 1)) {
                    System.err.println("KKT condition 2 violated: " + m_class[i] * output);
                }
            }
            if (MathUtils.eq(m_alpha[i], m_C * m_data.getInstance(i).getWeight())) {
                if (MathUtils.gt(m_class[i] * output, 1)) {
                    System.err.println("KKT condition 3 violated: " + m_class[i] * output);
                }
            }
        }
    }
}
