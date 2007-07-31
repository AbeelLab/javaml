/**
 * BinaryLinearSMO.java
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
import net.sf.javaml.distance.CachedDistance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.LinearKernel;

/**
 * <!-- globalinfo-start --> Implements John Platt's sequential minimal
 * optimization algorithm for training a support vector classifier.<br/> <br/>
 * This implementation globally replaces all missing values and transforms
 * nominal attributes into binary ones. It also normalizes all attributes by
 * default. (In that case the coefficients in the output are based on the
 * normalized data, not the original data --- this is important for interpreting
 * the classifier.)<br/> <br/> Multi-class problems are solved using pairwise
 * classification (1-vs-1 and if logistic models are built pairwise coupling
 * according to Hastie and Tibshirani, 1998).<br/> <br/> To obtain proper
 * probability estimates, use the option that fits logistic regression models to
 * the outputs of the support vector machine. In the multi-class case the
 * predicted probabilities are coupled using Hastie and Tibshirani's pairwise
 * coupling method.<br/> <br/> Note: for improved speed normalization should be
 * turned off when operating on SparseInstances.<br/> <br/> For more
 * information on the SMO algorithm, see<br/> <br/> J. Platt: Machines using
 * Sequential Minimal Optimization. In B. Schoelkopf and C. Burges and A. Smola,
 * editors, Advances in Kernel Methods - Support Vector Learning, 1998.<br/>
 * <br/> S.S. Keerthi, S.K. Shevade, C. Bhattacharyya, K.R.K. Murthy (2001).
 * Improvements to Platt's SMO Algorithm for SVM Classifier Design. Neural
 * Computation. 13(3):637-649.<br/> <br/> Trevor Hastie, Robert Tibshirani:
 * Classification by Pairwise Coupling. In: Advances in Neural Information
 * Processing Systems, 1998. <p/> <!-- globalinfo-end -->
 * 
 * <!-- technical-bibtex-start --> BibTeX:
 * 
 * <pre>
 * &#64;incollection{Platt1998,
 *    author = {J. Platt},
 *    booktitle = {Advances in Kernel Methods - Support Vector Learning},
 *    editor = {B. Schoelkopf and C. Burges and A. Smola},
 *    publisher = {MIT Press},
 *    title = {Machines using Sequential Minimal Optimization},
 *    year = {1998},
 *    URL = {http://research.microsoft.com/\~jplatt/smo.html},
 *    PS = {http://research.microsoft.com/\~jplatt/smo-book.ps.gz},
 *    PDF = {http://research.microsoft.com/\~jplatt/smo-book.pdf}
 * }
 * 
 * &#64;article{Keerthi2001,
 *    author = {S.S. Keerthi and S.K. Shevade and C. Bhattacharyya and K.R.K. Murthy},
 *    journal = {Neural Computation},
 *    number = {3},
 *    pages = {637-649},
 *    title = {Improvements to Platt's SMO Algorithm for SVM Classifier Design},
 *    volume = {13},
 *    year = {2001},
 *    PS = {http://guppy.mpe.nus.edu.sg/\~mpessk/svm/smo_mod_nc.ps.gz}
 * }
 * 
 * &#64;inproceedings{Hastie1998,
 *    author = {Trevor Hastie and Robert Tibshirani},
 *    booktitle = {Advances in Neural Information Processing Systems},
 *    editor = {Michael I. Jordan and Michael J. Kearns and Sara A. Solla},
 *    publisher = {MIT Press},
 *    title = {Classification by Pairwise Coupling},
 *    volume = {10},
 *    year = {1998},
 *    PS = {http://www-stat.stanford.edu/\~hastie/Papers/2class.ps}
 * }
 * </pre>
 * 
 * <p/> <!-- technical-bibtex-end -->
 * 
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author Shane Legg (shane@intelligenesis.net) (sparse vector code)
 * @author Stuart Inglis (stuart@reeltwo.com) (sparse vector code)
 * @author Thomas Abeel (modification for Java-ML) 
 */
public class BinaryLinearSMO extends Verbose implements Classifier {
    
    public BinaryLinearSMO(){
        this.m_C=1.0;
        this.m_kernel=new CachedDistance(new LinearKernel());
    }
    
