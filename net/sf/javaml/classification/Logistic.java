/**
 * Logistic.java
 *
 * %SVN.HEADER%
 * 
 * Based on work by Xin Xu
 */
package net.sf.javaml.classification;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.utils.ActiveSetsOptimization;
import nz.ac.waikato.cs.weka.Utils;

/**
 * <!-- globalinfo-start --> Class for building and using a multinomial logistic
 * regression model with a ridge estimator.<br/> <br/> There are some
 * modifications, however, compared to the paper of leCessie and van
 * Houwelingen(1992): <br/> <br/> If there are k classes for n instances with m
 * attributes, the parameter matrix B to be calculated will be an m*(k-1)
 * matrix.<br/> <br/> The probability for class j with the exception of the
 * last class is<br/> <br/> Pj(Xi) = exp(XiBj)/((sum[j=1..(k-1)]exp(Xi*Bj))+1)
 * <br/> <br/> The last class has probability<br/> <br/>
 * 1-(sum[j=1..(k-1)]Pj(Xi)) <br/> = 1/((sum[j=1..(k-1)]exp(Xi*Bj))+1)<br/>
 * <br/> The (negative) multinomial log-likelihood is thus: <br/> <br/> L =
 * -sum[i=1..n]{<br/> sum[j=1..(k-1)](Yij * ln(Pj(Xi)))<br/> +(1 -
 * (sum[j=1..(k-1)]Yij)) <br/> * ln(1 - sum[j=1..(k-1)]Pj(Xi))<br/> } + ridge *
 * (B^2)<br/> <br/> In order to find the matrix B for which L is minimised, a
 * Quasi-Newton Method is used to search for the optimized values of the m*(k-1)
 * variables. Note that before we use the optimization procedure, we 'squeeze'
 * the matrix B into a m*(k-1) vector. For details of the optimization
 * procedure, please check weka.core.Optimization class.<br/> <br/> Although
 * original Logistic Regression does not deal with instance weights, we modify
 * the algorithm a little bit to handle the instance weights.<br/> <br/> For
 * more information see:<br/> <br/> le Cessie, S., van Houwelingen, J.C.
 * (1992). Ridge Estimators in Logistic Regression. Applied Statistics.
 * 41(1):191-201.<br/> <br/> Note: Missing values are replaced using a
 * ReplaceMissingValuesFilter, and nominal attributes are transformed into
 * numeric attributes using a NominalToBinaryFilter. <p/> <!-- globalinfo-end
 * -->
 * 
 * <!-- technical-bibtex-start --> BibTeX:
 * 
 * <pre>
 * &#64;article{leCessie1992,
 *    author = {le Cessie, S. and van Houwelingen, J.C.},
 *    journal = {Applied Statistics},
 *    number = {1},
 *    pages = {191-201},
 *    title = {Ridge Estimators in Logistic Regression},
 *    volume = {41},
 *    year = {1992}
 * }
 * </pre>
 * 
 * <p/> <!-- technical-bibtex-end -->
 * 
 * <!-- options-start --> Valid options are: <p/>
 * 
 * <pre> -D
 *  Turn on debugging output.</pre>
 * 
 * <pre> -R &lt;ridge&gt;
 *  Set the ridge in the log-likelihood.</pre>
 * 
 * <pre> -M &lt;number&gt;
 *  Set the maximum number of iterations (default -1, until convergence).</pre>
 * 
 * <!-- options-end -->
 * 
 * @author Xin Xu (xx5@cs.waikato.ac.nz)
 * @version $Revision: 1.37 $
 */
public class Logistic implements Classifier {

    private static final long serialVersionUID = -5428362109088506874L;

    /** The coefficients (optimized parameters) of the model */
    private double[][] m_Par;

    /** The data saved as a matrix */
    private double[][] m_Data;

    /** The number of attributes in the model */
    private int m_NumPredictors;

    // /** The index of the class attribute */
    // protected int m_ClassIndex;

    /** The number of the class labels */
    private int m_NumClasses;

    /** The ridge parameter. */
    private double m_Ridge = 1e-8;

    /** The maximum number of iterations. */
    private int m_MaxIts = -1;

    private class OptEng extends ActiveSetsOptimization {
        /** Weights of instances in the data */
        private double[] weights;

        /** Class labels of instances */
        private int[] cls;

        /**
         * Set the weights of instances
         * 
         * @param w
         *            the weights to be set
         */
        public void setWeights(double[] w) {
            weights = w;
        }

        /**
         * Set the class labels of instances
         * 
         * @param c
         *            the class labels to be set
         */
        public void setClassLabels(int[] c) {
            cls = c;
        }

