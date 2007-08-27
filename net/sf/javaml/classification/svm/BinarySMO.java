/**
 * BinarySMO.java
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
 * Copyright (c) 1999 University of Waikato, Hamilton, New Zealand
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.classification.svm;

import java.util.HashSet;
import java.util.Set;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.Verbose;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.RBFKernel;

public class BinarySMO extends Verbose implements Classifier {

    private static final long serialVersionUID = -3186757526558912403L;

    
    public BinarySMO(){
        this(1.0);
    }
    
    public BinarySMO(double C){
        this.m_C=C;
    }
    
    /**
     * The complexity constant C. (default 1)
     */
    private double m_C = 1.0;

    /**
     * The epsilon for round-off error. (default 1.0e-12)
     */
    private double m_eps = 1.0e-12;

    /**
     * Precision constant for updating sets
     */
    private double m_Del = 1000 * Double.MIN_VALUE;

    /**
     * The tolerance parameter. (default 1.0e-3)
     */
    private double m_tol = 1.0e-3;

    /**
     * Kernel to use
     */
    private DistanceMeasure m_kernel = new RBFKernel();;

    /** The Lagrange multipliers. */
    private double[] m_alpha;

    /** The thresholds. */
    private double m_b, m_bLow, m_bUp;

    /** The indices for m_bLow and m_bUp */
    private int m_iLow, m_iUp;

    /** The training data. */
    private Dataset m_data;

    /** The transformed class values. */
    private double[] m_class;

    /** The current set of errors for all non-bound examples. */
    private double[] m_errors;

    /* The five different sets used by the algorithm. */
    /** {i: 0 < m_alpha[i] < C} */
    private Set<Integer> m_I0;

    /** {i: m_class[i] = 1, m_alpha[i] = 0} */
    private Set<Integer> m_I1;

    /** {i: m_class[i] = -1, m_alpha[i] =C} */
    private Set<Integer> m_I2;

    /** {i: m_class[i] = 1, m_alpha[i] = C} */
    private Set<Integer> m_I3;

    /** {i: m_class[i] = -1, m_alpha[i] = 0} */
    private Set<Integer> m_I4;

    /** The set of support vectors */
    private Set<Integer> m_supportVectors; // {i: 0 < m_alpha[i]}

    /** Stores the weight of the training instances */
    private double m_sumOfWeights = 0;

    /**
     * Method for building the binary classifier.
     * 
     * @param insts
     *            the set of training instances
     */
    public void buildClassifier(Dataset insts) {

        // Initialize some variables
        m_bUp = -1;
        m_bLow = 1;
        m_b = 0;
        m_alpha = null;
        m_data = null;
        m_errors = null;

        m_I0 = null;
        m_I1 = null;
        m_I2 = null;
        m_I3 = null;
        m_I4 = null;

        // Store the sum of weights
        verbose("Storing the sum of weights...");
        m_sumOfWeights = 0;
        for (int i = 0; i < insts.size(); i++) {
            m_sumOfWeights += insts.getInstance(i).getWeight();
        }
        // /m_sumOfWeights = insts.sumOfWeights();

        verbose("Setting class values...");
        m_class = new double[insts.size()];
        m_iUp = -1;
        m_iLow = -1;
        for (int i = 0; i < m_class.length; i++) {
            if ((int) insts.getInstance(i).getClassValue() == 0) {
                m_class[i] = -1;
                m_iLow = i;
            } else if ((int) insts.getInstance(i).getClassValue() == 1) {
                m_class[i] = 1;
                m_iUp = i;
            } else {
                throw new RuntimeException(
                        "This should not happen! A binary SMO can only take 0 and 1 as class values.");
            }
        }

        verbose("Checking for missing classes...");
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

            m_alpha = new double[0];
            m_class = new double[0];

            // Fit sigmoid if requested
            // if (fitLogistic) {
            // fitLogistic(insts, cl1, cl2, numFolds, new Random(randomSeed));
            // }
            return;
        }

        // Set the reference to the data
        m_data = insts;

        // Initialize alpha array to zero
        m_alpha = new double[m_data.size()];

        // Initialize sets
        m_supportVectors = new HashSet<Integer>();
        m_I0 = new HashSet<Integer>();
        m_I1 = new HashSet<Integer>();
        m_I2 = new HashSet<Integer>();
        m_I3 = new HashSet<Integer>();
        m_I4 = new HashSet<Integer>();

        // Initialize error cache
        m_errors = new double[m_data.size()];
        m_errors[m_iLow] = 1;
        m_errors[m_iUp] = -1;

        // Build up I1 and I4
        verbose("Building I1 and I4...");
        for (int i = 0; i < m_class.length; i++) {
            if (m_class[i] == 1) {
                m_I1.add(i);
            } else {
                m_I4.add(i);
            }
        }

        // Loop to find all the support vectors
        verbose("Search support vectors...");
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

                boolean innerLoopSuccess = true;
                numChanged = 0;
                while ((m_bUp < m_bLow - 2 * m_tol) && (innerLoopSuccess == true)) {
                    innerLoopSuccess = takeStep(m_iUp, m_iLow, m_errors[m_iLow]);
                }

            }

            if (examineAll) {
                examineAll = false;
            } else if (numChanged == 0) {
                examineAll = true;
            }
        }

        // Set threshold
        m_b = (m_bLow + m_bUp) / 2.0;

        m_errors = null;
        m_I0 = m_I1 = m_I2 = m_I3 = m_I4 = null;

        // // Fit sigmoid if requested
        // if (fitLogistic) {
        // fitLogistic(insts, cl1, cl2, numFolds, new Random(randomSeed));
        // }

    }

    /**
     * Computes SVM output for given instance.
     * 
     * @param inst
     *            the instance
     * @return the output of the SVM for the given instance
     */
    private double SVMOutput(Instance inst) {

        double result = 0;

        for (Integer i : m_supportVectors) {
            result += m_class[i] * m_alpha[i] * m_kernel.calculateDistance(m_data.getInstance(i), inst);
        }
        result -= m_b;

        return result;
    }

    /**
     * Examines instance.
     * 
     * @param i2
     *            index of instance to examine
     * @return true if examination was successfull
     */
    private boolean examineExample(int i2) {

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
            throw new RuntimeException("This should never happen!");
        }
        return takeStep(i1, i2, F2);
    }

    /**
     * Method solving for the Lagrange multipliers for two instances.
     * 
     * @param i1
     *            index of the first instance
     * @param i2
     *            index of the second instance
     * @param F2
     * @return true if multipliers could be found
     */
    private boolean takeStep(int i1, int i2, double F2) {

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
        k12 = m_kernel.calculateDistance(m_data.getInstance(i2), m_data.getInstance(i1));
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

        // Update error cache using new Lagrange multipliers
        for (Integer j : m_I0) {
            if ((j != i1) && (j != i2)) {
                m_errors[j] += y1 * (a1 - alph1)
                        * m_kernel.calculateDistance(m_data.getInstance(i1), m_data.getInstance(j)) + y2 * (a2 - alph2)
                        * m_kernel.calculateDistance(m_data.getInstance(j), m_data.getInstance(i2));
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
            throw new RuntimeException("This should never happen!");
        }

        // Made some progress.
        return true;
    }

    public int classifyInstance(Instance instance) {
        double output = SVMOutput(instance);
        if (output < 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public double[] distributionForInstance(Instance instance) {
        double[] out = new double[2];
        out[classifyInstance(instance)]++;
        return out;
    }

}