    public BinaryLinearSMO(double C,DistanceMeasure dm){
        this.m_C=C;
        this.m_kernel=dm;
        
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
    private DistanceMeasure m_kernel =null;

    /** The Lagrange multipliers. */
    private double[] m_alpha;

    /** The thresholds. */
    private double m_b, m_bLow, m_bUp;

    /** The indices for m_bLow and m_bUp */
    private int m_iLow, m_iUp;

    /** The training data. */
    private Dataset m_data;

    /** Weight vector for linear machine. */
    private double[] m_weights;

    /**
     * Variables to hold weight vector in sparse form. (To reduce storage
     * requirements.)
     */
    private double[] m_sparseWeights;

    private int[] m_sparseIndices;

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

    // /** Stores logistic regression model for probability estimate */
    // private Logistic m_logistic = null;

    /** Stores the weight of the training instances */
    private double m_sumOfWeights = 0;

    // /**
    // * Fits logistic regression model to SVM outputs analogue
    // * to John Platt's method.
    // *
    // * @param insts the set of training instances
    // * @param cl1 the first class' index
    // * @param cl2 the second class' index
    // * @param numFolds the number of folds for cross-validation
    // * @param random for randomizing the data
    // * @throws Exception if the sigmoid can't be fit successfully
    // */
    // private void fitLogistic(Instances insts, int cl1, int cl2,
    // int numFolds, Random random)
    // throws Exception {
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
    // BinarySMO smo = (BinarySMO)so.getObject();
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

    // /**
    // * sets the kernel to use
    // *
    // * @param value the kernel to use
    // */
    // public void setKernel(Kernel value) {
    // m_kernel = value;
    // }
    //    
    // /**
    // * Returns the kernel to use
    // *
    // * @return the current kernel
    // */
    // public Kernel getKernel() {
    // return m_kernel;
    // }

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
        verbose("Storing the sum of weights...");
        m_sumOfWeights = 0;
        for (int i = 0; i < insts.size(); i++) {
            m_sumOfWeights += insts.getInstance(i).getWeight();
        }

        // Set class values
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
                throw new RuntimeException("This should never happen! A binary SMO can only take 0 and 1 as class values.");
            }
        }

        // Check whether one or both classes are missing
        verbose("Checking for missing classes...");
        if ((m_iUp == -1) || (m_iLow == -1)) {
            if (m_iUp != -1) {
                m_b = -1;
            } else if (m_iLow != -1) {
                m_b = 1;
            } else {
                m_class = null;
                return;
            }
            // if (m_KernelIsLinear) {
            m_sparseWeights = new double[0];
            m_sparseIndices = new int[0];
            m_class = null;
            // } else {
            // m_supportVectors = new SMOset(0);
            // m_alpha = new double[0];
            // m_class = new double[0];
            // }

            // // Fit sigmoid if requested
            // if (fitLogistic) {
            // fitLogistic(insts, cl1, cl2, numFolds, new Random(randomSeed));
            // }
            return;
        }

        // Set the reference to the data
        m_data = insts;

        // If machine is linear, reserve space for weights
        // if (m_KernelIsLinear) {
        m_weights = new double[m_data.size()];
        // } else {
        // m_weights = null;
        // }

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

        // // init kernel
        // m_kernel.buildKernel(m_data);

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
        verbose("Searching for support vectors...");
        int numChanged = 0;
        boolean examineAll = true;
        while ((numChanged > 0) || examineAll) {
//            verbose("Numchanged = "+numChanged+" "+examineAll);
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

        // // Save memory
        // m_kernel.clean();

        m_errors = null;
        m_I0 = m_I1 = m_I2 = m_I3 = m_I4 = null;

        // If machine is linear, delete training data
        // and store weight vector in sparse format
        // if (m_KernelIsLinear) {

        // We don't need to store the set of support vectors
        m_supportVectors = null;

        // We don't need to store the class values either
        m_class = null;

        // // Clean out training data
        // if (!m_checksTurnedOff) {
        // m_data = new Instances(m_data, 0);
        // } else {
        // m_data = null;
        // }

        // Convert weight vector
        double[] sparseWeights = new double[m_weights.length];
        int[] sparseIndices = new int[m_weights.length];
        int counter = 0;
        for (int i = 0; i < m_weights.length; i++) {
            if (m_weights[i] != 0.0) {
                sparseWeights[counter] = m_weights[i];
                sparseIndices[counter] = i;
                counter++;
            }
        }
        m_sparseWeights = new double[counter];
        m_sparseIndices = new int[counter];
        System.arraycopy(sparseWeights, 0, m_sparseWeights, 0, counter);
        System.arraycopy(sparseIndices, 0, m_sparseIndices, 0, counter);

        // Clean out weight vector
        m_weights = null;

        // We don't need the alphas in the linear case
        m_alpha = null;
        // }

        // Fit sigmoid if requested
        // if (fitLogistic) {
        // fitLogistic(insts, cl1, cl2, numFolds, new Random(randomSeed));
        // }

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
    private double SVMOutput(Instance inst){

        double result = 0;

        // Is the machine linear?
        // if (m_KernelIsLinear) {

        // Is weight vector stored in sparse format?
        if (m_sparseWeights == null) {
//            verbose("SVMOutput dense");
            int n1 = inst.size();
            for (int p = 0; p < n1; p++) {
                // if (inst.index(p) != m_classIndex) {
                result += m_weights[p] * inst.getValue(p);
                // }
            }
        } else {
//            verbose("SVMOutput sparse");
//            int n1 = inst.size();// numValues();
            int n2 = m_sparseWeights.length;
            for (int i = 0; i < n2; i++) {
                result += inst.getValue(m_sparseIndices[i]) * m_sparseWeights[i];
            }
            // for (int p1 = 0, p2 = 0; p1 < n1 && p2 < n2;) {
            // int ind1 = inst.index(p1);
            // int ind2 = m_sparseIndices[p2];
            // if (ind1 == ind2) {
            // if (ind1 != m_classIndex) {
            // result += inst.valueSparse(p1) * m_sparseWeights[p2];
            // }
            // p1++;
            // p2++;
            // } else if (ind1 > ind2) {
            // p2++;
            // } else {
            // p1++;
            // }
            // }
        }
        // }
        // else {
        // for (int i = m_supportVectors.getNext(-1); i != -1; i =
        // m_supportVectors.getNext(i)) {
        // result += m_class[i] * m_alpha[i] * m_kernel.eval(index, i, inst);
        // }
        // }
        result -= m_b;

        return result;
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
    private boolean examineExample(int i2){

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
     * @throws Exception
     *             if something goes wrong
     */
    private boolean takeStep(int i1, int i2, double F2)  {

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

        // Update weight vector to reflect change a1 and a2, if linear SVM
        Instance inst1 = m_data.getInstance(i1);
        for (int p1 = 0; p1 < inst1.size(); p1++) {
            m_weights[p1] += y1 * (a1 - alph1) * inst1.getValue(p1);
        }
        Instance inst2 = m_data.getInstance(i2);
        for (int p2 = 0; p2 < inst2.size(); p2++) {
             m_weights[p2] += y2 * (a2 - alph2) * inst2.getValue(p2);
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