        /**
         * Evaluate objective function
         * 
         * @param x
         *            the current values of variables
         * @return the value of the objective function
         */
        protected double objectiveFunction(double[] x) {
            double nll = 0; // -LogLikelihood
            int dim = m_NumPredictors + 1; // Number of variables per class

            for (int i = 0; i < cls.length; i++) { // ith instance

                double[] exp = new double[m_NumClasses - 1];
                int index;
                for (int offset = 0; offset < m_NumClasses - 1; offset++) {
                    index = offset * dim;
                    for (int j = 0; j < dim; j++)
                        exp[offset] += m_Data[i][j] * x[index + j];
                }
                double max = exp[Utils.maxIndex(exp)];
                double denom = Math.exp(-max);
                double num;
                if (cls[i] == m_NumClasses - 1) { // Class of this instance
                    num = -max;
                } else {
                    num = exp[cls[i]] - max;
                }
                for (int offset = 0; offset < m_NumClasses - 1; offset++) {
                    denom += Math.exp(exp[offset] - max);
                }

                nll -= weights[i] * (num - Math.log(denom)); // Weighted NLL
            }

            // Ridge: note that intercepts NOT included
            for (int offset = 0; offset < m_NumClasses - 1; offset++) {
                for (int r = 1; r < dim; r++)
                    nll += m_Ridge * x[offset * dim + r] * x[offset * dim + r];
            }

            return nll;
        }

        /**
         * Evaluate Jacobian vector
         * 
         * @param x
         *            the current values of variables
         * @return the gradient vector
         */
        protected double[] evaluateGradient(double[] x) {
            double[] grad = new double[x.length];
            int dim = m_NumPredictors + 1; // Number of variables per class

            for (int i = 0; i < cls.length; i++) { // ith instance
                double[] num = new double[m_NumClasses - 1]; // numerator of
                // [-log(1+sum(exp))]'
                int index;
                for (int offset = 0; offset < m_NumClasses - 1; offset++) { // Which
                    // part
                    // of x
                    double exp = 0.0;
                    index = offset * dim;
                    for (int j = 0; j < dim; j++)
                        exp += m_Data[i][j] * x[index + j];
                    num[offset] = exp;
                }

                double max = num[Utils.maxIndex(num)];
                double denom = Math.exp(-max); // Denominator of
                // [-log(1+sum(exp))]'
                for (int offset = 0; offset < m_NumClasses - 1; offset++) {
                    num[offset] = Math.exp(num[offset] - max);
                    denom += num[offset];
                }
                Utils.normalize(num, denom);

                // Update denominator of the gradient of -log(Posterior)
                double firstTerm;
                for (int offset = 0; offset < m_NumClasses - 1; offset++) { // Which
                    // part
                    // of x
                    index = offset * dim;
                    firstTerm = weights[i] * num[offset];
                    for (int q = 0; q < dim; q++) {
                        grad[index + q] += firstTerm * m_Data[i][q];
                    }
                }

                if (cls[i] != m_NumClasses - 1) { // Not the last class
                    for (int p = 0; p < dim; p++) {
                        grad[cls[i] * dim + p] -= weights[i] * m_Data[i][p];
                    }
                }
            }

            // Ridge: note that intercepts NOT included
            for (int offset = 0; offset < m_NumClasses - 1; offset++) {
                for (int r = 1; r < dim; r++)
                    grad[offset * dim + r] += 2 * m_Ridge * x[offset * dim + r];
            }

            return grad;
        }
    }

    /**
     * Builds the classifier
     * 
     * @param train
     *            the training data to be used for generating the boosted
     *            classifier.
     * @throws Exception
     *             if the classifier could not be built successfully
     */
    public void buildClassifier(Dataset train) {
        m_NumClasses = train.numClasses();

        int nK = m_NumClasses - 1; // Only K-1 class labels needed
        int nR = m_NumPredictors = train.instance(0).size();
        int nC = train.size();

        m_Data = new double[nC][nR + 1]; // Data values
        int[] Y = new int[nC]; // Class labels
        double[] xMean = new double[nR + 1]; // Attribute means
        double[] xSD = new double[nR + 1]; // Attribute stddev's
        double[] sY = new double[nK + 1]; // Number of classes
        double[] weights = new double[nC]; // Weights of instances
        double totWeights = 0; // Total weights of the instances
        m_Par = new double[nR + 1][nK]; // Optimized parameter values

        // if (m_Debug) {
        // System.out.println("Extracting data...");
        // }

        for (int i = 0; i < nC; i++) {
            // initialize X[][]
            Instance current = train.instance(i);
            Y[i] = (int) current.classValue(); // Class value starts from 0
            weights[i] = current.weight(); // Dealing with weights
            totWeights += weights[i];

            m_Data[i][0] = 1;
            int j = 1;
            for (int k = 0; k < nR; k++) {

                double x = current.value(k);
                m_Data[i][j] = x;
                xMean[j] += weights[i] * x;
                xSD[j] += weights[i] * x * x;
                j++;

            }

            // Class count
            sY[Y[i]]++;
        }

        xMean[0] = 0;
        xSD[0] = 1;
        for (int j = 1; j <= nR; j++) {
            xMean[j] = xMean[j] / totWeights;
            if (totWeights > 1)
                xSD[j] = Math.sqrt(Math.abs(xSD[j] - totWeights * xMean[j] * xMean[j]) / (totWeights - 1));
            else
                xSD[j] = 0;
        }

        // Normalise input data
        for (int i = 0; i < nC; i++) {
            for (int j = 0; j <= nR; j++) {
                if (xSD[j] != 0) {
                    m_Data[i][j] = (m_Data[i][j] - xMean[j]) / xSD[j];
                }
            }
        }
        //
        // if (m_Debug) {
        // System.out.println("\nIteration History...");
        // }

        double x[] = new double[(nR + 1) * nK];
        double[][] b = new double[2][x.length]; // Boundary constraints, N/A
        // here

        // Initialize
        for (int p = 0; p < nK; p++) {
            int offset = p * (nR + 1);
            x[offset] = Math.log(sY[p] + 1.0) - Math.log(sY[nK] + 1.0); // Null
            // model
            b[0][offset] = Double.NaN;
            b[1][offset] = Double.NaN;
            for (int q = 1; q <= nR; q++) {
                x[offset + q] = 0.0;
                b[0][offset + q] = Double.NaN;
                b[1][offset + q] = Double.NaN;
            }
        }

        OptEng opt = new OptEng();
        // opt.setDebug(m_Debug);
        opt.setWeights(weights);
        opt.setClassLabels(Y);

        if (m_MaxIts == -1) { // Search until convergence
            x = opt.findArgmin(x, b);
            while (x == null) {
                x = opt.getVarbValues();
                // if (m_Debug)
                // System.out.println("200 iterations finished, not enough!");
                x = opt.findArgmin(x, b);
            }
            // if (m_Debug)
            // System.out.println(" -------------<Converged>--------------");
        } else {
            opt.setMaxIteration(m_MaxIts);
            x = opt.findArgmin(x, b);
            if (x == null) // Not enough, but use the current value
                x = opt.getVarbValues();
        }

//        m_LL = -opt.getMinFunction(); // Log-likelihood

        // Don't need data matrix anymore
        m_Data = null;

        // Convert coefficients back to non-normalized attribute units
        for (int i = 0; i < nK; i++) {
            m_Par[0][i] = x[i * (nR + 1)];
            for (int j = 1; j <= nR; j++) {
                m_Par[j][i] = x[i * (nR + 1) + j];
                if (xSD[j] != 0) {
                    m_Par[j][i] /= xSD[j];
                    m_Par[0][i] -= m_Par[j][i] * xMean[j];
                }
            }
        }
    }

    /**
     * Computes the distribution for a given instance
     * 
     * @param instance
     *            the instance for which distribution is computed
     * @return the distribution
     */
    public double[] distributionForInstance(Instance instance) {

        // m_ReplaceMissingValues.input(instance);
        // instance = m_ReplaceMissingValues.output();
        // m_AttFilter.input(instance);
        // instance = m_AttFilter.output();
        // m_NominalToBinary.input(instance);
        // instance = m_NominalToBinary.output();

        // Extract the predictor columns into an array
        double[] instDat = new double[m_NumPredictors + 1];
        int j = 1;
        instDat[0] = 1;
        for (int k = 0; k < m_NumPredictors; k++) {
            // if (k != m_ClassIndex) {
            instDat[j++] = instance.value(k);
            // }
        }

        double[] distribution = evaluateProbability(instDat);
        return distribution;
    }

    /**
     * Compute the posterior distribution using optimized parameter values and
     * the testing instance.
     * 
     * @param data
     *            the testing instance
     * @return the posterior probability distribution
     */
    private double[] evaluateProbability(double[] data) {
        double[] prob = new double[m_NumClasses], v = new double[m_NumClasses];

        // Log-posterior before normalizing
        for (int j = 0; j < m_NumClasses - 1; j++) {
            for (int k = 0; k <= m_NumPredictors; k++) {
                v[j] += m_Par[k][j] * data[k];
            }
        }
        v[m_NumClasses - 1] = 0;

        // Do so to avoid scaling problems
        for (int m = 0; m < m_NumClasses; m++) {
            double sum = 0;
            for (int n = 0; n < m_NumClasses - 1; n++)
                sum += Math.exp(v[n] - v[m]);
            prob[m] = 1 / (sum + Math.exp(-v[m]));
        }

        return prob;
    }

    public int classifyInstance(Instance instance) {
        double[] distribution = distributionForInstance(instance);
        int index = 0;
        double bestProb = distribution[0];
        for (int i = 1; i < distribution.length; i++) {
            if (distribution[i] > bestProb) {
                bestProb = distribution[i];
                index = i;
            }
        }
        return index;
    }

}
